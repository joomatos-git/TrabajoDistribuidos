package GameObjects;

public class King extends Piece {
    private boolean hasMoved = false;
    
    public King(Color color) {
        super(color);
    }
    
    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        if (!end.isEmpty() && end.getPiece().getColor() == this.color) {
            return false;
        }
        
        int rowDiff = Math.abs(start.getRow() - end.getRow());
        int colDiff = Math.abs(start.getCol() - end.getCol());
        
        // Movimiento normal
        if (rowDiff <= 1 && colDiff <= 1) {
            return true;
        }
        
        // Enroque 
        return isValidCastling(board, start, end);
    }
    
    private boolean isValidCastling(Board board, Tile start, Tile end) {
        if (hasMoved) {
            return false;
        }
        
        // solo misma fila aunq es un poco redundante por hasMoved, pero prefiero curarme en salud
        if (start.getRow() != end.getRow()) {
            return false;
        }
        
        int colDiff = end.getCol() - start.getCol();
        
        // Enroque corto (hacia la derecha)
        if (colDiff == 2) {
            Tile rookTile = board.getTile(start.getRow(), 7);
            if (rookTile.isEmpty() || !(rookTile.getPiece() instanceof Rook)) {
                return false;
            }
            Rook rook = (Rook) rookTile.getPiece();
            if (rook.hasMoved()) {
                return false;
            }
            // Verificar que las casillas intermedias estén vacías
            return board.getTile(start.getRow(), 5).isEmpty() &&
                   board.getTile(start.getRow(), 6).isEmpty();
        }
        
        // Enroque largo (hacia la izquierda)
        if (colDiff == -2) {
            Tile rookTile = board.getTile(start.getRow(), 0);
            if (rookTile.isEmpty() || !(rookTile.getPiece() instanceof Rook)) {
                return false;
            }
            Rook rook = (Rook) rookTile.getPiece();
            if (rook.hasMoved()) {
                return false;
            }
            return board.getTile(start.getRow(), 1).isEmpty() &&
                   board.getTile(start.getRow(), 2).isEmpty() &&
                   board.getTile(start.getRow(), 3).isEmpty();
        }
        
        return false;
    }
    
    public boolean hasMoved() {
        return hasMoved;
    }
    
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
    
    @Override
    protected String getName() {
        return "K";
    }

}