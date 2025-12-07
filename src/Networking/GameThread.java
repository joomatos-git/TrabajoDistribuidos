package Networking;

import java.io.*;
import java.net.Socket;

import Acciones.Action;
import GameObjects.Game;
import Logic.TurnResolver;

public class GameThread implements Runnable {

    private final Socket[] clients;  

    public GameThread(Socket[] clients) {
        this.clients = clients;
    }

    @Override
    public void run() {
        System.out.println("GameThread started.");

        try (
            ObjectOutputStream out1 = new ObjectOutputStream(clients[0].getOutputStream());
            ObjectInputStream in1   = new ObjectInputStream(clients[0].getInputStream());
            ObjectOutputStream out2 = new ObjectOutputStream(clients[1].getOutputStream());
            ObjectInputStream in2   = new ObjectInputStream(clients[1].getInputStream());
        ) {
            // nombres (handshake para testing al principio)
            out1.writeObject("WELCOME - send your name (String)");
            out1.flush();
            String name1 = (String) in1.readObject();

            out2.writeObject("WELCOME - send your name (String)");
            out2.flush();
            String name2 = (String) in2.readObject();

            System.out.println("Players: " + name1 + " vs " + name2);

            out1.writeObject("OPPONENT:" + name2);
            out2.writeObject("OPPONENT:" + name1);
            out1.flush();
            out2.flush();

            // crea juego (será almacenado aqui, no se envia a ningun sitio. Manejo solo mandando acciones)
            Game game = new Game(name1, name2); 

            // manda tablero inicial
            out1.writeObject(game.toString());
            out2.writeObject(game.toString());
            out1.flush();
            out2.flush();

            while (true) {
                out1.writeObject("YOUR_ACTION");
                out2.writeObject("YOUR_ACTION");
                out1.flush();
                out2.flush();

                // recibe acciones (se construyen en el cliente a raiz de
                Action a1 = (Action) in1.readObject();
                Action a2 = (Action) in2.readObject();

                

                // resolver turno según acciones
                TurnResolver.resolveTurn(game.getBoard(), a1, a2);

                // enviar post-turno
                out1.writeObject(game.toString());
                out2.writeObject(game.toString());
                out1.flush();
                out2.flush();

                System.out.println("Turn resolved and state sent.");
            }

        } catch (Exception e) {
            System.out.println("GameThread ended due to exception: " + e.getMessage());
        } finally {
            System.out.println("Closing game sockets.");
            try { clients[0].close(); } catch (Exception ignored) {}
            try { clients[1].close(); } catch (Exception ignored) {}
        }
    }
}



// notas para mañana. HiloJugador creado en algun punto de aqui para manejar ahi el In/Out.
// meter cyclicbarriers y countdownlatch en vez de hacerlo con el readObject este potroso que esta puesto aqui
// XML integration y en teoria lo mas barebones esta ya

