package edu.sustech.xiangqi.controller;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import edu.sustech.xiangqi.EntityType;
import edu.sustech.xiangqi.XiangQiApp;
import edu.sustech.xiangqi.view.PieceComponent;
import edu.sustech.xiangqi.view.VisualStateComponent;
import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.Light;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;


import java.awt.*;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.xiangqi.XiangQiApp.CELL_SIZE;

public class boardController {

    private ChessBoardModel model;
    private Entity selectedEntity = null;



    public boardController(ChessBoardModel model) {
        this.model = model;

    }

    /**
     * The main entry point for user interaction, called by InputHandler.
     *
     * @param row the logical row of the click
     * @param col the logical col of the click
     */
    public void onGridClicked(int row, int col) {
        if (model.isGameOver()) {
            return;
        }

        Entity clickedEntity = findEntityAt(row, col);

        // Case 1: A piece is already selected
        if (selectedEntity != null) {

            // If the player clicks the same piece again, deselect it.
            if (clickedEntity == selectedEntity) {
                deselectPiece();

                return;
            }

            // This is a move attempt.
            handleMove(row, col);

            // Case 2: No piece is selected
        } else {
            // If the clicked spot has a piece, select it.
            if (clickedEntity != null) {
                handleSelection(clickedEntity);
            }
        }
    }



    /**
     * Handles the logic for selecting a piece.
     *
     * @param pieceEntity The entity that was clicked.
     */
    private void handleSelection(Entity pieceEntity) {
        AbstractPiece logicPiece = pieceEntity.getComponent(PieceComponent.class).getPieceLogic();

        if (logicPiece.isRed() == model.isRedTurn()) {
            this.selectedEntity = pieceEntity;
            this.selectedEntity.getComponent(VisualStateComponent.class).setInactive(); // Darken the selected piece
            showLegalMoves(logicPiece);

        }

    }

    /**
     * Handles the logic for deselecting the currently selected piece.
     */
    private void deselectPiece() {
        if (selectedEntity != null) {
            selectedEntity.getComponent(VisualStateComponent.class).setNormal();
            selectedEntity = null;
            clearMoveIndicators();

        }
    }

    private void handleMove(int targetRow, int targetCol) {
        AbstractPiece pieceToMove = selectedEntity.getComponent(PieceComponent.class).getPieceLogic();
        Entity entityToMove = this.selectedEntity;
        Point2D startPosition = entityToMove.getPosition();
        Entity capturedEntity = findEntityAt(targetRow, targetCol);

        boolean moveSuccess = model.movePiece(pieceToMove, targetRow, targetCol);

        if (moveSuccess) {
            playMoveAndEndGameAnimation(entityToMove, capturedEntity, startPosition, targetRow, targetCol);
        }

        deselectPiece();
    }


    private void playMoveAndEndGameAnimation(Entity entityToMove, Entity capturedEntity, Point2D startPos, int targetRow, int targetCol) {
        Point2D targetPosition = XiangQiApp.getVisualPosition(targetRow, targetCol);
        entityToMove.setPosition(targetPosition);
        boolean willBeGameOver = model.isGameOver();

        animationBuilder()
                .duration(Duration.seconds(0.2))
                .translate(entityToMove)
                .from(startPos)
                .to(targetPosition)
                .buildAndPlay();

        // 2. 【关键】与此同时，启动一个一次性的定时器，在动画结束后执行后续操作
        //    我们给定时器设置的时间比动画稍长一点（例如0.25秒），以确保动画完全播放完毕。
        runOnce(() -> {
            // 这里的代码会在 0.25 秒后执行
            if (willBeGameOver) {
                if (capturedEntity != null) {
                    capturedEntity.removeFromWorld();
                }
                showGameOverBanner();
            } else {
                if (capturedEntity != null) {
                    capturedEntity.removeFromWorld();
                }
                updateTurnIndicator();
            }
        }, Duration.seconds(0.25));
    }




    private void showGameOverBanner() {
        XiangQiApp app = getAppCast();
        Text banner = app.getGameOverBanner();
        Rectangle dimmingRect = app.getGameOverDimmingRect();

        // 1. 准备报幕文字内容
        banner.setText(model.getWinner() + " 胜！");
        app.centerTextInApp(banner);

        // 2. 【新增】播放全场变暗的动画
        dimmingRect.setVisible(true);
        // 3. 【关键】使用 runOnce 定时器，在变暗动画结束后执行下一阶段
        runOnce(() -> {
            // 这段代码将在 0.5 秒后执行

            // a. 准备报幕文字的初始状态
            banner.setScaleX(0);
            banner.setScaleY(0);
            banner.setVisible(true);

            // b. 播放报幕文字的出现动画
            animationBuilder()
                    .duration(Duration.seconds(0.5))
                    .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                    .scale(banner)
                    .to(new Point2D(1.0, 1.0))
                    .buildAndPlay(); // 干净地调用

        }, Duration.seconds(0.5)); // 定时器的延迟时间和第一段动画的时长完全一样

        // 4. 更新回合指示器为“游戏结束”
        updateTurnIndicator();
    }

    /**
     * 【新增】更新回合指示器文本和颜色的方法
     */
    public void updateTurnIndicator() {
        // 1. 获取主 App 实例和我们的 TurnIndicator 组件
        XiangQiApp app = getAppCast();
        var indicator = app.getTurnIndicator();

        // 2. 调用组件的 update 方法，传入当前状态
        indicator.update(model.isRedTurn(), model.isGameOver());
    }

    /**
     * 【新增】处理玩家投降的逻辑
     */
    public void surrender() {
        // 如果游戏已经结束，就什么都不做
        if (model.isGameOver()) {
            return;
        }

        // 1. 在模型中设置游戏结束状态
        // 投降的是当前回合方，所以胜利者是对方
        model.endGame(model.isRedTurn() ? "黑方" : "红方");

        // 2. 显示酷炫的胜利报幕
        showGameOverBanner();
    }

    /**
     * Checks if the game is over and shows a dialog box if it is.
     */
    private void checkGameOver() {
        if (model.isGameOver()) {
            updateTurnIndicator(); // 确保文本变为“游戏结束”
            String winner = model.getWinner();
            String message = "游戏结束！\n胜利者是: " + winner + "\n\n是否开始新的一局？";

            getDialogService().showConfirmationBox(message, yes -> {
                if (yes) {
                    getGameController().startNewGame();
                } else {
                    getGameController().exit();
                }
            });
        }
    }

    /**
     * Finds the entity at a given logical grid position.
     * @param row the logical row
     * @param col the logical column
     * @return the entity at that position, or null if none is found.
     */
    private Entity findEntityAt(int row, int col) {
        Point2D topLeft = XiangQiApp.getVisualPosition(row, col);
        // The size of the search area should match the size of the piece's view
        double pieceSize = CELL_SIZE - 8; // 8 is the padding we used in the factory
        Rectangle2D selectionRect = new Rectangle2D(topLeft.getX(), topLeft.getY(), pieceSize, pieceSize);

        return getGameWorld().getEntitiesInRange(selectionRect)
                .stream()
                .filter(e -> e.isType(EntityType.PIECE))
                .findFirst()
                .orElse(null);
    }

    /**
     * 【新增】处理悔棋请求的公共方法
     */
    public void undo() {
        boolean undoSuccess = model.undoMove();

        if (undoSuccess) {
            getGameWorld().getEntitiesByType(EntityType.PIECE).forEach(Entity::removeFromWorld);

            for (AbstractPiece pieceLogic : model.getPieces()) {

                // --- 【这里是构建 ID 的具体代码】 ---
                String colorPrefix = pieceLogic.isRed() ? "Red" : "Black";
                String pieceTypeName = pieceLogic.getClass().getSimpleName().replace("Piece", "");
                String entityID = colorPrefix + pieceTypeName;
                // ------------------------------------

                Point2D visualPos = XiangQiApp.getVisualPosition(pieceLogic.getRow(), pieceLogic.getCol());
                spawn(entityID, new SpawnData(visualPos).put("pieceLogic", pieceLogic));
            }

            updateTurnIndicator();
            deselectPiece();
        }
    }

    private void clearMoveIndicators() {
        getGameWorld().getEntitiesByType(EntityType.MOVE_INDICATOR)
                .forEach(Entity::removeFromWorld);
    }


    private void showLegalMoves(AbstractPiece piece) {
        // 先清理旧的
        clearMoveIndicators();

        // 获取所有合法走法
        List<Point> moves = piece.getLegalMoves(model);

        for (Point p : moves) {
            Point2D pos = XiangQiApp.getVisualPosition(p.y, p.x);
            spawn("MoveIndicator", pos);
        }
    }
}
