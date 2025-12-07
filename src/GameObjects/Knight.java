package GameObjects;

public class Knight extends Piece {
    private int code;
    
    public Knight(Color color, Tile initialTile, int code) {
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
        
        // para la 'L': 2+1 o 1+2
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    @Override
    protected String getName() {
        return "N" + String.valueOf(code);
    }
}