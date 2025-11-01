package edu.sustech.xiangqi.model;

public class AdvisorPiece extends AbstractPiece{
    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
            int changeX = Math.abs(targetRow - getRow());
            int changeY = Math.abs(targetCol - getCol());
            if (changeX != 1 || changeY != 1) {
                return false;
            }
            if (isRed()) {
                if (targetRow < 7 || targetCol < 3 || targetCol > 5) {
                    return false;
                }
                return  true;
            }
                if (targetRow > 2 ||  targetCol < 3 || targetCol > 5) {
                    return false;
                }

        return true;
    }

    public AdvisorPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

}
