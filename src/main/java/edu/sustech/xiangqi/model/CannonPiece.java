package edu.sustech.xiangqi.model;

public class CannonPiece extends AbstractPiece{
    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        return false;
    }

    public CannonPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

}
