package edu.sustech.xiangqi.model;
import java.awt.Point;
import java.util.List;

public class ElephantPiece extends AbstractPiece {
    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        if (targetPiece != null && targetPiece.isRed() == this.isRed()) {
            return false;
        }

        int currentRow = this.getRow();
        int currentCol = this.getCol();
        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }

        int rowDiff = Math.abs(targetRow - currentRow);
        int colDiff = Math.abs(targetCol - currentCol);

        if (rowDiff != colDiff) {
            return false;
        }
        if(isRed()){
            return 5 <= targetRow && targetRow <= 9;
        }
        else {
            return 0 <= targetRow && targetRow <= 4;
        }

    }

    @Override
    public List<Point> getLegalMoves(ChessBoardModel model) {
        return List.of();
    }

    public ElephantPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }
}
