package Networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import Acciones.Action;
import Acciones.ActionType;
import GameObjects.Board;
import GameObjects.Game;
import GameObjects.Game.GameState;
import GameObjects.Piece;
import GameObjects.Tile;
import Logic.GameValidator;

public class PlayerThread implements Runnable{
	private Socket player;
	private CyclicBarrier barrier;
	private int id;
	private String[] names;
	private ObjectOutputStream out;
	private Action[] actionPerformed;
	private Game game;
	
	public PlayerThread(Socket socket, Game game, CyclicBarrier barrier,  int i, String[] names,
			ObjectOutputStream out, Action[] action) {
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
		try {
			ObjectInputStream in = new ObjectInputStream(player.getInputStream());
			String nombre = in.readUTF();
			names[id]=nombre;
			actionPerformed[id] = null;
			Board board = game.getBoard();
			barrier.await();
			while(game.getState()!=GameState.FINISHED) {
				
				do {
					Piece piece = null;
					while (piece == null) {
			            out.writeUTF("Introduce fila de la pieza a mover (ej: 1):");
		                out.flush();

			            int row = in.readInt()-1;
			            out.writeUTF("Columna (ej: A):");
		                out.flush();

			            String colStr = in.readUTF().trim().toUpperCase();
			            if (colStr.length() != 1) {
			                out.writeUTF("Columna inválida");
			                out.flush();
			                continue;
			            }
			            char ch = colStr.charAt(0);
			            int col = ch - 'A'; 
			            
			            if (!board.isValidPosition(row, col)) {
			            	out.writeUTF("Posición inválida");
			                out.flush();

			                continue;
			            }
			            Tile t = board.getTile(row, col);
			            if (t.isEmpty()) {
			            	out.writeUTF("No hay pieza en esa casilla");
			                out.flush();

			                continue;
			            }
			            piece = t.getPiece();
			        }
					
					Tile destination = null;
			        while (destination == null) {
			        	out.writeUTF("Introduce fila destino ");
		                out.flush();

			            int row = in.readInt()-1;
			            out.writeUTF("Columna ");
		                out.flush();

			            String colStr = in.readUTF().trim().toUpperCase();
			            if (colStr.length() != 1) {
			                out.writeUTF("Columna inválida");
			                out.flush();

			                continue;
			            }
			            char ch = colStr.charAt(0);
			            int col = ch - 'A'; 
			            if (!board.isValidPosition(row, col)) {
			                out.writeUTF("Posición inválida");
			                out.flush();

			                continue;
			            }
			            destination = board.getTile(row, col);
			        }
			        
			        ActionType type = null;
			        while (type == null) {
			        	out.writeUTF("Tipo de acción (MOVE/ATTACK): ");
		                out.flush();

			            String s = in.readUTF();
			            if (s.equalsIgnoreCase("MOVE")) type = ActionType.MOVE;
			            else if (s.equalsIgnoreCase("ATTACK")) type = ActionType.ATTACK;
			            else {
			            	out.writeUTF("Tipo inválido"); 
			            	out.flush();
			            }
			        }
			        
			        actionPerformed[id] = new Action(type, piece, destination);
			        
					if(!GameValidator.isValidAction(board, actionPerformed[id])) {
						out.writeUTF("Accion invalida. Repetir proceso");
		                out.flush();

					}
					
				}while(!GameValidator.isValidAction(board, actionPerformed[id]));
				barrier.await();
				
				//esperar a que resuelva el gamethread las acciones
				barrier.await();
					
				
			}
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {}
		
		
	}

}
