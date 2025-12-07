package Networking;

public class BoardStateDTO {
	//DataTransferObject de board (para evitar tener que andar mandando Game o Board como serializables, mas eficiente solo mandar acciones)
	
	public String[][] board;     // ASCII matrix like: "WP1", "BR2", "   "
    public boolean gameOver;
    public String message;       // for turn log / errors

    public BoardStateDTO(int size) {
        board = new String[size][size];
    }

}
