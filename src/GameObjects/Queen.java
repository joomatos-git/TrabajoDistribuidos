package GameObjects;

public class Queen extends Piece {
    public Queen(Color color) {
        super(color);
    }
    
    
    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int rowDiff = Math.abs(end.getRow() - start.getRow());
        int colDiff = Math.abs(end.getCol() - start.getCol());
        
        // Movimiento de torre (línea recta)
        if (start.getRow() == end.getRow() || start.getCol() == end.getCol()) {
            return isPathClear(board, start, end);
        }
        
        // Movimiento de alfil (diagonal)
        if (rowDiff == colDiff) {
            return isPathClear(board, start, end);
        }
        
        return false;
    }
    
    @Override
	protected String getName() {
        return "Q";
    }


	
}