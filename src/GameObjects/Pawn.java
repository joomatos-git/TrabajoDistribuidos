package GameObjects;

public class Pawn extends Piece {
    private int code;
    
    public Pawn(Color color, Tile initialTile, int code) {
        super(color, initialTile);
        this.code = code;
    }
    
    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int direction = (this.color == Color.WHITE) ? 1 : -1;
        int rowDiff = end.getRow() - start.getRow();
        int colDiff = Math.abs(end.getCol() - start.getCol());
        
        // MOVE solo puede ser en línea recta hacia adelante
        if (colDiff != 0) {
            return false;
        }
        
        // 1 casilla hacia adelante
        if (rowDiff == direction && end.isEmpty()) {
            return true;
        }
        
        // 2 casillas desde posición inicial
        int startRow = (this.color == Color.WHITE) ? 1 : 6;
        if (start.getRow() == startRow && rowDiff == 2 * direction) {
            int middleRow = start.getRow() + direction;
            return board.getTile(middleRow, start.getCol()).isEmpty() && end.isEmpty();
        }
        
        return false;
    }
    
    @Override
    public boolean canAttack(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int direction = (this.color == Color.WHITE) ? 1 : -1;
        int rowDiff = end.getRow() - start.getRow();
        int colDiff = Math.abs(end.getCol() - start.getCol());
        
        // ATTACK solo en diagonal
        if (rowDiff == direction && colDiff == 1) {
            return true;
        }
        
        return false;
    }
    
    @Override
    protected String getName() {
        return "P" + String.valueOf(code);
    }
}