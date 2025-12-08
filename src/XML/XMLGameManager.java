package XML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Acciones.Action;
import GameObjects.Piece;


public class XMLGameManager {
    private static final String XML_FILE = "/TrabajoDistribuidos/src/XML/Games.xml";
    private static final String DTD_FILE = "/TrabajoDistribuidos/src/XML/Partida.dtd";
    

    public static void saveGame(String player1, String player2, 
                                List<Action[]> turns, String result) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            Document doc;
            Element rootElement;
            
            // Cargar documento existente o crear nuevo
            File xmlFile = new File(XML_FILE);
            if (xmlFile.exists()) {
                doc = builder.parse(xmlFile);
                rootElement = doc.getDocumentElement();
            } else {
                doc = builder.newDocument();
                rootElement = doc.createElement("games");
                doc.appendChild(rootElement);
            }
            
            // elemento game
            Element gameElement = doc.createElement("game");
            
            // Atributos
            String gameId = "game_" + System.currentTimeMillis(); //para tener una unica para cada game
            gameElement.setAttribute("id", gameId);
            
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            gameElement.setAttribute("date", now.format(formatter));
            
            // Elemento players
            Element playersElement = doc.createElement("players");
            
            Element whiteElement = doc.createElement("white");
            whiteElement.setTextContent(player1);
            playersElement.appendChild(whiteElement);
            
            Element blackElement = doc.createElement("black");
            blackElement.setTextContent(player2);
            playersElement.appendChild(blackElement);
            
            gameElement.appendChild(playersElement);
            
            // Elemento turns
            Element turnsElement = doc.createElement("turns");
            
            for (int i = 0; i < turns.size(); i++) {
                Action[] turnActions = turns.get(i);
                Element turnElement = doc.createElement("turn");
                turnElement.setAttribute("number", String.valueOf(i + 1));
                
                // White action
                Element whiteActionElement = createActionElement(doc, "whiteAction", turnActions[0]);
                turnElement.appendChild(whiteActionElement);
                
                // Black action
                Element blackActionElement = createActionElement(doc, "blackAction", turnActions[1]);
                turnElement.appendChild(blackActionElement);
                
                turnsElement.appendChild(turnElement);
            }
            
            gameElement.appendChild(turnsElement);
            
            Element resultElement = doc.createElement("result");
            resultElement.setTextContent(result);
            
            //  ganador 
            if (result.contains(player1)) {
                resultElement.setAttribute("winner", "WHITE");
            } else if (result.contains(player2)) {
                resultElement.setAttribute("winner", "BLACK");
            } else {
                resultElement.setAttribute("winner", "DRAW");
            }
            
            gameElement.appendChild(resultElement);
            
            // añadir al root
            rootElement.appendChild(gameElement);
            
            // Escribir al archivo con DOCTYPE
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //indent para que quede mejor a primera vista
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, DTD_FILE);
            
            DOMSource source = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(XML_FILE));
            transformer.transform(source, streamResult);
            
            System.out.println("Partida guardada en " + XML_FILE);
            
        } catch (Exception e) {
            System.err.println("Error guardando partida en XML:");
            e.printStackTrace();
        }
    }
    

    private static Element createActionElement(Document doc, String elementName, Action action) {
        Element actionElement = doc.createElement(elementName);
        
        actionElement.setAttribute("type", action.getType().toString());
        
        Piece piece = action.getPiece();
        int fromRow = piece.getCurrentTile().getRow()+1;
        int fromCol = piece.getCurrentTile().getCol()+1;
        
        int toRow = action.getDestination().getRow()+1;
        int toCol = action.getDestination().getCol()+1;
        
        actionElement.setAttribute("fromRow", String.valueOf(fromRow));
        actionElement.setAttribute("fromCol", String.valueOf(fromCol));
        actionElement.setAttribute("toRow", String.valueOf(toRow));
        actionElement.setAttribute("toCol", String.valueOf(toCol));
        actionElement.setAttribute("piece", piece.toString());
        
        return actionElement;
    }
    

    public static void printAllGames() {
        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                System.out.println("No hay partidas guardadas.");
                return;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            
            NodeList gamesList = doc.getElementsByTagName("game");
            
            System.out.println("\n=== PARTIDAS GUARDADAS ===");
            System.out.println("Total de partidas: " + gamesList.getLength() + "\n");
            
            for (int i = 0; i < gamesList.getLength(); i++) {
                Node gameNode = gamesList.item(i);
                
                if (gameNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element gameElement = (Element) gameNode;
                    
                    String id = gameElement.getAttribute("id");
                    String date = gameElement.getAttribute("date");
                    
                    String white = gameElement.getElementsByTagName("white").item(0).getTextContent();
                    String black = gameElement.getElementsByTagName("black").item(0).getTextContent();
                    
                    Element resultElement = (Element) gameElement.getElementsByTagName("result").item(0);
                    String result = resultElement.getTextContent();
                    String winner = resultElement.getAttribute("winner");
                    
                    NodeList turnsList = gameElement.getElementsByTagName("turn");
                    
                    System.out.println("Partida #" + (i + 1));
                    System.out.println("  ID: " + id);
                    System.out.println("  Fecha: " + date);
                    System.out.println("  Jugadores: " + white + " (Blanco) vs " + black + " (Negro)");
                    System.out.println("  Turnos: " + turnsList.getLength());
                    System.out.println("  Resultado: " + result + " (Ganador: " + winner + ")");
                    System.out.println();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error leyendo partidas:");
            e.printStackTrace();
        }
    }
    

    public static void printGameDetails(String gameId) {
        try {
            File xmlFile = new File(XML_FILE);
            if (!xmlFile.exists()) {
                System.out.println("No hay partidas guardadas.");
                return;
            }
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            
            NodeList gamesList = doc.getElementsByTagName("game");
            
            for (int i = 0; i < gamesList.getLength(); i++) {
                Element gameElement = (Element) gamesList.item(i);
                
                if (gameElement.getAttribute("id").equals(gameId)) {
                    System.out.println("\n=== DETALLES DE PARTIDA ===");
                    System.out.println("ID: " + gameId);
                    System.out.println("Fecha: " + gameElement.getAttribute("date"));
                    
                    String white = gameElement.getElementsByTagName("white").item(0).getTextContent();
                    String black = gameElement.getElementsByTagName("black").item(0).getTextContent();
                    System.out.println("Jugadores: " + white + " vs " + black);
                    
                    NodeList turnsList = gameElement.getElementsByTagName("turn");
                    System.out.println("\nTurnos:");
                    
                    for (int j = 0; j < turnsList.getLength(); j++) {
                        Element turnElement = (Element) turnsList.item(j);
                        String turnNum = turnElement.getAttribute("number");
                        
                        Element whiteAction = (Element) turnElement.getElementsByTagName("whiteAction").item(0);
                        Element blackAction = (Element) turnElement.getElementsByTagName("blackAction").item(0);
                        
                        System.out.println("  Turno " + turnNum + ":");
                        System.out.println("    Blanco: " + formatAction(whiteAction));
                        System.out.println("    Negro: " + formatAction(blackAction));
                    }
                    
                    String result = gameElement.getElementsByTagName("result").item(0).getTextContent();
                    System.out.println("\nResultado: " + result);
                    
                    return;
                }
            }
            
            System.out.println("Partida no encontrada.");
            
        } catch (Exception e) {
            System.err.println("Error leyendo detalles:");
            e.printStackTrace();
        }
    }
    
    private static String formatAction(Element actionElement) {
        String type = actionElement.getAttribute("type");
        String piece = actionElement.getAttribute("piece");
        String from = "(" + actionElement.getAttribute("fromRow") + "," + 
                      actionElement.getAttribute("fromCol") + ")";
        String to = "(" + actionElement.getAttribute("toRow") + "," + 
                    actionElement.getAttribute("toCol") + ")";
        
        return type + " " + piece + " de " + from + " a " + to;
    }
}