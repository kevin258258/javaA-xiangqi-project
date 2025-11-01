package edu.sustech.xiangqi.model;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardModel {
    // 储存棋盘上所有的棋子，要实现吃子的话，直接通过pieces.remove(被吃掉的棋子)删除就可以
    private final List<AbstractPiece> pieces;
    private static final int ROWS = 10;
    private static final int COLS = 9;
    private  boolean isRedTurn = true;
    private boolean isGameOver = false;

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

    public boolean movePiece(AbstractPiece piece, int newRow, int newCol) {
        if (!isValidPosition(newRow, newCol)) {
            return false;
        }

        if (!piece.canMoveTo(newRow, newCol, this)) {
            return false;
        }
         if (getPieceAt(newRow, newCol) != null) {
             pieces.remove(getPieceAt(newRow, newCol));
         }
        piece.moveTo(newRow, newCol);
        return true;
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }
}
