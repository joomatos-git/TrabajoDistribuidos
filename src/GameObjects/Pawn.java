package GameObjects;

public class Pawn extends Piece {
    
	private int code;
	
    public Pawn(Color color, int c) {
    	super(color);
    	this.code = c;
    }
    
    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        // No autocapturas
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int direction = (this.color == Color.WHITE) ? 1 : -1;
        int rowDiff = end.getRow() - start.getRow();
        int colDiff = Math.abs(end.getCol() - start.getCol());
        
        // linea recta hacia delante
        if (colDiff != 0) {
            return false; // No puede moverse en diagonal
        }
        
        // 1 casilla (no inicio)
        if (rowDiff == direction && end.isEmpty()) {
            return true;
        }
        
        // 2 casillas (solo inicio)
        int startRow = (this.color == Color.WHITE) ? 1 : 6;
        if (start.getRow() == startRow && rowDiff == 2 * direction) {
            int middleRow = start.getRow() + direction;
            return board.getTile(middleRow, start.getCol()).isEmpty() && end.isEmpty();
        }
        
        return false;
    }
    
    @Override
    public boolean canAttack(Board board, Tile start, Tile end) {
        // No puedes atacar tu propia pieza
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int direction = (this.color == Color.WHITE) ? 1 : -1;
        int rowDiff = end.getRow() - start.getRow();
        int colDiff = Math.abs(end.getCol() - start.getCol());
        
        // ATTACK solo puede ser en diagonal (1 casilla adelante, 1 a los lados)
        if (rowDiff == direction && colDiff == 1) {
            return true; // Puede atacar casillas vacías
        }
        
        return false;
    }
    
    @Override
    protected String getName() {
        return 'P'+String.valueOf(code);
    }
}