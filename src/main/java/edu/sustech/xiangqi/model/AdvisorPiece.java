package edu.sustech.xiangqi.model;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AdvisorPiece extends AbstractPiece{
    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = this.getRow();
        int currentCol = this.getCol();
        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        if (targetPiece != null && targetPiece.isRed() == this.isRed()) {
            return false;
        }


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

    @Override
    public List<Point> getLegalMoves(ChessBoardModel model) {
        return List.of();
    }


    public AdvisorPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

}
