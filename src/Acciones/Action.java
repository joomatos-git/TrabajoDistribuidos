package Acciones;

import java.io.Serializable;

import GameObjects.Piece;
import GameObjects.Tile;

public class Action implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private ActionType type;
    private Piece piece;
    private Tile destination;
    
    public Action(ActionType type, Piece piece, Tile destination) {
        this.type = type;
        this.piece = piece;
        this.destination = destination;
    }
    
    public ActionType getType() { return type; }
    public Piece getPiece() { return piece; }
    public Tile getDestination() { return destination; }
    
    @Override
    public String toString() {
        return type + ": " + piece + " -> " + destination.toString();
    }
}