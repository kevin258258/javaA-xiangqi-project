package edu.sustech.xiangqi.model;
import java.awt.Point;
import java.util.List;

public class HorsePiece extends AbstractPiece {
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

        int rowDiff = Math.abs(targetRow - currentRow);
        int colDiff = Math.abs(targetCol - currentCol);
        if ((rowDiff == 1 && colDiff == 2) || (rowDiff == 2&& colDiff == 1)) {
            return true;
        }

        return false;
    }

    @Override
    public List<Point> getLegalMoves(ChessBoardModel model) {
        return List.of();
    }

    public HorsePiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

}
