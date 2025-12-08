package Networking;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import Acciones.Action;
import GameObjects.Game;
import Logic.TurnResolver;
import XML.XMLGameManager;

/**
 * Thread super simple del juego.
 */
public class GameThread implements Runnable {
    private final Socket[] clients;
    private CyclicBarrier barrier = new CyclicBarrier(3);
    private String[] names = new String[2];
    private Action[] thisTurn = new Action[2];
    private Game game;
    private ObjectOutputStream[] out = new ObjectOutputStream[2];
    private List<Action[]> gameHistory = new ArrayList<>(); //para almacenar en el XML un registro de partidas
    
    public GameThread(Socket[] clients) {
        this.clients = clients;
    }
    
    @Override
    public void run() {
        try {
            out[0] = new ObjectOutputStream(clients[0].getOutputStream());
            out[1] = new ObjectOutputStream(clients[1].getOutputStream());
            
            game = new Game("P1", "P2");
            
            // Iniciar PlayerThreads
            new Thread(new PlayerThread(clients[0], game, barrier, 0, names, out[0], thisTurn)).start();
            new Thread(new PlayerThread(clients[1], game, barrier, 1, names, out[1], thisTurn)).start();
            
            // Pedir nombres
            sendBoth("SEND_NAME");
            barrier.await();
            
            // Configurar juego
            game.setNames(names[0], names[1]);
            sendBoth("Players: " + names[0] + " vs " + names[1]);
            sendBoth("GAME_START");
            sendBoth(game.getBoard());
            
            barrier.await();
            
            // juego
            while (game.getState() != Game.GameState.FINISHED) {
                // Enviar turno
                sendBoth("YOUR_TURN");
                
                out[0].reset();
                out[1].reset();
                sendBoth(game.getBoard());
                
                barrier.await(); // Inicio de turno
                barrier.await(); // Acciones recibidas
                
                // debug 
                System.out.println("Acción P1: " + thisTurn[0]);
                System.out.println("Acción P2: " + thisTurn[1]);
                
                // Resolver turno
                TurnResolver.resolveTurn(game.getBoard(), thisTurn[0], thisTurn[1]);
                
                // debug tablero después
                System.out.println("Tablero después de resolver:");
                System.out.println(game.getBoard().toString());
                
                gameHistory.add(new Action[] {thisTurn[0], thisTurn[1]});
                
                barrier.await(); // Turno resuelto
            }
            
            // Fin
            String result = game.getResultSummary();
            sendBoth("GAME_OVER: " + result);
            
            System.out.println("\nGuardando partida en XML...");
            XMLGameManager.saveGame(names[0], names[1], gameHistory, result);

            
            
            Thread.sleep(500);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out[0].close();
                out[1].close();
                clients[0].close();
                clients[1].close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendBoth(Object obj) {
        try {
            out[0].writeObject(obj);
            out[0].flush();
            out[1].writeObject(obj);
            out[1].flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}