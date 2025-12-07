package GameObjects;

public class Bishop extends Piece {
    private int code;
    
    public Bishop(Color color, Tile initialTile, int code) {
        super(color, initialTile);
        this.code = code;
    }
    
    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int rowDiff = Math.abs(end.getRow() - start.getRow());
        int colDiff = Math.abs(end.getCol() - start.getCol());
        
        // Diagonal
        if (rowDiff != colDiff) {
            return false;
        }
        
        return isPathClear(board, start, end);
    }
    
    @Override
    protected String getName() {
        return "B" + String.valueOf(code);
    }
}