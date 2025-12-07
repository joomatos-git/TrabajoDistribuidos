package GameObjects;

public abstract class Piece {
    
    protected Color color;
    protected boolean stunned;
    protected boolean alive;
    Tile currentTile; // Package-private para sincronización con Tile
    
    public Piece(Color color, Tile initialTile) {
        this.color = color;
        this.stunned = false;
        this.alive = true;
        this.currentTile = initialTile;
        
        if (initialTile != null) {
            initialTile.placePieceDirectly(this);
        }
    }
    
    // movimiento
    public abstract boolean canMove(Board board, Tile start, Tile end);
    
    // ataque (por defecto igual que moverse, excepto para peones)
    public boolean canAttack(Board board, Tile start, Tile end) {
        return canMove(board, start, end);
    }
    
    protected boolean isPathClear(Board board, Tile start, Tile end) {
        int rowStart = start.getRow();
        int colStart = start.getCol();
        int rowEnd = end.getRow();
        int colEnd = end.getCol();
        
        int rowStep = Integer.compare(rowEnd, rowStart);
        int colStep = Integer.compare(colEnd, colStart);
        
        int currentRow = rowStart + rowStep;
        int currentCol = colStart + colStep;
        
        while (currentRow != rowEnd || currentCol != colEnd) {
            if (!board.getTile(currentRow, currentCol).isEmpty()) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        
        return true;
    }
    
    public Color getColor() { return color; }
    public boolean isStunned() { return stunned; }
    public void setStunned(boolean stunned) { this.stunned = stunned; }
    public boolean isAlive() { return alive; }
    
    public void kill(Board board) {
        this.alive = false;
        if (this.currentTile != null) {
            this.currentTile.setPiece(null);
        }
        board.capturePiece(this); 
    }
    
    
    public Tile getCurrentTile() {
        return currentTile;
    }
    
    @Override
    public String toString() {
        char colorChar = (color == Color.WHITE) ? 'W' : 'B';
        return "" + colorChar + getName();
    }
    
    protected abstract String getName();
}