package Logic;

import java.util.ArrayList;
import java.util.List;

import Acciones.Action;
import Acciones.ActionType;
import GameObjects.Board;
import GameObjects.Piece;
import GameObjects.Tile;

public class TurnResolver {
	private static List<Piece> attackers = new ArrayList<Piece>();
	
    
    public static void resolveTurn(Board board, Action action1, Action action2) {
        removeAllStuns(board);
        attackers.clear();
        if(action1.getType()==ActionType.ATTACK) {
        	attackers.add(action1.getPiece());
        }
        if(action2.getType()==ActionType.ATTACK) {
        	attackers.add(action2.getPiece());
        }
        resolveMovements(board, action1, action2);
        resolveAttacks(board, action1, action2);
    }
    
    private static void removeAllStuns(Board board) {
        for (Piece piece : board.getAllPieces()) {
            if (piece.isAlive()) {
                piece.setStunned(false);
            }
        }
    }
    
    private static void resolveMovements(Board board, Action action1, Action action2) {
        boolean action1IsMove = (action1.getType() == ActionType.MOVE);
        boolean action2IsMove = (action2.getType() == ActionType.MOVE);
        
        if (action1IsMove && action2IsMove) {
            if (action1.getDestination() == action2.getDestination()) {
                System.out.println("CONFLICTO: Ambos jugadores intentan moverse a " + 
                                   action1.getDestination() + ". Movimientos cancelados.");
                return;
            }
            
            executeMove(action1);
            executeMove(action2);
        }
        else if (action1IsMove) {
            executeMove(action1);
        } else if (action2IsMove) {
            executeMove(action2);
        }
    }
    
    private static void resolveAttacks(Board board, Action action1, Action action2) {
        boolean action1IsAttack = (action1.getType() == ActionType.ATTACK);
        boolean action2IsAttack = (action2.getType() == ActionType.ATTACK);
        
        if (!action1IsAttack && !action2IsAttack) {
            return;
        }
        
        if(action1.getDestination().equals(action2.getDestination())) {
        	if (action1IsAttack && !action2IsAttack) {
                executeAttack(board, action1, action2);
                return;
            }
            
            if (action2IsAttack && !action1IsAttack) {
                executeAttack(board, action2, action1);
                return;
            }
            if (action1IsAttack && action2IsAttack) {
        		//matar a ambas porque hay conflicto de ataque.
                action1.getPiece().kill(board);
                action2.getPiece().kill(board);               
                return;
            }
        }
        else {
        	if (action1IsAttack && !action2IsAttack) {
                executeAttackSimple(board,  action1);
                return;
            }
            
            if (action2IsAttack && !action1IsAttack) {
                executeAttackSimple(board,  action2);
                return;
            }
            if (action1IsAttack && action2IsAttack) {
            	executeAttackSimple(board, action1);
                executeAttackSimple(board, action2);
                executeAttackMovements(board, action1, action2);
            }
        }

    }

    private static void executeMove(Action action) {
        Piece piece = action.getPiece();
        
        if (!piece.isAlive()) {
            System.out.println("ERROR: Intento de mover pieza muerta " + piece);
            return;
        }
        
        Tile fromTile = piece.getCurrentTile();
        Tile toTile = action.getDestination();
        
        if (fromTile == null) {
            return;
        }
        fromTile.setPiece(null);
        toTile.setPiece(piece);
        
        System.out.println(piece + " se movió de " + fromTile + " a " + toTile);
    }
    
    
    private void executeAttack(Board board, Piece attacker, Tile startTile, Tile targetTile) {
        Piece victim = targetTile.getPiece();

        if (victim != null && victim.getColor() != attacker.getColor()) {
            victim.kill(board); 

            targetTile.setPiece(attacker);
            startTile.setPiece(null);
        }
    }

    
    
    private static void executeAttack(Board board, Action attackAction, Action otherAction) {
        Piece attacker = attackAction.getPiece();
        
        if (!attacker.isAlive()) {
            System.out.println("ERROR: Intento de atacar con pieza muerta " + attacker);
            return;
        }
        
        Tile attackerTile = attacker.getCurrentTile();
        Tile targetTile = attackAction.getDestination();
        
        if (attackerTile == null) {
            return;
        }
        
        if (otherAction.getType() == ActionType.MOVE &&
            otherAction.getDestination() == attackAction.getDestination()) {
            
            Piece victim = otherAction.getPiece();
            
            if (!victim.isAlive()) {
                System.out.println("ERROR: Víctima ya está muerta " + victim);
                return;
            }
            
            Tile movingFromTile = victim.getCurrentTile();
            
            System.out.println(attacker + " mata a " + victim + " mientras se movía a " + targetTile);
            
            victim.kill(board);
            
            if (movingFromTile != null) {
                movingFromTile.setPiece(null);
            }
            
            attackerTile.setPiece(null);
            targetTile.setPiece(attacker);
            return;
        }
        
        executeAttackSimple(board, attackAction);
    }
    
    private static void executeAttackSimple(Board board, Action attackAction) {
        Piece attacker = attackAction.getPiece();
        if (!attacker.isAlive()) {
            System.out.println("ERROR: Intento de atacar con pieza muerta " + attacker);
            return;
        }

        Tile attackerTile = attacker.getCurrentTile();
        Tile targetTile = attackAction.getDestination();
        if (attackerTile == null) return;

        if (targetTile.isEmpty()) {
            attacker.setStunned(true);
            System.out.println(attacker + " atacó al vacío en " + targetTile + ". ¡Aturdido!");
            return;
        }

        Piece victim = targetTile.getPiece();
        if (!victim.isAlive()) {
            System.out.println("ERROR: Víctima ya está muerta " + victim);
            return;
        }

        if(!attackers.contains(victim)) {
        	System.out.println(attacker + " captura a " + victim + " en " + targetTile);
            victim.kill(board); 
        }else {
            attacker.setStunned(true);
            System.out.println(attacker + " atacó al vacío en " + targetTile + ". ¡Aturdido!");
            return;

        }
        
    }
    
    // separado en dos porque podía pasar que atacara primero uno y entonces hacia nula al otro que ataca pero no lo captura en fin una movida
    private static void executeAttackMovements(Board board, Action action1, Action action2) {
        Piece attacker1 = action1.getPiece();
        Piece attacker2 = action2.getPiece();
        Tile attacker1From = attacker1.getCurrentTile();
        Tile attacker2From = attacker2.getCurrentTile();
        Tile attacker1To = action1.getDestination();
        Tile attacker2To = action2.getDestination();
        
        if(attacker1To.equals(attacker2From)&& attacker2To.equals(attacker1From)) {
        	attacker1From.setPiece(attacker2);
        	attacker2From.setPiece(attacker1);
        	return;
        }
        
        if(attacker1To.equals(attacker2From)) {
        	attacker2To.setPiece(attacker2);
        	attacker1To.setPiece(attacker1);
        	attacker1From.setPiece(null);
        	return;
        }
        if(attacker2To.equals(attacker1From)) {
        	attacker1To.setPiece(attacker1);
        	attacker2To.setPiece(attacker2);
        	attacker2From.setPiece(null);
        	return;
        }
        
        attacker1From.setPiece(null);
        attacker1To.setPiece(attacker1);

        attacker2From.setPiece(null);
        attacker2To.setPiece(attacker2);

    }
}
        
        
        
