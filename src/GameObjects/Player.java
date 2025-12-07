package GameObjects;

import Acciones.Action;

public class Player {
    private String name;
    private Color color;
    private Action pendingAction;
    private boolean isReady;
    
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.pendingAction = null;
        this.isReady = false;
    }
    
    public void submitAction(Action action) {
        this.pendingAction = action;
        this.isReady = true;
    }
    
    public Action getPendingAction() {
        return pendingAction;
    }
    
    public void clearAction() {
        this.pendingAction = null;
        this.isReady = false;
    }
    
    public boolean isReady() {
        return isReady;
    }
    
    public String getName() {
        return name;
    }
    
    public Color getColor() {
        return color;
    }
    
    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}