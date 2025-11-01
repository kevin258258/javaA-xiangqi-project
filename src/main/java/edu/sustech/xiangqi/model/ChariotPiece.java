package edu.sustech.xiangqi.model;

public class ChariotPiece extends AbstractPiece {
    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {

        return false;
    }
    public ChariotPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

}
