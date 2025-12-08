package Networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import Acciones.Action;
import Acciones.ActionType;
import GameObjects.Board;
import GameObjects.Piece;
import GameObjects.Tile;
import Logic.GameValidator;


public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            System.out.println("=== CONECTADO AL SERVIDOR ===\n");

            //  nombre
            Object msg = in.readObject();
            if ("SEND_NAME".equals(msg)) {
                System.out.print("Introduce tu nombre: ");
                String nombre = sc.nextLine().trim();
                out.writeObject(nombre);
                out.flush();
            }

            // info inicial
            String playersInfo = (String) in.readObject();
            System.out.println("\n" + playersInfo);

            String gameMsg = (String) in.readObject();
            if ("GAME_START".equals(gameMsg)) {
                Board initialBoard = (Board) in.readObject();
                System.out.println("\n=== TABLERO INICIAL ===");
                System.out.println(initialBoard.toString());
            }

            // Loop del juego
            while (true) {
                Object turnMsg = in.readObject();
                
                if (turnMsg instanceof String) {
                    String msgStr = (String) turnMsg;
                    
                    if (msgStr.startsWith("GAME_OVER")) {
                        System.out.println("\n" + msgStr);
                        break;
                    }
                    
                    if ("YOUR_TURN".equals(msgStr)) {
                        // Board (es como una copia, pero luego en el thread lo convierto todo asi que da igual. No sabia como hacerlo sino)
                        Board board = (Board) in.readObject();
                        
                        System.out.println("\n=== TU TURNO ===");
                        System.out.println(board.toString());
                        
                        // Crear acción 
                        Action action = collectValidAction(board, sc);
                        
                        // Enviar acción 
                        out.writeObject(action);
                        out.flush();
                        
                        System.out.println("Acción enviada. Esperando...");
                    }
                }
            }

            System.out.println("\n¡Gracias por jugar!");

        } catch (Exception e) {
            System.err.println("Error en cliente:");
            e.printStackTrace();
        }
    }

    private static Action collectValidAction(Board board, Scanner sc) {
        while (true) {
            try {
                System.out.print("\nFila origen (1-8): ");
                int row = Integer.parseInt(sc.nextLine().trim()) - 1;
                
                System.out.print("Columna origen (A-H): ");
                String colStr = sc.nextLine().trim().toUpperCase();
                int col = colStr.charAt(0) - 'A';
                
                if (!board.isValidPosition(row, col)) {
                    System.out.println("❌ Posición inválida");
                    continue;
                }
                
                Tile origin = board.getTile(row, col);
                if (origin.isEmpty()) {
                    System.out.println("❌ No hay pieza ahí");
                    continue;
                }
                
                Piece piece = origin.getPiece();
                
                System.out.print("Fila destino (1-8): ");
                int destRow = Integer.parseInt(sc.nextLine().trim()) - 1;
                
                System.out.print("Columna destino (A-H): ");
                String destColStr = sc.nextLine().trim().toUpperCase();
                int destCol = destColStr.charAt(0) - 'A';
                
                if (!board.isValidPosition(destRow, destCol)) {
                    System.out.println("❌ Posición inválida");
                    continue;
                }
                
                Tile destination = board.getTile(destRow, destCol);
                
                System.out.print("Tipo (MOVE/ATTACK): ");
                String typeStr = sc.nextLine().trim().toUpperCase();
                
                ActionType type = "MOVE".equals(typeStr) ? ActionType.MOVE : ActionType.ATTACK;
                
                Action action = new Action(type, piece, destination);
                
                if (!GameValidator.isValidAction(board, action)) {
                    System.out.println("❌ Movimiento inválido");
                    continue;
                }
                
                return action;
                
            } catch (Exception e) {
                System.out.println("❌ Error en input: " + e.getMessage());
            }
        }
    }
}