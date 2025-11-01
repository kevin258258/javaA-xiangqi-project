package edu.sustech.xiangqi.model;

public class CannonPiece extends AbstractPiece {

    public CannonPiece(String name, int row, int col, boolean isRed) {
        super(name, row, col, isRed);
    }

    @Override
    public boolean canMoveTo(int targetRow, int targetCol, ChessBoardModel model) {
        int currentRow = this.getRow();
        int currentCol = this.getCol();
        if (currentRow == targetRow && currentCol == targetCol) {
            return false;
        }
        if (getRow() == targetRow && getCol() == targetCol) {
            return false;
        }


        AbstractPiece targetPiece = model.getPieceAt(targetRow, targetCol);
        if (targetPiece != null && targetPiece.isRed() == this.isRed()) {
            return false;
        }


        if (targetRow != currentRow && targetCol != currentCol) {
            return false;
        }


        int piecesInPath = countPiecesInPath(currentRow, currentCol, targetRow, targetCol, model);

        if (targetPiece == null) {

            return piecesInPath == 0;
        } else {

            return piecesInPath == 1;
        }
    }


    private int countPiecesInPath(int r1, int c1, int r2, int c2, ChessBoardModel model) {
        int count = 0;
        if (r1 == r2) { // 横向移动
            int start = Math.min(c1, c2) + 1;
            int end = Math.max(c1, c2);
            for (int c = start; c < end; c++) {
                if (model.getPieceAt(r1, c) != null) {
                    count++;
                }
            }
        } else { // 纵向移动
            int start = Math.min(r1, r2) + 1;
            int end = Math.max(r1, r2);
            for (int r = start; r < end; r++) {
                if (model.getPieceAt(r, c1) != null) {
                    count++;
                }
            }
        }
        return count;
    }
}