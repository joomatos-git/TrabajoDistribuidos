package Networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import Acciones.Action;
import Acciones.ActionType;
import GameObjects.Board;
import GameObjects.Piece;
import GameObjects.Tile;
import Logic.GameValidator;


public class Client {
    private static final String HOST = "localhost"; //solo para la partida
    private static final int PORT = 5000;
    private static final String SERVER = "http://localhost:8080"; //solo para el HTTP


    public static void main(String[] args) {
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Bienvenido!\nOpciones:\n1.Unirse a una partida\n2.Ver el almacen de partidas guardadas\n3.Salir");
    	int i = sc.nextInt();
		sc.nextLine(); // consumir el \n 

    	while(i!=3) {
    		if(i==1) {
    			joinGame(sc);
    		}
    		else if(i==2) {
    			accessStored(sc);
    		}
    		else {
    			System.out.println("Introduce un número válido");
    		}
        	System.out.println("Bienvenido!\nOpciones:\n1.Unirse a una partida\n\2.Ver el almacen de partidas guardadas\n3.Salir");
    		i = sc.nextInt();
    		sc.nextLine(); // consumir el \n 


    	}
    	System.out.println("Hasta luego.");
        sc.close();

    }
    
    
    private static void accessStored(Scanner sc) {
        
        System.out.println("=== CLIENTE HTTP - CONSULTA DE PARTIDAS ===\n");
        
        while (true) {
            System.out.println("\nOpciones:");
            System.out.println("1. Ver lista de partidas");
            System.out.println("2. Ver detalles de una partida");
            System.out.println("3. Ver replay de una partida");
            System.out.println("4. Salir");
            System.out.print("\nElige opción: ");
            
            String opcion = sc.nextLine().trim();
            
            try {
                switch (opcion) {
                    case "1":
                        getGames();
                        break;
                        
                    case "2":
                        System.out.print("Introduce el ID de la partida: ");
                        String gameId = sc.nextLine().trim();
                        getGameDetails(gameId);
                        break;
                        
                    case "3":
                        System.out.print("Introduce el ID de la partida: ");
                        String replayId = sc.nextLine().trim();
                        getGameReplay(replayId);
                        break;
                        
                    case "4":
                        System.out.println("\n¡Hasta luego!");
                        return;
                        
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }
    
    private static void joinGame(Scanner sc) {
    	try (Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ) {

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
                       
                       if ("WRONG_ACTION".equals(msgStr)) {
                           System.out.println("Alguien ha intentado mover una pieza del otro color.");
                           
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
                    System.out.println("Posición inválida");
                    continue;
                }
                
                Tile destination = board.getTile(destRow, destCol);
                
                System.out.print("Tipo (MOVE/ATTACK): ");
                String typeStr = sc.nextLine().trim().toUpperCase();
                
                ActionType type = "MOVE".equals(typeStr) ? ActionType.MOVE : ActionType.ATTACK;
                
                Action action = new Action(type, piece, destination);
                
                if (!GameValidator.isValidAction(board, action)) {
                    System.out.println("Movimiento inválido");
                    continue;
                }
                
                return action;
                
            } catch (Exception e) {
                System.out.println("Error en input: " + e.getMessage());
            }
        }
    }
    
    
    
    
    //METODOS AUXILIARES PARA EL HTTP
    
    /**
     * GET /games - Lista todas las partidas
     */
    private static void getGames() throws Exception {
        System.out.println("\nObteniendo lista de partidas...\n");
        
        URL url = new URL(SERVER + "/games");
        String response = makeRequest(url);
        
        System.out.println(response);
    }
    
    /**
     * GET /game/{id} - Detalles de una partida
     */
    private static void getGameDetails(String gameId) throws Exception {
        System.out.println("\nObteniendo detalles de la partida...\n");
        
        URL url = new URL(SERVER + "/game/" + gameId);
        String response = makeRequest(url);
        
        if (response.startsWith("HTTP/1.1 404")) {
            System.out.println("Partida no encontrada.");
        } else {
            System.out.println(response);
        }
    }
    
    /**
     * GET /replay/{id} - Replay turno a turno
     */
    private static void getGameReplay(String gameId) throws Exception {
        System.out.println("\nObteniendo replay de la partida...\n");
        
        URL url = new URL(SERVER + "/replay/" + gameId);
        String response = makeRequest(url);
        
        if (response.startsWith("HTTP/1.1 404")) {
            System.out.println("Partida no encontrada.");
        } else {
            System.out.println(response);
        }
    }
    
    /**
     * Realiza una petición HTTP GET y devuelve la respuesta
     */
    private static String makeRequest(URL url) throws Exception {
        URLConnection connection = url.openConnection();
        
        BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), "UTF-8"));
        
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
        }
        
        in.close();
        
        return response.toString();
    }
}