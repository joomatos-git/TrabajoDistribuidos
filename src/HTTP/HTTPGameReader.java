package HTTP;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import Acciones.Action;
import Acciones.ActionType;
import GameObjects.Board;
import GameObjects.Game;
import GameObjects.Piece;
import GameObjects.Tile;
import Logic.TurnResolver;

// generar texto a partir de las peticiones de HTTPServer
public class HTTPGameReader {
    private static final String XML_FILE = "C:\\Users\\Joseba\\eclipse-workspace\\TrabajoDistribuidos\\src\\XML\\Games.xml";
    

    public static String getGamesList() throws Exception {
    	
    	File xmlFile = new File(XML_FILE);
    	System.out.println("Busco XML en: " + xmlFile.getAbsolutePath()); //debug temp
    	System.out.println("Existe? " + xmlFile.exists());

        if (!xmlFile.exists()) {
            return "No hay partidas guardadas.\n";
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        
        NodeList gamesList = doc.getElementsByTagName("game");
        
        StringBuilder txt = new StringBuilder();
        txt.append("=== PARTIDAS GUARDADAS ===\n");
        txt.append("Total de partidas: ").append(gamesList.getLength()).append("\n\n");
        
        for (int i = 0; i < gamesList.getLength(); i++) {
            Element gameElement = (Element) gamesList.item(i);
            
            String id = gameElement.getAttribute("id");
            String date = gameElement.getAttribute("date");
            String white = gameElement.getElementsByTagName("white").item(0).getTextContent();
            String black = gameElement.getElementsByTagName("black").item(0).getTextContent();
            
            txt.append((i+1)).append(". ID: ").append(id).append("\n");
            txt.append("   Jugadores: ").append(white).append(" vs ").append(black).append("\n");
            txt.append("   Fecha: ").append(date).append("\n\n");
        }
        
        return txt.toString();
    }
    
    /*
      GET /game/{id} - Genera texto plano con detalles de una partida
     */
    public static String getGameDetails(String gameId) throws Exception {
        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            return null;
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        
        NodeList gamesList = doc.getElementsByTagName("game");
        
        for (int i = 0; i < gamesList.getLength(); i++) {
            Element gameElement = (Element) gamesList.item(i);
            
            if (gameElement.getAttribute("id").equals(gameId)) {
                StringBuilder txt = new StringBuilder();
                
                String date = gameElement.getAttribute("date");
                String white = gameElement.getElementsByTagName("white").item(0).getTextContent();
                String black = gameElement.getElementsByTagName("black").item(0).getTextContent();
                Element resultElement = (Element) gameElement.getElementsByTagName("result").item(0);
                String result = resultElement.getTextContent();
                String winner = resultElement.getAttribute("winner");
                
                txt.append("\n=== DETALLES DE PARTIDA ===\n");
                txt.append("ID: ").append(gameId).append("\n");
                txt.append("Fecha: ").append(date).append("\n");
                txt.append("Blanco: ").append(white).append("\n");
                txt.append("Negro: ").append(black).append("\n");
                txt.append("Resultado: ").append(result).append(" (Ganador: ").append(winner).append(")\n\n");
                
                NodeList turnsList = gameElement.getElementsByTagName("turn");
                txt.append("TURNOS (").append(turnsList.getLength()).append("):\n");
                txt.append("----------------------------------------\n");
                
                for (int j = 0; j < turnsList.getLength(); j++) {
                    Element turnElement = (Element) turnsList.item(j);
                    String turnNum = turnElement.getAttribute("number");
                    
                    Element whiteAction = (Element) turnElement.getElementsByTagName("whiteAction").item(0);
                    Element blackAction = (Element) turnElement.getElementsByTagName("blackAction").item(0);
                    
                    txt.append("Turno ").append(turnNum).append(":\n");
                    txt.append("  Blanco: ").append(formatAction(whiteAction)).append("\n");
                    txt.append("  Negro:  ").append(formatAction(blackAction)).append("\n\n");
                }
                
                return txt.toString();
            }
        }
        
        return null; // Partida no encontrada
    }
    
    /*
     GET /replay/{id} - Genera texto plano con replay turno a turno
     */
    public static String getGameReplay(String gameId) throws Exception {
        File xmlFile = new File(XML_FILE);
        if (!xmlFile.exists()) {
            return null;
        }
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        
        NodeList gamesList = doc.getElementsByTagName("game");
        
        for (int i = 0; i < gamesList.getLength(); i++) {
            Element gameElement = (Element) gamesList.item(i);
            
            if (gameElement.getAttribute("id").equals(gameId)) {
                StringBuilder txt = new StringBuilder();
                
                String white = gameElement.getElementsByTagName("white").item(0).getTextContent();
                String black = gameElement.getElementsByTagName("black").item(0).getTextContent();
                
                txt.append("\n=== REPLAY: ").append(white).append(" vs ").append(black).append(" ===\n\n");
                
                // Tablero inicial
                txt.append("TABLERO INICIAL:\n");
                Game game = new Game(white, black);
                txt.append(game.getBoard().toString());
                
                // Simular cada turno
                NodeList turnsList = gameElement.getElementsByTagName("turn");
                
                for (int j = 0; j < turnsList.getLength(); j++) {
                    Element turnElement = (Element) turnsList.item(j);
                    String turnNum = turnElement.getAttribute("number");
                    
                    Element whiteActionElement = (Element) turnElement.getElementsByTagName("whiteAction").item(0);
                    Element blackActionElement = (Element) turnElement.getElementsByTagName("blackAction").item(0);
                    
                    Action whiteAction = elementActiontoAction(whiteActionElement, game.getBoard());
                    Action blackAction = elementActiontoAction(blackActionElement, game.getBoard());
                    
                    TurnResolver.resolveTurn(game.getBoard(), whiteAction, blackAction);
                                        

                    
                    txt.append("----------------------------------------\n");
                    txt.append("TURNO ").append(turnNum).append(":\n");
                    txt.append("  Blanco: ").append(whiteAction.toString()).append("\n");
                    txt.append("  Negro:  ").append(blackAction.toString()).append("\n\n");
                    

                    txt.append(game.getBoard().toString());
                }
                
                Element resultElement = (Element) gameElement.getElementsByTagName("result").item(0);
                String result = resultElement.getTextContent();
                txt.append("----------------------------------------\n");
                txt.append("RESULTADO: ").append(result).append("\n");
                
                return txt.toString();
            }
        }
        
        return null;
    }
    
    /*
      frormatea una acción para mostrar
     */
    private static String formatAction(Element actionElement) {
        String type = actionElement.getAttribute("type");
        String piece = actionElement.getAttribute("piece");
        String from = "(" + actionElement.getAttribute("fromRow") + "," + 
                      actionElement.getAttribute("fromCol") + ")";
        String to = "(" + actionElement.getAttribute("toRow") + "," + 
                    actionElement.getAttribute("toCol") + ")";
        
        return type + " " + piece + " de " + from + " -> " + to;
    }
    
    /* 
     	convierte un elemento accion en una accion, para la simulación de la partida.
     */
    
    private static Action elementActiontoAction(Element actionElement, Board board) {
    	String type = actionElement.getAttribute("type");
        int fromRow = Integer.parseInt(actionElement.getAttribute("fromRow")); 
        int fromCol = Integer.parseInt(actionElement.getAttribute("fromCol"));
        int toRow = Integer.parseInt(actionElement.getAttribute("toRow")); 
        int toCol = Integer.parseInt(actionElement.getAttribute("toCol"));
        
        ActionType aType=null;

        if(type.equals("MOVE")){
        	aType = ActionType.MOVE;
        }else if(type.equals("ATTACK")) {
        	aType=ActionType.ATTACK;
        }
        
        Piece p = board.getTile(fromRow, fromCol).getPiece();
        

        Tile t = board.getTile(toRow, toCol);

        
    	
        
        return new Action(aType, p, t);
    	
    }
    
    
    
}