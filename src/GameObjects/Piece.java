package GameObjects;

public abstract class Piece {
	
	
	
	enum Color{
		WHITE,
		BLACK
	}
	
    protected Color color;
    protected boolean stunned;
    
    public Piece(Color color) {
        this.color = color;
        this.stunned = false;
    }
    
    // Método para validar movimiento
    public abstract boolean canMove(Board board, Tile start, Tile end);
    
    // Método para validar ataque (por defecto, igual que moverse. Solo existe para los peones.)
    public boolean canAttack(Board board, Tile start, Tile end) {
        return canMove(board, start, end);
    }
    
    // Método para verificar si el camino está despejado segun el movimiento que han elegido (para torre, alfil, reina)
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
    
    @Override
    public String toString() {
        char colorChar = (color == Color.WHITE) ? 'W' : 'B';
        return "" + colorChar + getName();
    }
    
    // Cada pieza retorna su símbolo (Peon = P, Caballo = N(kNight)...) y un numero, para luego poder identificar cual mueves y tal si lo hago por consola. Los peones seran P1-P8 y asi
    protected abstract String getName();
}
