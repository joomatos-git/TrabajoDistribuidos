package Acciones;

import GameObjects.Tile;

public class Action {
    private ActionType type;
    private Tile origin;
    private Tile destination;
    
    
    public Action(ActionType t, Tile o, Tile d) {
    	this.type=t;
    	this.origin=o;
    	this.destination=d;
    }
    
    // Getters
    public ActionType getType() { return type; }
    public Tile getOrigin() { return origin; }
    public Tile getDestination() { return destination; }
    
    
    @Override
    public String toString() {
        return type + ": " + origin.toString() + "->" + destination.toString();
    }
}