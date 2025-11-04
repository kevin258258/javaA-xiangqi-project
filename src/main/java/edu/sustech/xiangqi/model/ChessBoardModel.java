package edu.sustech.xiangqi.model;
import java.awt.Point;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardModel {


    // 储存棋盘上所有的棋子，要实现吃子的话，直接通过pieces.remove(被吃掉的棋子)删除就可以
    private final List<AbstractPiece> pieces;
    private static final int ROWS = 10;
    private static final int COLS = 9;
    private  boolean isRedTurn = true;
    private boolean isGameOver = false;
    private String winner;

    //让视图检查是否结束
    public boolean isGameOver() {
        return isGameOver;
    }

    public String getWinner() {
        return winner;
    }

    public boolean isRedTurn() {
        return isRedTurn;
    }

    public ChessBoardModel() {
        pieces = new ArrayList<>();
        initializePieces();
    }

    private void initializePieces() {
        // 黑方棋子

            // 黑方棋子 (isRed = false, 位于棋盘上半部分, row 0-4)
            pieces.add(new ChariotPiece("车", 0, 0, false));
            pieces.add(new HorsePiece("马", 0, 1, false));
            pieces.add(new ElephantPiece("象", 0, 2, false));
            pieces.add(new AdvisorPiece("士", 0, 3, false));
            pieces.add(new GeneralPiece("将", 0, 4, false));
            pieces.add(new AdvisorPiece("士", 0, 5, false));
            pieces.add(new ElephantPiece("象", 0, 6, false));
            pieces.add(new HorsePiece("马", 0, 7, false));
            pieces.add(new ChariotPiece("车", 0, 8, false));

            pieces.add(new CannonPiece("炮", 2, 1, false));
            pieces.add(new CannonPiece("炮", 2, 7, false));

            pieces.add(new SoldierPiece("卒", 3, 0, false));
            pieces.add(new SoldierPiece("卒", 3, 2, false));
            pieces.add(new SoldierPiece("卒", 3, 4, false));
            pieces.add(new SoldierPiece("卒", 3, 6, false));
            pieces.add(new SoldierPiece("卒", 3, 8, false));

            // 红方棋子 (isRed = true, 位于棋盘下半部分, row 5-9)
            pieces.add(new SoldierPiece("兵", 6, 0, true));
            pieces.add(new SoldierPiece("兵", 6, 2, true));
            pieces.add(new SoldierPiece("兵", 6, 4, true));
            pieces.add(new SoldierPiece("兵", 6, 6, true));
            pieces.add(new SoldierPiece("兵", 6, 8, true));

            pieces.add(new CannonPiece("炮", 7, 1, true));
            pieces.add(new CannonPiece("炮", 7, 7, true));

            pieces.add(new ChariotPiece("车", 9, 0, true));
            pieces.add(new HorsePiece("马", 9, 1, true));
            pieces.add(new ElephantPiece("相", 9, 2, true)); // 注意红方的象叫“相”
            pieces.add(new AdvisorPiece("仕", 9, 3, true)); // 注意红方的士叫“仕”
            pieces.add(new GeneralPiece("帅", 9, 4, true));
            pieces.add(new AdvisorPiece("仕", 9, 5, true));
            pieces.add(new ElephantPiece("相", 9, 6, true));
            pieces.add(new HorsePiece("马", 9, 7, true));
            pieces.add(new ChariotPiece("车", 9, 8, true));

    }

    public List<AbstractPiece> getPieces() {
        return pieces;
    }

    public AbstractPiece getPieceAt(int row, int col) {
        for (AbstractPiece piece : pieces) {
            if (piece.getRow() == row && piece.getCol() == col) {
                return piece;
            }
        }
        return null;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    //关于结束提示，之后在写一些gui
    public boolean movePiece(AbstractPiece piece, int newRow, int newCol) {
        if (isGameOver) {
            System.out.println("游戏已结束，无法移动棋子！");
            return false;
        }
        if (piece.isRed() != isRedTurn) {
            return false;
        }

        if (!isValidPosition(newRow, newCol)) {
            return false;
        }

        if (!piece.canMoveTo(newRow, newCol, this)) {
            return false;
        }
         if (getPieceAt(newRow, newCol) != null) {
             if(getPieceAt(newRow, newCol) instanceof GeneralPiece){
                 this.isGameOver = true;
                 this.winner = isRedTurn ? "红方" : "黑方";
                 System.out.println("游戏结束!。胜利者是: " + this.winner);
             }
             pieces.remove(getPieceAt(newRow, newCol));
         }
        piece.moveTo(newRow, newCol);
        if (isGameOver) {
            return false;
        }
        isRedTurn = !isRedTurn;

        // 先检查有没有把自己害死
        if (isCheckMate(!isRedTurn)) {
            this.isGameOver = true;
            this.winner = !isRedTurn ? "黑方" : "红方";

            System.out.println("游戏结束!。胜利者是: " + this.winner);
        }
        else if (isGeneraInCheck(isRedTurn)) {
            // 顺便处理“将军”的提示
            System.out.println("将军!");
        }


        //在检查另一方
        if (isCheckMate(isRedTurn)) {
            this.isGameOver = true;
            this.winner = isRedTurn ? "黑方" : "红方";

            System.out.println("游戏结束!。胜利者是: " + this.winner);
        }
        else if (isGeneraInCheck(isRedTurn)) {
            // 顺便处理“将军”的提示
            System.out.println("将军!");
        }
        return true;
    }
    //将军检测
    public  boolean isGeneraInCheck(Boolean isGeneraRed){
        AbstractPiece king = FindKing(isGeneraRed);
        AbstractPiece enemyKing = FindKing(!isGeneraRed);

        if (king == null) {
            return false;
        }

        if (enemyKing != null && king.getCol() == enemyKing.getCol()) {

            // 如果在同一列，则检查它们之间是否有其他棋子
            int startRow = Math.min(king.getRow(), enemyKing.getRow()) + 1;
            int endRow = Math.max(king.getRow(), enemyKing.getRow());
            boolean hasPieceInBetween = false;
            for (int r = startRow; r < endRow; r++) {
                if (getPieceAt(r, king.getCol()) != null) {
                    hasPieceInBetween = true;
                    break; // 找到了一个子，就可以停止检查了
                }
            }

            // 如果中间没有棋子，则构成“王对王”将军！
            if (!hasPieceInBetween) {
                return true;
            }
        }


        for (AbstractPiece piece : getPieces()) {
            if(piece.isRed() != isGeneraRed) {
                if (piece.canMoveTo(king.getRow(), king.getCol(), this)) {
                    return true;
                }
            }
        }

        return false;
    }
    //将死检测
    public Boolean isCheckMate(Boolean isPlayerRed) {
        AbstractPiece king = FindKing(isPlayerRed);
        AbstractPiece enemyKing = FindKing(!isPlayerRed);

        if (king == null) {
            return false;
        }
        if (!isGeneraInCheck(isPlayerRed)) {
            return false;
        }
        // 检查是否满足王对王的条件
        if (king != null && enemyKing != null && king.getCol() == enemyKing.getCol()) {
            boolean hasPieceInBetween = false;
            int startRow = Math.min(king.getRow(), enemyKing.getRow()) + 1;
            int endRow = Math.max(king.getRow(), enemyKing.getRow());
            for (int r = startRow; r < endRow; r++) {
                if (getPieceAt(r, king.getCol()) != null) {
                    hasPieceInBetween = true;
                    break;
                }
            }

            // 如果确实是王对王将军（中间无子）
            if (!hasPieceInBetween) {
                if (isRedTurn == !isPlayerRed) {
                    return true;
                }
            }
        }
        for (AbstractPiece piece : getPieces()) {
            if (piece.isRed() ==  isPlayerRed ){
                List<Point> legalMoves = piece.getLegalMoves(this);
                for (Point Move : legalMoves) {
                    int OriginalRow = piece.getRow();
                    int OriginalCol = piece.getCol();
                    int TargetRow = Move.y;
                    int TargetCol = Move.x;

                    piece.moveTo(TargetRow, TargetCol);
                    boolean stillInCheck = isGeneraInCheck(isPlayerRed);
                    piece.moveTo(OriginalRow, OriginalCol);
                    if (!stillInCheck) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    AbstractPiece FindKing(boolean isKingRed){
        for (AbstractPiece piece : getPieces()) {
            if (piece instanceof GeneralPiece && piece.isRed() == isKingRed)
                return piece;
        }
        return null;
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }
}
