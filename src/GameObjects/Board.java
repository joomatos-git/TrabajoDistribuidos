package GameObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
    private static final int SIZE = 8;
    private Tile[][] tiles;
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    private List<Piece> capturedPieces;
    
    public Board() {
        tiles = new Tile[SIZE][SIZE];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        capturedPieces = new ArrayList<>();
        initializeTiles();
    }
    
    
    public void capturePiece(Piece piece) {
        whitePieces.remove(piece);
        blackPieces.remove(piece);
        capturedPieces.add(piece);
    }

    public List<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public List<Piece> getCapturedPiecesForColor(Color color) {
    	List<Piece> list = new ArrayList<>();
    	for(Piece p:capturedPieces) {
    		if(p.getColor()==color) {
    			list.add(p);
    		}
    	}
    	return list;
    	
    }
    
    
    private void initializeTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                tiles[row][col] = new Tile(row, col);
            }
        }
    }
    
    public void setupInitialPieces() {
        // BLANCAS (fila 0 y 1)
        Piece piece;
        
        piece = new Rook(Color.WHITE, tiles[0][0], 1);
        whitePieces.add(piece);
        
        piece = new Knight(Color.WHITE, tiles[0][1], 1);
        whitePieces.add(piece);
        
        piece = new Bishop(Color.WHITE, tiles[0][2], 1);
        whitePieces.add(piece);
        
        piece = new Queen(Color.WHITE, tiles[0][3]);
        whitePieces.add(piece);
        
        piece = new King(Color.WHITE, tiles[0][4]);
        whitePieces.add(piece);
        
        piece = new Bishop(Color.WHITE, tiles[0][5], 2);
        whitePieces.add(piece);
        
        piece = new Knight(Color.WHITE, tiles[0][6], 2);
        whitePieces.add(piece);
        
        piece = new Rook(Color.WHITE, tiles[0][7], 2);
        whitePieces.add(piece);
        
        for (int col = 0; col < SIZE; col++) {
            piece = new Pawn(Color.WHITE, tiles[1][col], col + 1);
            whitePieces.add(piece);
        }
        
        // NEGRAS (fila 6 y 7)
        for (int col = 0; col < SIZE; col++) {
            piece = new Pawn(Color.BLACK, tiles[6][col], col + 1);
            blackPieces.add(piece);
        }
        
        piece = new Rook(Color.BLACK, tiles[7][0], 1);
        blackPieces.add(piece);
        
        piece = new Knight(Color.BLACK, tiles[7][1], 1);
        blackPieces.add(piece);
        
        piece = new Bishop(Color.BLACK, tiles[7][2], 1);
        blackPieces.add(piece);
        
        piece = new Queen(Color.BLACK, tiles[7][3]);
        blackPieces.add(piece);
        
        piece = new King(Color.BLACK, tiles[7][4]);
        blackPieces.add(piece);
        
        piece = new Bishop(Color.BLACK, tiles[7][5], 2);
        blackPieces.add(piece);
        
        piece = new Knight(Color.BLACK, tiles[7][6], 2);
        blackPieces.add(piece);
        
        piece = new Rook(Color.BLACK, tiles[7][7], 2);
        blackPieces.add(piece);
    }
    
    public Tile getTile(int row, int col) {
        return tiles[row][col];
    }
    
    public int getSize() {
        return SIZE;
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
    
    
    //3 metodos para obtener piezas VIVAS
    public List<Piece> getWhitePieces() {
        return new ArrayList<>(whitePieces);
    }
    
    public List<Piece> getBlackPieces() {
        return new ArrayList<>(blackPieces);
    }
    
    public List<Piece> getAllPieces() {
        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(whitePieces);
        allPieces.addAll(blackPieces);
        return allPieces;
    }
    
    public void print() {
        int cellWidth = 3; // width of each cell

        // Print column letters
        System.out.print("   "); // padding for row numbers
        for (int col = 0; col < SIZE; col++) {
            char colLetter = (char) ('A' + col);
            System.out.print(String.format("%-" + cellWidth + "s", colLetter));
        }
        System.out.println();

        // Print board
        for (int row = 0; row < SIZE; row++) {
            int displayRow = SIZE - row; // Chess rows 8-1

            // Print horizontal line
            System.out.print("  ");
            for (int col = 0; col < SIZE; col++) {
                System.out.print("+" + "-".repeat(cellWidth));
            }
            System.out.println("+");

            // Print row number and pieces
            System.out.print(displayRow + " ");
            for (int col = 0; col < SIZE; col++) {
                Piece piece = tiles[SIZE - 1 - row][col].getPiece(); // row reversed
                String pieceStr = (piece == null) ? "" : piece.toString();
                System.out.print("|" + String.format("%-" + cellWidth + "s", pieceStr));
            }
            System.out.println("|");
        }

        // Bottom line
        System.out.print("  ");
        for (int col = 0; col < SIZE; col++) {
            System.out.print("+" + "-".repeat(cellWidth));
        }
        System.out.println("+");

        System.out.println("Piezas blancas vivas: " + whitePieces.size());
        System.out.println("Piezas negras vivas: " + blackPieces.size());
    }

    
    //por si me da tiempo a hacer interfaz para hacer algo rollo chess.com
    public int getMaterialCaptured(Color color) {
        return capturedPieces.stream()
            .filter(p -> p.getColor() == color)
            .mapToInt(this::getPieceValue)
            .sum();
    }
    
    private int getPieceValue(Piece piece) {
        if (piece instanceof Pawn) return 1;
        if (piece instanceof Knight) return 3;
        if (piece instanceof Bishop) return 3;
        if (piece instanceof Rook) return 5;
        if (piece instanceof Queen) return 9;
        if (piece instanceof King) return 0; // Rey no tiene "valor" de captura
        return 0;
    }
    
    
    
}