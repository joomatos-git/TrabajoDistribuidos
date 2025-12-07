package GameObjects;

public class Tile {
    private int row;
    private int column;
    private Piece piece;
    
    public Tile(int row, int col) {
        this.row = row;
        this.column = col;
        this.piece = null;
    }
    
    public boolean isEmpty() {
        Piece p = this.getPiece();
        return p == null;
    }
    
    public Piece getPiece() {
        if (piece != null && !piece.isAlive()) {
            return null; // piezas muertas como casilla vacía
        }
        return piece;
    }
    
    void placePieceDirectly(Piece piece) {
        this.piece = piece;
    }
    
    // Establece una pieza (para movimientos) con sincronización bidireccional
    public void setPiece(Piece piece) {
        // 1. Limpiar la pieza anterior de esta casilla
        if (this.piece != null && this.piece != piece) {
            this.piece.currentTile = null;
        }
        
        // Limpiar la casilla anterior de la nueva pieza
        if (piece != null && piece.currentTile != this) {
            Tile oldTile = piece.currentTile;
            if (oldTile != null) {
                oldTile.piece = null;
            }
            piece.currentTile = this;
        }
        
        // 3. Establecer la nueva relación
        this.piece = piece;
    }
    
    public int getRow() { return row; }
    public int getCol() { return column; }
    
    @Override
    public String toString() {
        return getCharForNumber(column) + row;
    }
    
    private String getCharForNumber(int i) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        if (i > 25) {
            return null;
        }
        return Character.toString(alphabet[i]);
    }
}