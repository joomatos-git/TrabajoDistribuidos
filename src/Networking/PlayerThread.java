package Networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CyclicBarrier;

import Acciones.Action;
import Acciones.ActionType;
import GameObjects.Board;
import GameObjects.Game;
import GameObjects.Piece;
import GameObjects.Tile;


public class PlayerThread implements Runnable {
    private Socket player;
    private CyclicBarrier barrier;
    private int id;
    private String[] names;
    private ObjectOutputStream out;
    private Action[] actionPerformed;
    private Game game;
    
    public PlayerThread(Socket socket, Game game, CyclicBarrier barrier, int i, 
                       String[] names, ObjectOutputStream out, Action[] action) {
        this.player = socket;
        this.barrier = barrier;
        this.id = i;
        this.names = names;
        this.out = out;
        this.actionPerformed = action;
        this.game = game;
    }
    
    @Override
    public void run() {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(player.getInputStream());
            
            // nombre
            Object msg = in.readObject();
            names[id] = (String) msg;
            
            barrier.await(); // Ambos tienen nombre
            barrier.await(); // Game enviado
            
            // Loop del juego
            while (game.getState() != Game.GameState.FINISHED) {
                barrier.await(); // Turno empieza
                
                // acción 
                Action clientAction = (Action) in.readObject();
                
                // TRADUCIR: convertir a Action con objetos del servidor, en vez de con los de la copia que no estan aqui.
                Action serverAction = translateAction(clientAction, game.getBoard());
                
                if (serverAction == null) {
                    System.out.println("Jugador " + (id + 1) + " envió acción inválida");
                }
                
                actionPerformed[id] = serverAction;
                
                barrier.await(); // acciones recibidas
                barrier.await(); // resuelto
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
    }
    
    // necesario pq sino no se cambiaban las piezas ya que pieza era del board serializado entonces se liaba un poco la cosa.
    private Action translateAction(Action clientAction, Board serverBoard) {
        try {
            Piece clientPiece = clientAction.getPiece();
            Tile clientDestination = clientAction.getDestination();
            ActionType type = clientAction.getType();
            
            int originRow = clientPiece.getCurrentTile().getRow();
            int originCol = clientPiece.getCurrentTile().getCol();
            int destRow = clientDestination.getRow();
            int destCol = clientDestination.getCol();
            
            Tile serverOrigin = serverBoard.getTile(originRow, originCol);
            if (serverOrigin.isEmpty()) {
                return null;
            }
            
            Piece serverPiece = serverOrigin.getPiece();
            Tile serverDestination = serverBoard.getTile(destRow, destCol);
            
            return new Action(type, serverPiece, serverDestination);
            
        } catch (Exception e) {
            System.err.println("Error traduciendo acción: " + e.getMessage());
            return null;
        }
    }
    
    
}