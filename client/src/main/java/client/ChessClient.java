package client;

import chess.*;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import model.GameData;
import client.results.*;
import client.requests.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static websocket.commands.UserGameCommand.CommandType.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageHandler {
    enum State{
        PRELOGIN,POSTLOGIN,GAMEPLAY
    }
    enum Color{
        BLACK,WHITE
    }
    private final ServerFacade server;
    private WebsocketFacade websocket;
    private State state;
    private String authToken;
    private boolean admin = false;
    private Color color;
    private int gameNumber;
    private ChessGame chessGame;

    public ChessClient(String serverUrl) throws Exception {
        server = new ServerFacade(serverUrl);
        websocket = new WebsocketFacade(serverUrl,this);
        state = State.PRELOGIN;
        color = null;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        switch(state){
            case PRELOGIN:
                preloginRepl(scanner);
                break;
            case POSTLOGIN:
                postloginRepl(scanner);
                break;
            case GAMEPLAY:
                gameplayRepl(scanner);
                break;
        }
    }

    private String help() {
        String help;
        System.out.println("Enter one of the following commands:");
        if (state == State.PRELOGIN) {
            help = """
                     - help: display this message
                     - quit: exit the program
                     - login: enter credentials to do more actions
                     - register: create an account
                    """;
        } else if (state == State.POSTLOGIN) {
            help = """
                     - help: display this message
                     - logout: end session, not program
                     - create game: create a new chess game
                     - list games: list all chess games
                     - play game: join a chess game
                     - observe game: observe a chess game
                    """;
        }else if(state == State.GAMEPLAY){
            help = """
                     - help: display this message
                     - leave: return to Postlogin UI
                     - redraw chess board: redraws the board
                     - make move: make a move in the game
                     - resign: admit defeat and lose
                     - highlight legal moves: highlights the moves of a chess piece
                    """;
        }else{
            help = "";
        }
        return help;
    }

    private void preloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(help());
            System.out.print(SET_TEXT_COLOR_BLUE+">>> "+SET_TEXT_COLOR_WHITE);
            String input = scanner.nextLine();
            try{
                result = preloginEval(input, scanner);
                if(!result.equals("quit")){
                    System.out.println(result);
                }else{
                    break;
                }
            }catch (Throwable e){
                System.out.println(e.getMessage());
            }
            if(state == State.POSTLOGIN){
                break;
            }
        }
        if(state == State.POSTLOGIN){
            clearScreen();
            System.out.println("Entering postlogin UI");
            run();
        }
    }

    private void postloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("logout")) {
            System.out.print(help());
            System.out.print(SET_TEXT_COLOR_GREEN+ ">>> "+SET_TEXT_COLOR_WHITE);
            String input = scanner.nextLine();
            try{
                result = postloginEval(input, scanner);
                System.out.println(result);
            }catch (Throwable e){
                System.out.println(e.getMessage());
            }
            if(state == State.PRELOGIN || state == State.GAMEPLAY){
                break;
            }
        }
        if(state == State.PRELOGIN){
            clearScreen();
            System.out.println("Entering prelogin UI\n");
            run();
        }
        if(state == State.GAMEPLAY){
            clearScreen();
            System.out.println("Entering Gameplay UI\n");
            run();
        }
    }

    private void gameplayRepl(Scanner scanner){
        var result = "";
        while(!result.equals("leave")){
            System.out.print(help());
            System.out.print(SET_TEXT_COLOR_YELLOW+ ">>> "+SET_TEXT_COLOR_WHITE);
            String input = scanner.nextLine();
            try{
                result = gameplayEval(input, scanner);
                if(Objects.equals(result, "leave")){
                    break;
                }
                System.out.println(result);
            }catch (Throwable e){
                System.out.println(e.getMessage());
            }
            if(state == State.POSTLOGIN){
                break;
            }
        }
        if(state == State.POSTLOGIN){
            clearScreen();
            System.out.println("Entering postlogin UI\n");
            run();
        }
    }

    private String gameplayEval(String input, Scanner scanner){
        try{
            String inputLower = input.toLowerCase();
            String cmd = "";
            if(!inputLower.isEmpty()){
                cmd = inputLower;
            }
            return switch(cmd){
                case "help" -> "";
                case "leave" -> {
                   state = State.POSTLOGIN;
                   yield "leave";
                }
                case "resign" -> resign(scanner);
                case "highlight legal moves" -> drawHighlightedBoard(scanner);
                case "redraw chess board" -> redrawChessboard();
                case "make move" -> makeMove(scanner);
                default -> "Invalid input, try again";
            };
        }catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String resign(Scanner scanner){
        System.out.println("THIS IS NOT IMPLEMENTED YET");
        return "";
    }

    private String makeMove(Scanner scanner){
        System.out.println("THIS IS NOT IMPLEMENTED YET");
        return "";
    }

    private String drawHighlightedBoard(Scanner scanner){
        try {
            System.out.println("Enter the position of the piece you want to check its moves: (ex. \"B5\")");
            String input = scanner.nextLine();
            Map<Character,Integer> map = new HashMap<>();
            map.put('a',1);
            map.put('b',2);
            map.put('c',3);
            map.put('d',4);
            map.put('e',5);
            map.put('f',6);
            map.put('g',7);
            map.put('h',8);
            try{
                if(input.equals("STOP")){
                    return "";
                }
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
                ChessPosition start = new ChessPosition(row,col);
                Collection<ChessMove> moves = chessGame.validMoves(start);
                Collection<ChessPosition> posToHighlight = new ArrayList<>();
                for(ChessMove move : moves){
                    posToHighlight.add(move.getEndPosition());
                }
                if(color == null){
                    color = Color.WHITE;
                }
                drawBoard(chessGame.getBoard(), color, posToHighlight);

            }catch(Exception e){
                System.out.println("Invalid position. Try again or type STOP to exit to the Gameplay UI");
                drawHighlightedBoard(scanner);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    private String redrawChessboard(){
        try {
            if(color == null){
                color = Color.WHITE;
            }
            drawBoard(chessGame.getBoard(), color, null);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    private String preloginEval(String input,Scanner scanner){
        try {
            String inputLower = input.toLowerCase();
            String cmd = "";
            if(!inputLower.isEmpty()){
                cmd = inputLower;
            }
            return switch (cmd) {
                case "login" -> login(scanner);
                case "register" -> register(scanner);
                case "quit" -> "quit";
                case "help" -> "";
                default -> "Invalid input, try again";
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String login(Scanner scan){
        String password;
        System.out.print("Enter your username: ");
        String username = scan.nextLine();
        if(username.equals("STOP")){
            return "";
        }
        System.out.print("Enter your password: ");
        password = scan.nextLine();
        try{
            LoginResult result = server.login(new LoginRequest(username,password));
            authToken = result.authToken();
            state = State.POSTLOGIN;
            if(username.equals("admin")){
                admin = true;
            }
            return "You logged in as: "+result.username() + "\n";
        }catch(Exception e){
            if(e.getMessage().equals("Error: unauthorized")){
                System.out.println("No user was found with those credentials. " +
                        "Please try again or type \"STOP\" for the username to exit to the menu.\n");
            } else if (e.getMessage().equals("Error: bad request")){
                System.out.println("Either the username or the password was entered incorrectly." +
                        " Please try again or type \"STOP\" for the username to exit to the menu.\n");
            } else{
                System.out.println("    Error: "+e.getMessage());
            }
            return login(scan);
        }
    }

    private String register(Scanner scan){
        String password;
        System.out.print("Enter a username: ");
        String username = scan.nextLine();
        if(username.equals("STOP")){
            return "";
        }
        System.out.print("Enter a password: ");
        password = scan.nextLine();
        System.out.print("Enter an email address: ");
        String email = scan.nextLine();
        try{
            RegisterResult result = server.register(new RegisterRequest(username,password,email));
            authToken = result.authToken();
            state = State.POSTLOGIN;
            if(username.equals("admin")){
                admin = true;
            }
            return "You registered with the username: "+result.username()+", your password, and the email: "+email;
        }catch(Exception e){
            if(e.getMessage().equals("Error: bad request")){
                System.out.println("You were unable to register. Please try again or type \"STOP\" for the username to exit to the menu.\n");
            }else if(e.getMessage().equals("Error: already taken")){
                System.out.println("The username "+username+" is already taken. Please try again or type \"STOP\" for the username to exit to the menu.\n");
            }
            else{
                System.out.println("    Error: "+e.getMessage());
            }
            return register(scan);
        }
    }

    private String postloginEval(String input, Scanner scanner){
        try {
            String inputLower = input.toLowerCase();
            String cmd = "help";
            if(!inputLower.isEmpty()){
                cmd = inputLower;
            }
            return switch (cmd) {
                case "logout" -> logout();
                case "create game" -> createGame(scanner);
                case "list games" -> listGames();
                case "play game" -> playGame(scanner);
                case "observe game" -> observeGame(scanner);
                case "help" -> "";
                default -> {
                    if (admin && cmd.equals("clear")) {
                        yield clear();
                    }
                    yield "Invalid input, try again";
                }
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String logout() {
        try{
            server.logout(new LogoutRequest(authToken));
            state = State.PRELOGIN;
            admin = false;
            return "Logged out";
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("RESETTING");
            run();
        }
        return "";
    }

    private String createGame(Scanner scan) {
        System.out.print("Enter a name for the game: ");
        String gameName = scan.nextLine();
        try{
            server.createGame(authToken,gameName);
        } catch (Exception e) {
            if(e.getMessage().equals("Error: bad request")){
                System.out.println("You were unable to create the game. Please try again or type quit for the username to exit to the menu.\n");
            }
            else{
                System.out.println("    Error: "+e.getMessage());
            }
            return createGame(scan);
        }
        return "Created "+gameName;
    }

    private String listGames() {
        System.out.println("Here are all of the games");
        try {
            ArrayList<GameData> games = server.listGames(authToken);
            System.out.print(SET_TEXT_COLOR_GREEN + SET_BG_COLOR_BLACK);
            int counter = 1;
            for(GameData game : games){
                String gameName = game.gameName();
                String pB = game.blackUsername();
                String pW = game.whiteUsername();
                System.out.print(SET_BG_COLOR_BLACK);
                System.out.println(counter+" Name: "+gameName +
                        ", White: "+pW+", Black: "+pB+RESET_BG_COLOR);
                counter+=1;
            }
            System.out.print(SET_TEXT_COLOR_WHITE + RESET_BG_COLOR);
        } catch (Exception e) {
            System.out.println("Error in listing games. Please try again.");
        }
        return "";
    }

    private String playGame(Scanner scan) {
        System.out.print("Enter the number of the game that you want to join: ");
        String input = scan.nextLine();
        if(input.equals("STOP")){
            return "";
        }
        try{
            int number = Integer.parseInt(input);
            try{
                ArrayList<GameData> games = server.listGames(authToken);
                if(number > games.size() || number <= 0){
                    throw new Exception("The input number was not a valid game number. Please try again. " +
                            "Enter \"STOP\" to exit to the menu.");
                }
                GameData gameToJoin = games.get(number-1);
                gameNumber = number-1;
                System.out.print("Enter the color that you want to claim: ");
                input = scan.nextLine().toLowerCase();
                if(!(input.equals("b")||input.equals("w")||
                        input.equals("black")||input.equals("white"))){
                    throw new Exception("That was not a valid color. Please try again");
                }
                try{
                    var u = new UserGameCommand(CONNECT,authToken,gameToJoin.gameID());
                    if(input.equals("b") || input.equals("black")) {
                        color = Color.BLACK;
                        u.setColor("BLACK");
                        server.joinGame(new JoinRequest("BLACK", gameToJoin.gameID()), authToken);
//                        drawBoard(gameToJoin.game().getBoard(), color, null);
                    }else{
                        color = Color.WHITE;
                        u.setColor("WHITE");
                        server.joinGame(new JoinRequest("WHITE", gameToJoin.gameID()), authToken);
//                        drawBoard(gameToJoin.game().getBoard(), color,null);
                    }
                    state = State.GAMEPLAY;
                    websocket.send(u);
                    return "";
                }catch(Exception e){
                    if(e.getMessage().equals("Error: already taken")){
                        System.out.println("This player has already been taken. Please try again. Enter \"STOP\" to exit to the menu.");
                        playGame(scan);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                playGame(scan);
            }
        } catch (NumberFormatException e) {
            System.out.print("The input number was not a valid game number. ");
            System.out.println("Please try again. Enter \"STOP\" to exit to the menu.");
            playGame(scan);
        }
        return "";
    }

    private String observeGame(Scanner scan) {
        System.out.print("Enter the number of the game that you want to observe: ");
        String input = scan.nextLine();
        if(input.equals("STOP")){
            return "";
        }
        try{
            int number = Integer.parseInt(input);
            try{
                ArrayList<GameData> games = server.listGames(authToken);
                if(number > games.size() || number <= 0){
                    throw new Exception("The input number was not a valid game number. Please try again. " +
                            "Enter \"STOP\" to exit to the menu.");
                }
                GameData gameToObserve = games.get(number-1);
                gameNumber = number-1;
//                int gameIDToObserve = gameToObserve.gameID();
                System.out.println(gameToObserve.whiteUsername()+" is playing as WHITE");
                System.out.println(gameToObserve.blackUsername()+" is playing as BLACK");
                drawBoard(gameToObserve.game().getBoard(), Color.WHITE,null);
                state = State.GAMEPLAY;
                UserGameCommand u = new UserGameCommand(CONNECT,authToken,gameToObserve.gameID());
                u.setColor("observing");
                websocket.send(u);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                observeGame(scan);
            }
        } catch (NumberFormatException e) {
            System.out.print("The input number was not a valid game number. ");
            System.out.println("Please try again. Enter \"STOP\" to exit to the menu.");
            observeGame(scan);
        }
        return "";
    }

    private String clear() {
        if(admin) {
            try {
                server.logout(new LogoutRequest(authToken));
                server.clear();
                state = State.PRELOGIN;
            } catch (Exception e) {
                System.out.println("Error" + e.getMessage());
            }
            return "Cleared the database";
        }
        return "nice try. How'd you get here?";
    }

    private void clearScreen(){
        System.out.print(ERASE_SCREEN);
    }

    private void drawBoard(ChessBoard board, Color color, Collection<ChessPosition> highlightedPositions){
        System.out.println("Here is the current board: ");
        if(color == Color.WHITE){
            board = flip(board);
        }else{
            board = flipForBlack(board);
        }
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[] header = {" ","a","b","c","d","e","f","g","h"," "};
        String[] sides = {"1", "2", "3", "4", "5", "6", "7", "8"};
        if(color == Color.BLACK){
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
                if(color == Color.BLACK){
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
                    //board has a null object there
                    p = " ";
                    tF = RESET_TEXT_COLOR;
                }
                if(r%2==0){
                    if(i%2 == 0){
                        bgF = SET_BG_COLOR_WHITE;
                        if(highlightedPositions!= null && highlightedPositions.contains(pos)){
                            bgF = SET_BG_COLOR_GREEN;
                        }
                    }else{
                        bgF = SET_BG_COLOR_BLACK;
                        if(highlightedPositions!= null && highlightedPositions.contains(pos)){
                            bgF = SET_BG_COLOR_DARK_GREEN;
                        }
                    }
                }else{
                    if (i % 2 == 0) {
                        bgF = SET_BG_COLOR_BLACK;
                        if (highlightedPositions!= null && highlightedPositions.contains(pos)) {
                            bgF = SET_BG_COLOR_DARK_GREEN;
                        }
                    } else {
                        bgF = SET_BG_COLOR_WHITE;
                        if (highlightedPositions!= null && highlightedPositions.contains(pos)) {
                            bgF = SET_BG_COLOR_GREEN;
                        }
                    }
                }
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

    public void notify(ServerMessage s,String m){
        ServerMessage.ServerMessageType messageType = s.getServerMessageType();
        Gson gson = new Gson();
        switch (messageType){
            case ERROR:
                ErrorMessage message = new Gson().fromJson(m, ErrorMessage.class);
                System.out.println(message.getErrorMessage());
                break;
            case LOAD_GAME:
                LoadGameMessage l = new Gson().fromJson(m, LoadGameMessage.class);;
                chessGame = l.getChessGame();
                drawBoard(chessGame.getBoard(),color,null);
                run();
                break;
            case NOTIFICATION:
                NotificationMessage n = new Gson().fromJson(m, NotificationMessage.class);;
                String nMessage = n.getMessage();
                System.out.println(nMessage);
                break;
        }
    }
}
