package Acciones;

public class Action {
    private ActionType type;
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    
    public Action(ActionType type, int fromRow, int fromCol, int toRow, int toCol) {
        this.type = type;
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
    }
    
    // Getters
    public ActionType getType() { return type; }
    public int getFromRow() { return fromRow; }
    public int getFromCol() { return fromCol; }
    public int getToRow() { return toRow; }
    public int getToCol() { return toCol; }
    
    @Override
    public String toString() {
        return type + ": (" + fromRow + "," + fromCol + ") -> (" + toRow + "," + toCol + ")";
    }
}