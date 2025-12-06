package GameObjects;

import GameObjects.Piece.Color;

public class Board {
    private static final int SIZE = 8;
    private Tile[][] tiles;
    
    public Board() {
        tiles = new Tile[SIZE][SIZE];
        initializeTiles();
        setupInitialPieces();
    }
    
    // Casillas vacías
    private void initializeTiles() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                tiles[row][col] = new Tile(row, col);
            }
        }
    }
    
    // Posición inicial de las piezas
    private void setupInitialPieces() {
        // negras
        tiles[0][0].setPiece(new Rook(Color.WHITE,1));
        tiles[0][1].setPiece(new Knight(Color.WHITE,1));
        tiles[0][2].setPiece(new Bishop(Color.WHITE,1));
        tiles[0][3].setPiece(new Queen(Color.WHITE));
        tiles[0][4].setPiece(new King(Color.WHITE));
        tiles[0][5].setPiece(new Bishop(Color.WHITE,2));
        tiles[0][6].setPiece(new Knight(Color.WHITE,2));
        tiles[0][7].setPiece(new Rook(Color.WHITE,2));
        
        for (int col = 0; col < SIZE; col++) {
            tiles[1][col].setPiece(new Pawn(Color.BLACK, col+1));
        }
        
        // blancas
        for (int col = 0; col < SIZE; col++) {
            tiles[6][col].setPiece(new Pawn(Color.WHITE, col+1));
        }
        
        tiles[7][0].setPiece(new Rook(Color.BLACK,1));
        tiles[7][1].setPiece(new Knight(Color.BLACK,1));
        tiles[7][2].setPiece(new Bishop(Color.BLACK,1));
        tiles[7][3].setPiece(new Queen(Color.BLACK));
        tiles[7][4].setPiece(new King(Color.BLACK));
        tiles[7][5].setPiece(new Bishop(Color.BLACK,2));
        tiles[7][6].setPiece(new Knight(Color.BLACK,2));
        tiles[7][7].setPiece(new Rook(Color.BLACK,2));
    }
    
    
    public Tile getTile(int row, int col) {
    	return tiles[row][col];
    }
    
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    // debugging
    public void print() {
        System.out.println("  0 1 2 3 4 5 6 7");
        System.out.println(" +-+-+-+-+-+-+-+-+");
        
        for (int row = SIZE - 1; row >= 0; row--) {
            System.out.print(row + "|");
            for (int col = 0; col < SIZE; col++) {
                Piece piece = tiles[row][col].getPiece();
                if (piece == null) {
                    System.out.print(" |");
                } else {
                    System.out.print(piece.toString() + "|");
                }
            }
            System.out.println();
            System.out.println(" +-+-+-+-+-+-+-+-+");
        }
    }

    
    
    
}
    
