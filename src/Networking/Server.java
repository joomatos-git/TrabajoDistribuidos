package Networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		int gamePort = 8080;
		try (ServerSocket ss = new ServerSocket(gamePort)) {
			while (true) {
				try {
					Socket[] clientes = new Socket[3];
					
					for (int i = 0; i < 2; i++) {
						clientes[i] = ss.accept();
						 System.out.println("Cliente " + (i+1) + " conectado");
						 System.out.println("Faltan " + (2-i-1) + " clientes para empezar un nuevo juego");
					}
					
					pool.execute(new GameThread(clientes));

				} catch (IOException ex ) { ex.printStackTrace(); }
			}
		} catch (IOException e) {
			e.printStackTrace();
			pool.shutdown();
		}
	}

}
