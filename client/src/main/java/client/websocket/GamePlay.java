package client.websocket;

import chess.*;
import client.ChessClient;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;
import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;
import static websocket.commands.UserGameCommand.CommandType.RESIGN;

public class GamePlay {
    private WebsocketFacade websocket;
    private String authToken;
    private int gameNumber;
    ChessGame chessGame;
    ChessClient.Color color;
    ChessClient chessClient;
    private Map<Character,Integer> map;

    public GamePlay(WebsocketFacade wS,String aT,int gN,ChessGame cG,ChessClient.Color c,ChessClient cC){
        websocket = wS;
        authToken = aT;
        gameNumber = gN;
        chessGame = cG;
        color = c;
        chessClient = cC;
        map = new HashMap<>();
        map.put('a',1);
        map.put('b',2);
        map.put('c',3);
        map.put('d',4);
        map.put('e',5);
        map.put('f',6);
        map.put('g',7);
        map.put('h',8);
    }

    public String resign(Scanner scanner){
        System.out.println("Are you sure you want to resign? (Y to confirm, resign): ");
        String input = scanner.nextLine();
        if(!input.equalsIgnoreCase("y")){
            return "You have not resigned.";
        }

        System.out.println("resigning ...");
        try {
            websocket.send(new UserGameCommand(RESIGN, authToken, gameNumber + 1));
            return "";
        } catch (IOException e) {
            System.out.println("Error, resign failed");
        }
        return "";
    }

    public ChessPosition getPositionFromInput(String input) throws Exception {
        char[] cInput = input.toCharArray();
        if(cInput.length != 2){
            throw new Exception("Invalid position");
        }
        char ch = cInput[1];
        if (ch < '1' || ch > '8') {
            throw new Exception("Column must be 1-8");
        }
        int col = map.get(Character.toLowerCase(cInput[0]));
        int row = ch - '0';
        return new ChessPosition(row,col);
    }

    public String drawHighlightedBoard(Scanner scanner){
        try {
            System.out.println("Enter the position of the piece you want to check its moves: (ex. \"B5\")");
            String input = scanner.nextLine();
            if(input.equals("STOP")){
                return "";
            }
            try{
                ChessPosition start = getPositionFromInput(input);
                Collection<ChessMove> moves = chessGame.validMoves(start);
                Collection<ChessPosition> posToHighlight = new ArrayList<>();
                for(ChessMove move : moves){
                    posToHighlight.add(move.getEndPosition());
                }
                if(color == null){
                    color = ChessClient.Color.WHITE;
                }
                chessClient.gP.drawBoard(chessGame.getBoard(), color, posToHighlight, chessClient);

            }catch(Exception e){
                System.out.println("Invalid position. Try again or type STOP to exit to the Gameplay UI");
                drawHighlightedBoard(scanner);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    public String redrawChessboard(){
        try {
            if(color == null){
                color = ChessClient.Color.WHITE;
            }
            chessClient.gP.drawBoard(chessGame.getBoard(), color, null, chessClient);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    public String makeMove(Scanner scanner, ChessClient chessClient){
        if(color == null){
            return "Error: cannot move pieces as observer. \n Please try another command \n";
        }
        System.out.println("Enter the position of the piece you want to move: (ex. \"B5\")");
        String input = scanner.nextLine();
        try{
            if(input.equals("STOP")){
                return "";
            }
            ChessPosition start = getPositionFromInput(input);
            System.out.println("Enter the position you want to move the piece to: (ex. \"B5\")");
            input = scanner.nextLine();
            if(input.equals("STOP")){
                return "";
            }
            ChessPosition end = getPositionFromInput(input);
            ChessGame.TeamColor c;
            if(color == ChessClient.Color.WHITE){
                c = ChessGame.TeamColor.WHITE;
            }else{
                c = ChessGame.TeamColor.BLACK;
            }
            ChessBoard board = chessGame.getBoard();
            ChessPiece p = board.getPiece(start);
            if(p == null){
                throw new Exception("no piece at starting position");
            }

            if(p.getTeamColor() != c){
                throw new Exception("Attempted to move the other color");
            }
            ChessPiece.PieceType promotion = null;
            int forward = 1;
            boolean promotionValid = false;
            if(p.getTeamColor() == ChessGame.TeamColor.BLACK) {forward = -1;}//Black advances opposite of White
            if(start.getRow()+forward == 1 || start.getRow()+forward == 8) {promotionValid = true;}
            if(p.getPieceType() == ChessPiece.PieceType.PAWN && promotionValid){
                promotion = getPromotion(scanner,c);
            }
            ChessMove m = new ChessMove(start,end,promotion);
            var userGameCommand = new UserGameCommand(MAKE_MOVE, authToken, gameNumber+1);
            userGameCommand.setMove(m);
            websocket.send(userGameCommand);
            return "";
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
            System.out.println("Try again or type STOP to exit to the Gameplay UI");
            makeMove(scanner, chessClient);
        }
        return "";
    }

    private ChessPiece.PieceType getPromotion(Scanner scanner, ChessGame.TeamColor c){
        System.out.println("Enter the letter of the piece you want to promote your pawn to:" +
                " (ex. Q,K,R,B) ");
        String input = scanner.nextLine();
        Map<Character,ChessPiece> promotionMap = new HashMap<>();
        promotionMap.put('q',new ChessPiece(c, ChessPiece.PieceType.QUEEN));
        promotionMap.put('k',new ChessPiece(c, ChessPiece.PieceType.KNIGHT));
        promotionMap.put('r',new ChessPiece(c, ChessPiece.PieceType.ROOK));
        promotionMap.put('b',new ChessPiece(c, ChessPiece.PieceType.BISHOP));
        var cInput = input.toCharArray();
        if(cInput.length > 1 || promotionMap.get(Character.toLowerCase(cInput[0])) == null){
            System.out.println("invalid piece type. Try again.");
            return getPromotion(scanner, c);
        }
        return promotionMap.get(Character.toLowerCase(cInput[0])).getPieceType();
    }

    public void drawBoard(ChessBoard board, ChessClient.Color color, Collection<ChessPosition> highlightedPositions, ChessClient chessClient){
        System.out.println("Here is the current board: ");
        System.out.println("Current turn: "+ chessGame.getTeamTurn().toString());
        if(color == ChessClient.Color.WHITE){
            board = flip(board);
        }else{
            board = flipForBlack(board);
        }
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[] header = {" ","a","b","c","d","e","f","g","h"," "};
        String[] sides = {"1", "2", "3", "4", "5", "6", "7", "8"};
        if(color == ChessClient.Color.BLACK){
            sides = new String[]{"8", "7", "6", "5", "4", "3", "2", "1"};
            header = new String[]{" ","h","g","f","e","d","c","b","a"," "};
        }
        for (int i = 0; i < 10; i++) {
            printPiece(out, SET_BG_COLOR_LIGHT_GREY,header[i]);
        }
        out.print("\n");
        int r = 8;
        for(ChessPiece[] row : board.getBoard()){
            printPiece(out,SET_BG_COLOR_LIGHT_GREY,sides[r-1]);
            for (int i = 0; i < 8; i++) {
                ChessPosition pos = new ChessPosition(r,i+1);
                if(color == ChessClient.Color.BLACK){
                    pos = flipChessPos(pos);
                }
                String tF;
                String bgF;
                String p;
                try {
                    p = row[i].toString();
                    if (row[i].getTeamColor().toString().equals("BLACK")) {
                        tF = SET_TEXT_COLOR_BLUE;
                    } else {
                        tF = SET_TEXT_COLOR_RED;
                    }
                }catch(Exception e){
                    p = " ";
                    tF = RESET_TEXT_COLOR;
                }
                bgF = formatPiece(highlightedPositions, r, i, pos);
                p = tF+p;
                printPiece(out,bgF,p);
            }
            printPiece(out,SET_BG_COLOR_LIGHT_GREY,sides[r-1]);
            out.print("\n");
            r-=1;
        }
        for (int i = 0; i < 10; i++) {
            printPiece(out, SET_BG_COLOR_LIGHT_GREY,header[i]);
        }
        out.print("\n");
    }

    private static String formatPiece(Collection<ChessPosition> highlightedPositions, int r, int i, ChessPosition pos) {
        String bgF;
        if(r %2==0){
            if(i %2 == 0){
                bgF = SET_BG_COLOR_WHITE;
                if(highlightedPositions != null && highlightedPositions.contains(pos)){
                    bgF = SET_BG_COLOR_GREEN;
                }
            }else{
                bgF = SET_BG_COLOR_BLACK;
                if(highlightedPositions != null && highlightedPositions.contains(pos)){
                    bgF = SET_BG_COLOR_DARK_GREEN;
                }
            }
        }else{
            if (i % 2 == 0) {
                bgF = SET_BG_COLOR_BLACK;
                if (highlightedPositions != null && highlightedPositions.contains(pos)) {
                    bgF = SET_BG_COLOR_DARK_GREEN;
                }
            } else {
                bgF = SET_BG_COLOR_WHITE;
                if (highlightedPositions != null && highlightedPositions.contains(pos)) {
                    bgF = SET_BG_COLOR_GREEN;
                }
            }
        }
        return bgF;
    }

    private ChessPosition flipChessPos(ChessPosition pos){
        return new ChessPosition(9-pos.getRow(),9-pos.getColumn());

    }

    private ChessBoard flipForBlack(ChessBoard board){
        ChessBoard finalBoard = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                finalBoard.addPiece(new ChessPosition(i+1,8-j),board.getPiece(new ChessPosition(i+1,j+1)));
            }
        }
        return finalBoard;
    }

    private ChessBoard flip(ChessBoard board) {
        ChessBoard finalBoard = new ChessBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                finalBoard.addPiece(new ChessPosition(8-i,j+1),board.getPiece(new ChessPosition(i+1,j+1)));
            }
        }
        return finalBoard;
    }

    private void printPiece(PrintStream out, String colorBG, String label){
        out.print(colorBG + " "+label+" "+RESET_BG_COLOR+RESET_TEXT_COLOR);
    }
}
