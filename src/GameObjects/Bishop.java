package GameObjects;

public class Bishop extends Piece {
	private int code;
    public Bishop(Color color,int c) {
        super(color);
        this.code = c;
    }

    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int rowDiff = Math.abs(end.getRow() - start.getRow());
        int colDiff = Math.abs(end.getCol() - start.getCol());
        
        // diagonal
        if (rowDiff != colDiff) {
            return false;
        }
        
        return isPathClear(board, start, end);
    }
    
    
	
	@Override
	protected String getName() {
        return 'B'+String.valueOf(code);
    }
}