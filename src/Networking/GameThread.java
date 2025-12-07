package Networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import Acciones.Action;
import GameObjects.Game;
import GameObjects.Game.GameState;
import Logic.TurnResolver;

public class GameThread implements Runnable {

	private final Socket[] clients;
	private CyclicBarrier barrier = new CyclicBarrier(3);
	private String[] names = new String[2];
	private List<Action[]> register = new ArrayList<Action[]>();
	private Action[] thisTurn = new Action[2];
	private Game game;
	private ObjectOutputStream[] out = new ObjectOutputStream[2];

	public GameThread(Socket[] clients) {
		this.clients = clients;
	}

	@Override
	public void run() {
		System.out.println("Both players joined. Game started."); // debug
		try {
			out[0] = new ObjectOutputStream(clients[0].getOutputStream());
			out[1] = new ObjectOutputStream(clients[1].getOutputStream());

			game = new Game("Player1", "Player2"); // hasta que se manden los nombres, pq al crear el thread necesito el
													// game. Luego game.setName y arreglado

			new Thread(new PlayerThread(clients[0], game, barrier, 0, names, out[0], thisTurn)).start();
			new Thread(new PlayerThread(clients[1], game, barrier, 1, names, out[1], thisTurn)).start();

			// nombres
			sendBoth("WELCOME - send your name (String)");
			barrier.await();
			// ambos han mandado nombre
			sendBoth("Players: " + names[0] + " vs " + names[1]);

			// crea juego (será almacenado aqui)
			game.setNames(names[0], names[1]);

			// manda tablero inicial (como string para evitar mandar todo el rato el objeto
			// game y tener que serializar todo)
			sendBoth(game.toString());

			while (game.getState() != GameState.FINISHED) {
				sendBoth("YOUR_ACTION");

				barrier.await();
				// ambos han mandado accion

				// resolver turno según acciones
				TurnResolver.resolveTurn(game.getBoard(), thisTurn[0], thisTurn[1]);
				// enviar post-turno
				sendBoth(game.toString());

				// devolver control a jugadores
				barrier.await();

				System.out.println("Turn resolved and state sent.");
				register.add(Arrays.copyOf(thisTurn, 2));
			}

			sendBoth("Game is over. " + game.getResultSummary());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				clients[0].close();
				clients[1].close();
				out[0].close();
				out[1].close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void sendBoth(Object s) {
		try {
			out[0].writeObject(s);
			out[1].writeObject(s);
			out[0].flush();
			out[1].flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

// notas para mañana. HiloJugador creado en algun punto de aqui para manejar ahi el In/Out.
// meter cyclicbarriers y countdownlatch en vez de hacerlo con el readObject este potroso que esta puesto aqui
// XML integration y en teoria lo mas barebones esta ya
