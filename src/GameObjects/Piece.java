package GameObjects;

public abstract class Piece {
	enum Color{
		WHITE,
		BLACK
	}
	
	protected Color color;
    protected boolean stunned; // stun
    
    public Piece(Color c) {
        this.color = c;
        this.stunned = false;
    }
    
    public String getColor() { return color.toString(); }
    public boolean isStunned() { return stunned; }
    public void setStunned(boolean stunned) { this.stunned = stunned; }
    
    /*@Override
    public String toString() {
        return color.charAt(0) + type.substring(0, 1); // Ej: "WP" = White Pawn
    }*/
}

