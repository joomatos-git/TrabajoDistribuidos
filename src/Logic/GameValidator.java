package Logic;

import GameObjects.*;
import Acciones.*;
import java.util.ArrayList;
import java.util.List;

public class GameValidator {
    
    public static boolean isValidAction(Board board, Action action) {
        Piece piece = action.getPiece();
        Tile endTile = action.getDestination();
        
        if (piece == null || endTile == null) {
            return false;
        }
        
        if (!piece.isAlive()) {
            return false;
        }
        
        Tile startTile = piece.getCurrentTile();
        if (startTile == null) {
            return false;
        }
        
        if (piece.isStunned()) {
            return false;
        }
        
        if (action.getType() == ActionType.MOVE) {
            if (!endTile.isEmpty()) {
                return false;
            }
            return piece.canMove(board, startTile, endTile);
        } else {
            return piece.canAttack(board, startTile, endTile);
        }
    }
    
    public static List<Tile> getValidMoves(Board board, Piece piece) {
        List<Tile> validMoves = new ArrayList<>();

        if (!piece.isAlive() || piece.isStunned()) return validMoves;
        Tile start = piece.getCurrentTile();
        if (start == null) return validMoves;

        //obtener segun tipo de pieza (con los helpers de abajo y tal)
        if (piece instanceof Knight) {
            addKnightMoves(board, (Knight)piece, start, validMoves, true);
        } else if (piece instanceof King) {
            addKingMoves(board, (King)piece, start, validMoves, true);
        } else if (piece instanceof Rook) {
            addSlidingMoves(board, piece, start, validMoves, new int[][]{{1,0},{-1,0},{0,1},{0,-1}}, true);
        } else if (piece instanceof Bishop) {
            addSlidingMoves(board, piece, start, validMoves, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}}, true);
        } else if (piece instanceof Queen) {
            addSlidingMoves(board, piece, start, validMoves,
                new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}}, true);
        } else if (piece instanceof Pawn) {
            addPawnMoves(board, (Pawn)piece, start, validMoves, true);
        }

        return validMoves;
    }

    public static List<Tile> getValidAttacks(Board board, Piece piece) {
        List<Tile> validAttacks = new ArrayList<>();

        if (!piece.isAlive() || piece.isStunned()) return validAttacks;
        Tile start = piece.getCurrentTile();
        if (start == null) return validAttacks;

        if (piece instanceof Knight) {
            addKnightMoves(board, (Knight)piece, start, validAttacks, false);
        } else if (piece instanceof King) {
            addKingMoves(board, (King)piece, start, validAttacks, false);
        } else if (piece instanceof Rook) {
            addSlidingMoves(board, piece, start, validAttacks, new int[][]{{1,0},{-1,0},{0,1},{0,-1}}, false);
        } else if (piece instanceof Bishop) {
            addSlidingMoves(board, piece, start, validAttacks, new int[][]{{1,1},{1,-1},{-1,1},{-1,-1}}, false);
        } else if (piece instanceof Queen) {
            addSlidingMoves(board, piece, start, validAttacks,
                new int[][]{{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}}, false);
        } else if (piece instanceof Pawn) {
            addPawnMoves(board, (Pawn)piece, start, validAttacks, false);
        }

        return validAttacks;
    }

    
    private static final int[][] KNIGHT_OFFSETS = {
        {2,1},{2,-1},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}
    };

    private static final int[][] KING_OFFSETS = {
        {1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}
    };

    private static void addKnightMoves(Board board, Knight knight, Tile start, List<Tile> out, boolean forMove) {
        for (int[] o : KNIGHT_OFFSETS) {
            int nr = start.getRow() + o[0];
            int nc = start.getCol() + o[1];
            if (!board.isValidPosition(nr, nc)) continue;
            Tile t = board.getTile(nr, nc);
            if (forMove) {
                if (t.isEmpty() && knight.canMove(board, start, t)) out.add(t);
            } else {
                if (!t.isEmpty() && t.getPiece().getColor() != knight.getColor() && knight.canAttack(board, start, t)) out.add(t);
            }
        }
    }

    private static void addKingMoves(Board board, King king, Tile start, List<Tile> out, boolean forMove) {
        for (int[] o : KING_OFFSETS) {
            int nr = start.getRow() + o[0];
            int nc = start.getCol() + o[1];
            if (!board.isValidPosition(nr, nc)) continue;
            Tile t = board.getTile(nr, nc);
            if (forMove) {
                if (t.isEmpty() && king.canMove(board, start, t)) out.add(t);
            } else {
                if (!t.isEmpty() && t.getPiece().getColor() != king.getColor() && king.canAttack(board, start, t)) out.add(t);
            }
        }
    }

    //reina y torre
    private static void addSlidingMoves(Board board, Piece piece, Tile start, List<Tile> out, int[][] directions, boolean forMove) {
        for (int[] dir : directions) {
            int nr = start.getRow() + dir[0];
            int nc = start.getCol() + dir[1];
            while (board.isValidPosition(nr, nc)) {
                Tile t = board.getTile(nr, nc);
                if (t.isEmpty()) {
                    if (forMove) {
                        if (piece.canMove(board, start, t)) out.add(t);
                        nr += dir[0]; nc += dir[1];
                        continue;
                    } else {
                        nr += dir[0]; nc += dir[1];
                        continue;
                    }
                } else {
                    if (!forMove) {
                        if (t.getPiece().getColor() != piece.getColor() && piece.canAttack(board, start, t)) {
                            out.add(t);
                        }
                    }
                    break; 
                }
            }
        }
    }

    private static void addPawnMoves(Board board, Pawn pawn, Tile start, List<Tile> out, boolean forMove) {
        int direction = (pawn.getColor() == Color.WHITE) ? 1 : -1;
        int r = start.getRow();
        int c = start.getCol();

        if (forMove) {
            int nr = r + direction;
            if (board.isValidPosition(nr, c)) {
                Tile t = board.getTile(nr, c);
                if (t.isEmpty() && pawn.canMove(board, start, t)) out.add(t);
            }
            int startRow = (pawn.getColor() == Color.WHITE) ? 1 : 6;
            if (r == startRow) {
                int nr2 = r + 2 * direction;
                if (board.isValidPosition(nr2, c)) {
                    Tile mid = board.getTile(r + direction, c);
                    Tile t2 = board.getTile(nr2, c);
                    if (mid.isEmpty() && t2.isEmpty() && pawn.canMove(board, start, t2)) out.add(t2);
                }
            }
        } else {
            int[] cols = new int[]{c - 1, c + 1};
            int nr = r + direction;
            for (int nc : cols) {
                if (!board.isValidPosition(nr, nc)) continue;
                Tile t = board.getTile(nr, nc);
                if (!t.isEmpty() && t.getPiece().getColor() != pawn.getColor() && pawn.canAttack(board, start, t)) {
                    out.add(t);
                }
            }
        }
    }

    
    
}