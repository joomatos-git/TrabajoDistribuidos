package GameObjects;

public class Rook extends Piece {
    private int code;
    private boolean hasMoved = false; //para comprobar si se puede enrocar
	
	public Rook(Color color, int c) {
        super(color);
        this.code=c;
    }
    
	@Override
    public boolean canMove(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        // Debe moverse en línea recta (horizontal o vertical)
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
        return 'R'+String.valueOf(code);
    }


	
}