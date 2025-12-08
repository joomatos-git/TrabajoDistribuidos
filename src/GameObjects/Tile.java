package GameObjects;

import java.io.Serializable;

public class Tile implements Serializable{

	private static final long serialVersionUID = 1L;
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
    
    // hecho con sincronización bidireccional porque sino me volvia loco
    public void setPiece(Piece piece) {
        if (this.piece != null && this.piece != piece) {
            this.piece.currentTile = null;
        }
        
        if (piece != null && piece.currentTile != this) {
            Tile oldTile = piece.currentTile;
            if (oldTile != null) {
                oldTile.piece = null;
            }
            piece.currentTile = this;
        }
        
        this.piece = piece;
    }
    
    public int getRow() { return row; }
    public int getCol() { return column; }
    
    @Override
    public String toString() {
        return getCharForNumber(column) + (row+1);
    }
    
    private String getCharForNumber(int i) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        if (i > 25) {
            return null;
        }
        return Character.toString(alphabet[i]);
    }
}