package client;

import chess.*;
import client.websocket.GamePlay;
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
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageHandler {
    enum State{
        PRELOGIN,POSTLOGIN,GAMEPLAY
    }
    public enum Color{
        BLACK,WHITE
    }
    private final ServerFacade server;
    private final WebsocketFacade websocket;
    private State state;
    private String authToken;
    private boolean admin = false;
    private Color color;
    private int gameNumber;
    private ChessGame chessGame;
    public GamePlay gP;

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
        System.out.println(SET_TEXT_COLOR_WHITE+"Enter one of the following commands:");
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
            String input = getString(scanner,SET_TEXT_COLOR_BLUE);
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
            System.out.println("Entering postlogin UI");
            run();
        }
    }

    private void postloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("logout")) {
            String input = getString(scanner,SET_TEXT_COLOR_GREEN);
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
            System.out.println("Entering prelogin UI\n");
            run();
        }
        if(state == State.GAMEPLAY){
            System.out.println("Entering Gameplay UI\n");
            run();
        }
    }

    private String getString(Scanner scanner, String c) {
        System.out.print(help());
        System.out.print(c+ ">>> "+SET_TEXT_COLOR_WHITE);
        String input = scanner.nextLine();
        return input;
    }

    private void gameplayRepl(Scanner scanner){
        var result = "";
        while(!result.equals("leave")){
            try{
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Error with sleep");
                gameplayRepl(scanner);
            }
                String input = getString(scanner,SET_TEXT_COLOR_YELLOW);
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
            gP = new GamePlay(websocket,authToken,gameNumber,chessGame,color,this);
            return switch(cmd){
                case "help" -> "";
                case "leave" -> {
                   state = State.POSTLOGIN;
                   websocket.send(new UserGameCommand(LEAVE,authToken,gameNumber+1));
                   yield "leave";
                }
                case "resign" -> gP.resign(scanner);
                case "highlight legal moves" -> gP.drawHighlightedBoard(scanner);
                case "redraw chess board" -> gP.redrawChessboard();
                case "make move" -> gP.makeMove(scanner, this);
                default -> "Invalid input, try again";
            };
        }catch (Exception ex) {
            return ex.getMessage();
        }
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

    private String getNextLine(String msg, Scanner scan){
        System.out.println(msg);
        return scan.nextLine();
    }

    private String register(Scanner scan){
        String password;
        System.out.print("Enter a username: ");
        String username = scan.nextLine();
        if(username.equals("STOP")){
            return "";
        }
        password = getNextLine("Enter a password: ",scan);
        String email = getNextLine("Enter an email address: ",scan);
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
                System.out.println("The username "+username+" is already taken." +
                        " Please try again or type \"STOP\" for the username to exit to the menu.\n");
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
            System.out.println("Error: " + e.getMessage()+"\nRESETTING");
            run();
        }
        return "";
    }

    private String createGame(Scanner scan) {
        String gameName = getNextLine("Enter a name for the game: ",scan);
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
            if(games.isEmpty()){
                return "No games have been started. Create a game by typing \"create game\"\n";
            }
            System.out.print(SET_TEXT_COLOR_GREEN + SET_BG_COLOR_BLACK);
            int counter = 1;
            for(GameData game : games){
                String gameName = game.gameName();
                String pB = game.blackUsername();
                String pW = game.whiteUsername();
                if(pB == null){
                    pB = "UNCLAIMED";
                }
                if(pW == null){
                    pW = "UNCLAIMED";
                }
                System.out.print(SET_BG_COLOR_BLACK);
                System.out.println(counter+" Name: "+gameName +
                        ", White: "+pW+", Black: "+pB+ " playable: " +
                        game.game().isPlayable()+RESET_BG_COLOR);
                counter+=1;
            }
            System.out.print(SET_TEXT_COLOR_WHITE + RESET_BG_COLOR);
        } catch (Exception e) {
            System.out.println("Error in listing games. Please try again.");
        }
        return "";
    }

    private String playGame(Scanner scan) {
        String input = getNextLine("Enter the number of the game that you want to join: ",scan);
        if(input.equals("STOP")){
            return "";
        }
        try{
            int number = Integer.parseInt(input);
            try{
                ArrayList<GameData> games = server.listGames(authToken);
                if(number > games.size() || number <= 0){
                    throw new Exception("The input number was not a valid game number. Please try again. Enter \"STOP\" to exit to the menu.");
                }
                GameData gameToJoin = games.get(number-1);
                gameNumber = number-1;
                input = getNextLine("Enter the color that you want to claim: ",scan).toLowerCase();
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
                    }else{
                        color = Color.WHITE;
                        u.setColor("WHITE");
                        server.joinGame(new JoinRequest("WHITE", gameToJoin.gameID()), authToken);
                    }
                    synchronized (websocket){
                        state = State.GAMEPLAY;
                        websocket.send(u);
                        return "";
                    }
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
            System.out.print("The input number was not a valid game number. \n Please try again. Enter \"STOP\" to exit to the menu.");
            playGame(scan);
        }
        return "";
    }

    private String observeGame(Scanner scan) {
        String input = getNextLine("Enter the number of the game that you want to observe: ",scan);
        if(input.equals("STOP")){
            return "";
        }
        try{
            int number = Integer.parseInt(input);
            try{
                ArrayList<GameData> games = server.listGames(authToken);
                if(number > games.size() || number <= 0){
                    throw new Exception("The input number was not a valid game number. Please try again. Enter \"STOP\" to exit to the menu.");
                }
                GameData gameToObserve = games.get(number-1);
                gameNumber = number-1;
                System.out.println(gameToObserve.whiteUsername()+" is playing as WHITE");
                System.out.println(gameToObserve.blackUsername()+" is playing as BLACK");
                state = State.GAMEPLAY;
                UserGameCommand u = new UserGameCommand(CONNECT,authToken,gameToObserve.gameID());
                u.setColor("observing");
                websocket.send(u);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                observeGame(scan);
            }
        } catch (NumberFormatException e) {
            System.out.print("The input number was not a valid game number. \n "+"Please try again. Enter \"STOP\" to exit to the menu.");
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

    public void notify(ServerMessage s,String m){
       synchronized (websocket){
           ServerMessage.ServerMessageType messageType = s.getServerMessageType();
           switch (messageType){
               case ERROR:
                   ErrorMessage message = new Gson().fromJson(m, ErrorMessage.class);
                   System.out.println(message.getErrorMessage());
                   break;
               case LOAD_GAME:
                   LoadGameMessage l = new Gson().fromJson(m, LoadGameMessage.class);
                   chessGame = l.getChessGame();
                   gP.drawBoard(chessGame.getBoard(),color,null, this);
                   break;
               case NOTIFICATION:
                   NotificationMessage n = new Gson().fromJson(m, NotificationMessage.class);
                   String nMessage = n.getMessage();
                   System.out.println(nMessage);
                   break;
           }
       }
       System.out.print(SET_TEXT_COLOR_YELLOW+">>> "+SET_TEXT_COLOR_WHITE);
    }
}