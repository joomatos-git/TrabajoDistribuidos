package GameObjects;

public class Rook extends Piece {
    private int code;
    private boolean hasMoved = false; //para enrocar y tal
    
    public Rook(Color color, Tile initialTile, int code) {
        super(color, initialTile);
        this.code = code;
    }
    
    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        // Línea recta 
        if (start.getRow() != end.getRow() && start.getCol() != end.getCol()) {
            return false;
        }
        
        return isPathClear(board, start, end);
    }
    
    public boolean hasMoved() {
        return hasMoved;
    }
    
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    
    @Override
    protected String getName() {
        return "R" + String.valueOf(code);
    }
}