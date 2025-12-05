package GameObjects;

public class Tile {
    private int row;
    private int col;
    private Piece piece;  // null si está vacía
    
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
        this.piece = null;
    }
    
    public boolean isEmpty() {
        return piece == null;
    }
    
    // Getters y setters
    public Piece getPiece() { return piece; }
    public void setPiece(Piece piece) { this.piece = piece; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    
    
    @Override
    public String toString() {
    	return getCharForNumber(col)+row;
    }
    
    
    
    private String getCharForNumber(int i) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        if (i > 25) {
            return null;
        }
        return Character.toString(alphabet[i]);
    }

}
