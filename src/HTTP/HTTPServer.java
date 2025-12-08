package HTTP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HTTPServer {
    private static final int PORT = 8080;
    
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        
        System.out.println("=== SERVIDOR HTTP ===");
        System.out.println("Escuchando en http://localhost:" + PORT);
        System.out.println("Rutas disponibles:");
        System.out.println("  /games - Lista de partidas");
        System.out.println("  /game/{id} - Detalles de partida");
        System.out.println("  /replay/{id} - Replay turno a turno\n");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new HTTPRequestHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error en servidor HTTP:");
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}


class HTTPRequestHandler implements Runnable {
    private Socket socket;
    
    public HTTPRequestHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            // primera línea
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                sendError(out, 400, "Bad Request");
                return;
            }
            
            System.out.println("Petición: " + requestLine);
            
            // Parsear 
            String[] parts = requestLine.split(" ");
            if (parts.length < 3) {
                sendError(out, 400, "Bad Request");
                return;
            }
            
            String method = parts[0];
            String path = parts[1];
            
            // solo GET
            if (!method.equals("GET")) {
                sendError(out, 405, "Method Not Allowed");
                return;
            }
            
            // Consumir cabeceras 
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
            }
            
            handleRequest(path, out);
            
        } catch (IOException e) {
            System.err.println("Error manejando petición:");
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(String path, PrintWriter out) {
        if (path.equals("/") || path.equals("/games")) {
            handleGames(out);
        } else if (path.startsWith("/game/")) {
            String gameId = path.substring(6); // dejar solo la ID
            handleGameDetails(gameId, out);
        } else if (path.startsWith("/replay/")) {
            String gameId = path.substring(8); // dejar solo la ID
            handleReplay(gameId, out);
        } else {
            sendError(out, 404, "Not Found");
        }
    }
    
    /*
      GET /games - Lista todas las partidas
     */
    private void handleGames(PrintWriter out) {
        try {
            String content = HTTPGameReader.getGamesList();
            sendResponse(out, 200, "OK", content);
        } catch (Exception e) {
            sendError(out, 500, "Internal Server Error: " + e.getMessage());
        }
    }
    
    /*
      GET /game/{id} - Detalles (texto)
     */
    private void handleGameDetails(String gameId, PrintWriter out) {
        try {
            String content = HTTPGameReader.getGameDetails(gameId);
            if (content == null) {
                sendError(out, 404, "Partida no encontrada");
            } else {
                sendResponse(out, 200, "OK", content);
            }
        } catch (Exception e) {
            sendError(out, 500, "Internal Server Error: " + e.getMessage());
        }
    }
    
    /*
      GET /replay/{id} - turno a turno
     */
    private void handleReplay(String gameId, PrintWriter out) {
        try {
            String content = HTTPGameReader.getGameReplay(gameId);
            if (content == null) {
                sendError(out, 404, "Partida no encontrada");
            } else {
                sendResponse(out, 200, "OK", content);
            }
        } catch (Exception e) {
            sendError(out, 500, "Internal Server Error: " + e.getMessage());
        }
    }
    
    /*
      respuesta exito 
     */
    private void sendResponse(PrintWriter out, int statusCode, String statusMessage, String content) {
        out.println("HTTP/1.1 " + statusCode + " " + statusMessage);
        out.println("Content-Type: text/plain; charset=UTF-8");
        out.println("Content-Length: " + content.getBytes().length);
        out.println("Connection: close");
        out.println(); // Línea en blanco para no fallo de hacha
        out.println(content);
        out.flush();
    }
    
    /*
     respuesta de error
     */
    private void sendError(PrintWriter out, int statusCode, String message) {
        String content = "<html><body><h1>" + statusCode + " " + message + "</h1></body></html>";
        out.println("HTTP/1.1 " + statusCode + " " + message);
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println("Content-Length: " + content.getBytes().length);
        out.println("Connection: close");
        out.println();
        out.println(content);
        out.flush();
    }
}