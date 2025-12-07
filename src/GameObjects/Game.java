package GameObjects;

import Logic.TurnResolver;
import Logic.GameValidator;

import java.util.ArrayList;
import java.util.List;

import Acciones.Action;

public class Game {
    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private int turnNumber;
    private List<Action> actionHistory;
    private GameState state;
    private GameResult result; 
    
    public enum GameState {
        WAITING_FOR_PLAYERS,
        IN_PROGRESS,
        FINISHED
    }
    
    public enum GameResult {
        WHITE_WINS,
        BLACK_WINS,
        DRAW,          
        DRAW_BOTH_KINGS_DEAD,
        DRAW_INSUFFICIENT_MATERIAL,
        DRAW_STALEMATE,
        IN_PROGRESS
    }
    
    public Game(String whitePlayerName, String blackPlayerName) {
        this.board = new Board();
        board.setupInitialPieces();
        this.whitePlayer = new Player(whitePlayerName, Color.WHITE);
        this.blackPlayer = new Player(blackPlayerName, Color.BLACK);
        this.turnNumber = 1;
        this.state = GameState.IN_PROGRESS;
        this.result = GameResult.IN_PROGRESS;
        this.actionHistory = new ArrayList<>();
    }
    
    public void setNames(String s1, String s2) {
    	this.whitePlayer = new Player(s1, Color.WHITE);
    	this.blackPlayer = new Player(s2, Color.WHITE);
    }
    
    
    public boolean submitAction(Color playerColor, Action action) {
        Player player = (playerColor == Color.WHITE) ? whitePlayer : blackPlayer;
        
        if (player.isReady()) {
            return false;
        }
        
        if (state == GameState.FINISHED) {
            return false; 
        }
        
        if (!GameValidator.isValidAction(board, action)) {
            return false;
        }
        
        player.submitAction(action);
        
        if (bothPlayersReady()) {
            resolveTurn();
        }
        
        return true;
    }
    
    public boolean bothPlayersReady() {
        return whitePlayer.isReady() && blackPlayer.isReady();
    }
    
    private void resolveTurn() {
        Action whiteAction = whitePlayer.getPendingAction();
        Action blackAction = blackPlayer.getPendingAction();
        
        TurnResolver.resolveTurn(board, whiteAction, blackAction);
        
        whitePlayer.clearAction();
        blackPlayer.clearAction();
        
        turnNumber++;
        
        checkGameOver();
    }
    
    private void checkGameOver() {
        // si algún rey está muerto
        boolean whiteKingAlive=true;
    	for(Piece p:board.getCapturedPieces()) {
    		if(p instanceof King && p.getColor()==Color.WHITE) {
    			whiteKingAlive=false;
    			break;
    		}
    	}
    	
    	boolean blackKingAlive=true;
    	for(Piece p:board.getCapturedPieces()) {
    		if(p instanceof King && p.getColor()==Color.BLACK) {
    			blackKingAlive=false;
    			break;
    		}
    	}

        // empate específico de mi modalidad
        if (!whiteKingAlive && !blackKingAlive) {
            state = GameState.FINISHED;
            result = GameResult.DRAW_BOTH_KINGS_DEAD;
            return;
        }
        
        if (!whiteKingAlive) {
            state = GameState.FINISHED;
            result = GameResult.BLACK_WINS;
            return;
        }
        
        if (!blackKingAlive) {
            state = GameState.FINISHED;
            result = GameResult.WHITE_WINS;
            return;
        }
        
        if (isInsufficientMaterial()) {
            state = GameState.FINISHED;
            result = GameResult.DRAW_INSUFFICIENT_MATERIAL;
            return;
        }
        
        if (isStalemate()) {
            state = GameState.FINISHED;
            result = GameResult.DRAW_STALEMATE;
            return;
        }
    }
    
    private boolean isInsufficientMaterial() {
        var whitePieces = board.getWhitePieces();
        var blackPieces = board.getBlackPieces();
        
        if (whitePieces.size() == 1 && blackPieces.size() == 1) {
            return true;
        }
        
        if (whitePieces.size() == 2 && blackPieces.size() == 1) {
            boolean hasOnlyKnight = whitePieces.stream()
                .anyMatch(p -> p instanceof Knight);
            if (hasOnlyKnight) return true;
        }
        
        if (blackPieces.size() == 2 && whitePieces.size() == 1) {
            boolean hasOnlyKnight = blackPieces.stream()
                .anyMatch(p -> p instanceof Knight);
            if (hasOnlyKnight) return true;
        }
        
        if (whitePieces.size() == 2 && blackPieces.size() == 1) {
            boolean hasOnlyBishop = whitePieces.stream()
                .anyMatch(p -> p instanceof Bishop);
            if (hasOnlyBishop) return true;
        }
        
        if (blackPieces.size() == 2 && whitePieces.size() == 1) {
            boolean hasOnlyBishop = blackPieces.stream()
                .anyMatch(p -> p instanceof Bishop);
            if (hasOnlyBishop) return true;
        }
        
        if (whitePieces.size() == 2 && blackPieces.size() == 2) {
            Bishop whiteBishop = (Bishop) whitePieces.stream()
                .filter(p -> p instanceof Bishop)
                .findFirst().orElse(null);
            
            Bishop blackBishop = (Bishop) blackPieces.stream()
                .filter(p -> p instanceof Bishop)
                .findFirst().orElse(null);
            
            if (whiteBishop != null && blackBishop != null) {
                Tile whiteTile = whiteBishop.getCurrentTile();
                Tile blackTile = blackBishop.getCurrentTile();
                
                boolean whiteBishopOnWhiteSquare = (whiteTile.getRow() + whiteTile.getCol()) % 2 == 0;
                boolean blackBishopOnWhiteSquare = (blackTile.getRow() + blackTile.getCol()) % 2 == 0;
                
                if (whiteBishopOnWhiteSquare == blackBishopOnWhiteSquare) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    // hay ahogado (stalemate)?
    private boolean isStalemate() {
        boolean whiteHasLegalMoves = hasLegalMoves(Color.WHITE);
        boolean blackHasLegalMoves = hasLegalMoves(Color.BLACK);
        
        if (!whiteHasLegalMoves && !blackHasLegalMoves) {
            return true;
        }
                
        return false;
    }
    
    private boolean hasLegalMoves(Color color) {
        var pieces = (color == Color.WHITE) ? 
            board.getWhitePieces() : board.getBlackPieces();
        
        for (Piece piece : pieces) {
            // movimientos
            var moves = GameValidator.getValidMoves(board, piece);
            if (!moves.isEmpty()) {
                return true;
            }
            
            // ataques
            var attacks = GameValidator.getValidAttacks(board, piece);
            if (!attacks.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
    
    public Board getBoard() {
        return board;
    }
    
    public Player getWhitePlayer() {
        return whitePlayer;
    }
    
    public Player getBlackPlayer() {
        return blackPlayer;
    }
    
    public int getTurnNumber() {
        return turnNumber;
    }
    
    public GameState getState() {
        return state;
    }
    
    public GameResult getResult() {
        return result;
    }
    
    public Player getWinner() {
        if (state != GameState.FINISHED) {
            return null;
        }
        
        switch (result) {
            case WHITE_WINS:
                return whitePlayer;
            case BLACK_WINS:
                return blackPlayer;
            case DRAW:
            case DRAW_BOTH_KINGS_DEAD:
            case DRAW_INSUFFICIENT_MATERIAL:
            case DRAW_STALEMATE:
                return null; 
            default:
                return null;
        }
    }
    
    // saber si es empate
    public boolean isDraw() {
        return result == GameResult.DRAW ||
               result == GameResult.DRAW_BOTH_KINGS_DEAD ||
               result == GameResult.DRAW_INSUFFICIENT_MATERIAL ||
               result == GameResult.DRAW_STALEMATE;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Game [Turn ").append(turnNumber).append("] ");
        sb.append(whitePlayer).append(" vs ").append(blackPlayer);
        sb.append(" - ").append(state);
        
        if (state == GameState.FINISHED) {
            sb.append(" | Resultado: ").append(result);
        }
        
        return sb.toString();
    }
    
    // para mostrar resultado detallado
    public String getResultSummary() {
        if (state != GameState.FINISHED) {
            return "La partida sigue en curso.";
        }
        
        switch (result) {
            case WHITE_WINS:
                return "¡" + whitePlayer.getName() + " (BLANCAS) gana!";
            case BLACK_WINS:
                return "¡" + blackPlayer.getName() + " (NEGRAS) gana!";
            case DRAW_BOTH_KINGS_DEAD:
                return "¡EMPATE! Ambos reyes fueron eliminados simultáneamente.";
            case DRAW_INSUFFICIENT_MATERIAL:
                return "¡EMPATE! Material insuficiente para dar jaque mate.";
            case DRAW_STALEMATE:
                return "¡EMPATE! Ahogado (stalemate) - No hay movimientos legales disponibles.";
            case DRAW:
                return "¡EMPATE!";
            default:
                return "Estado desconocido.";
        }
    }
}