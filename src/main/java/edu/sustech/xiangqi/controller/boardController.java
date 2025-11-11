package edu.sustech.xiangqi.controller;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import edu.sustech.xiangqi.EntityType;
import edu.sustech.xiangqi.XiangQiApp;
import edu.sustech.xiangqi.view.PieceComponent;
import edu.sustech.xiangqi.view.VisualStateComponent;
import edu.sustech.xiangqi.model.AbstractPiece;
import edu.sustech.xiangqi.model.ChessBoardModel;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static edu.sustech.xiangqi.XiangQiApp.CELL_SIZE;

public class boardController {

    private  ChessBoardModel model;
    private Entity selectedEntity = null;

    public boardController(ChessBoardModel model) {
        this.model = model;

    }

    /**
     * The main entry point for user interaction, called by InputHandler.
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
     * @param pieceEntity The entity that was clicked.
     */
    private void handleSelection(Entity pieceEntity) {
        AbstractPiece logicPiece = pieceEntity.getComponent(PieceComponent.class).getPieceLogic();

        if (logicPiece.isRed() == model.isRedTurn()) {
            this.selectedEntity = pieceEntity;
            this.selectedEntity.getComponent(VisualStateComponent.class).setInactive(); // Darken the selected piece
        }
    }

    /**
     * Handles the logic for deselecting the currently selected piece.
     */
    private void deselectPiece() {
        if (selectedEntity != null) {
            selectedEntity.getComponent(VisualStateComponent.class).setNormal();
            selectedEntity = null;
        }
    }

    private void handleMove(int targetRow, int targetCol) {
        AbstractPiece pieceToMove = selectedEntity.getComponent(PieceComponent.class).getPieceLogic();
        Entity entityToMove = this.selectedEntity; // 先把要移动的实体存起来


        Point2D startPosition = entityToMove.getPosition();

        // 在移动前，检查目标位置是否有棋子
        Entity capturedEntity = findEntityAt(targetRow, targetCol);

        // 尝试在模型中移动
        boolean moveSuccess = model.movePiece(pieceToMove, targetRow, targetCol);

        if (moveSuccess) {
            // --- 视图更新 ---

            // 1. 处理吃子
            if (capturedEntity != null) {
                capturedEntity.removeFromWorld();
            }

            // 2. 计算出最终的视觉位置
            Point2D targetPosition = XiangQiApp.getVisualPosition(targetRow, targetCol);

            // 3. 【！！！绝对核心的修正！！！】
            // 在播放动画之前，立刻、强制性地将实体的位置更新到目标点。
            // 这确保了实体的 Position 属性与它组件内的逻辑棋子的 (row, col) 属性永远同步。
            entityToMove.setPosition(targetPosition);

            animationBuilder()
                    .duration(Duration.seconds(0.2))
                    .translate(entityToMove)
                    .from(startPosition)  // <-- 从我们记录的旧位置开始
                    .to(targetPosition)   // <-- 移动到它现在所在的新位置
                    .buildAndPlay();


            // 5. 检查游戏是否结束
            updateTurnIndicator();


            checkGameOver();

        }

        // 无论移动成功与否，都取消选择
        deselectPiece();
    }

    /**
     * 【新增】更新回合指示器文本和颜色的方法
     */
    public void updateTurnIndicator() {
        // 通过 FXGL.getAppCast() 获取到主程序实例，然后调用 getter
        Text indicator = ((XiangQiApp) FXGL.getApp()).getTurnIndicatorText();

        if (model.isGameOver()) {
            indicator.setText("游戏结束");
            indicator.setFill(Color.GRAY);
        } else if (model.isRedTurn()) {
            indicator.setText("轮到 红方");
            indicator.setFill(Color.RED);
        } else {
            indicator.setText("轮到 黑方");
            indicator.setFill(Color.BLACK);
        }
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
}