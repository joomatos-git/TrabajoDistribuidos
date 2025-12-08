package Networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor que acepta conexiones de clientes y crea partidas.
 * Cada partida se maneja en su propio GameThread.
 */
public class Server {
    private static final int PORT = 5000;
    
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        
        System.out.println("=== SERVIDOR DE JUEGO ===");
        System.out.println("Escuchando en puerto " + PORT);
        
        try (ServerSocket ss = new ServerSocket(PORT)) {
            int gameCounter = 0;
            
            while (true) {
                try {
                    Socket[] clientes = new Socket[2];
                    
                    System.out.println("\nEsperando jugadores para partida #" + (gameCounter + 1) + "...");
                    
                    // Aceptar 2 clientes
                    for (int i = 0; i < 2; i++) {
                        clientes[i] = ss.accept();
                        System.out.println("  Cliente " + (i + 1) + " conectado desde: " + 
                                         clientes[i].getInetAddress());
                    }
                    
                    gameCounter++;
                    System.out.println("Iniciando partida #" + gameCounter);
                    
                    // Crear y ejecutar GameThread
                    pool.execute(new GameThread(clientes));
                    
                } catch (IOException ex) {
                    System.err.println("Error aceptando clientes:");
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error fatal del servidor:");
            e.printStackTrace();
        } finally {
            pool.shutdown();
            System.out.println("Servidor detenido.");
        }
    }
}