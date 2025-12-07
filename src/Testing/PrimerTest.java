package Testing;

import java.util.Scanner;

import Acciones.Action;
import Acciones.ActionType;
import GameObjects.Board;
import GameObjects.Piece;
import GameObjects.Tile;
import Logic.GameValidator;
import Logic.TurnResolver;

public class PrimerTest {

	public static void main(String[] args) {
        Board board = new Board(); // Asume constructor que crea tablero y coloca piezas iniciales
        board.setupInitialPieces();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== TABLERO ACTUAL ===");
            board.print();

            System.out.println("\nJugador 1, introduce tu acción:");
            Action action1;
            do {
            action1 = readAction(board, sc);
            }while(!GameValidator.isValidAction(board, action1));

            System.out.println("\nJugador 2, introduce tu acción:");
            Action action2;
            do {
            action2 = readAction(board, sc);
            }while(!GameValidator.isValidAction(board, action2));
            
            
            System.out.println("\n=== RESOLVIENDO TURNO ===");
            TurnResolver.resolveTurn(board, action1, action2);

            System.out.println("\n=== TABLERO TRAS TURNO ===");
            board.print();
            
            System.out.println("\nPiezas capturadas:");
            for (Piece p : board.getCapturedPieces()) {
                System.out.println(p);
            }

            System.out.println("\n¿Continuar jugando? (s/n)");
            String cont = sc.next();
            if (!cont.equalsIgnoreCase("s")) break;
        }

        sc.close();
    }

    private static Action readAction(Board board, Scanner sc) {
        Piece piece = null;
        while (piece == null) {
            System.out.print("Introduce fila y columna de la pieza a mover/atacar (ej: 1 4): ");
            int r = sc.nextInt()-1;
            int c = sc.nextInt()-1;
            if (!board.isValidPosition(r, c)) {
                System.out.println("Posición inválida");
                continue;
            }
            Tile t = board.getTile(r, c);
            if (t.isEmpty()) {
                System.out.println("No hay pieza en esa casilla");
                continue;
            }
            piece = t.getPiece();
        }

        Tile destination = null;
        while (destination == null) {
            System.out.print("Introduce fila y columna destino (ej: 2 4): ");
            int r = sc.nextInt()-1;
            int c = sc.nextInt()-1;
            if (!board.isValidPosition(r, c)) {
                System.out.println("Posición inválida");
                continue;
            }
            destination = board.getTile(r, c);
        }

        ActionType type = null;
        while (type == null) {
            System.out.print("Tipo de acción (MOVE/ATTACK): ");
            String s = sc.next();
            if (s.equalsIgnoreCase("MOVE")) type = ActionType.MOVE;
            else if (s.equalsIgnoreCase("ATTACK")) type = ActionType.ATTACK;
            else System.out.println("Tipo inválido");
        }

        Action accion = new Action(type, piece, destination);
        System.out.println(accion.toString());
        
        return accion;

    }

}
