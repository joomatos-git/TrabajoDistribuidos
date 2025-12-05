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
        tiles[0][0].setPiece(new Rook(Color.BLACK));
        tiles[0][1].setPiece(new Knight(Color.BLACK));
        tiles[0][2].setPiece(new Bishop(Color.BLACK));
        tiles[0][3].setPiece(new Queen(Color.BLACK));
        tiles[0][4].setPiece(new King(Color.BLACK));
        tiles[0][5].setPiece(new Bishop(Color.BLACK));
        tiles[0][6].setPiece(new Knight(Color.BLACK));
        tiles[0][7].setPiece(new Rook(Color.BLACK));
        
        for (int col = 0; col < SIZE; col++) {
            tiles[1][col].setPiece(new Pawn(Color.BLACK));
        }
        
        // blancas
        for (int col = 0; col < SIZE; col++) {
            tiles[6][col].setPiece(new Pawn(Color.WHITE));
        }
        
        tiles[7][0].setPiece(new Rook(Color.WHITE));
        tiles[7][1].setPiece(new Knight(Color.WHITE));
        tiles[7][2].setPiece(new Bishop(Color.WHITE));
        tiles[7][3].setPiece(new Queen(Color.WHITE));
        tiles[7][4].setPiece(new King(Color.WHITE));
        tiles[7][5].setPiece(new Bishop(Color.WHITE));
        tiles[7][6].setPiece(new Knight(Color.WHITE));
        tiles[7][7].setPiece(new Rook(Color.WHITE));
    }
    
