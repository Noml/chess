package client;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import client.results.*;
import client.requests.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ChessClient {
    enum State{
        PRELOGIN,POSTLOGIN
    }
    private final ServerFacade server;
    private State state;
    private String authToken;
    private boolean admin = false;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
        state = State.PRELOGIN;
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
        }
    }

    private String help(){
        String help;
        System.out.println("Enter one of the following commands:");
        if(state == State.PRELOGIN){
            help = """
                     - help: display this message
                     - quit: exit the program
                     - login: enter credentials to do more actions
                     - register: create an account
                    """;
        }else{
            help = """
                     - help: display this message
                     - logout: end session, not program
                     - create game: create a new chess game
                     - list games: list all chess games
                     - play game: join a chess game
                     - observe game: observe a chess game
                    """;
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
            if(state == State.PRELOGIN){
                break;
            }
        }
        if(state == State.PRELOGIN){
            clearScreen();
            System.out.println("Entering prelogin UI");
            run();
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
            System.out.print(SET_TEXT_COLOR_LIGHT_GREY + SET_BG_COLOR_GREEN);
            int counter = 1;
            for(GameData game : games){
                String gameName = game.gameName();
                String pB = game.blackUsername();
                String pW = game.whiteUsername();
                System.out.print(SET_BG_COLOR_GREEN);
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
                System.out.print("Enter the color that you want to claim: ");
                input = scan.nextLine().toLowerCase();
                if(!(input.equals("b")||input.equals("w")||
                        input.equals("black")||input.equals("white"))){
                    throw new Exception("That was not a valid color. Please try again");
                }
                try{
                    if(input.equals("b") || input.equals("black")) {
                        server.joinGame(new JoinRequest("BLACK", gameToJoin.gameID()), authToken);
                        drawBoard(gameToJoin.game().getBoard(), "BLACK");
                    }else{
                        server.joinGame(new JoinRequest("WHITE", gameToJoin.gameID()), authToken);
                        drawBoard(gameToJoin.game().getBoard(), "WHITE");
                    }
                    return "You would normally be ablet to play, but we're still working on improving the UI to allow that.";
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
//                int gameIDToObserve = gameToObserve.gameID();
                System.out.println(gameToObserve.whiteUsername()+" is playing as WHITE");
                System.out.println(gameToObserve.blackUsername()+" is playing as BLACK");
                drawBoard(gameToObserve.game().getBoard(), null);
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

    private void drawBoard(ChessBoard board,String color){
        System.out.println("Here is the current board: ");
        if(color == null){
            color = "WHITE";
        }
        if(color.equals("WHITE")){
            board = flip(board);
        }else{
            board = flipForBlack(board);
        }
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[] header = {" ","a","b","c","d","e","f","g","h"," "};
        String[] sides = {"1", "2", "3", "4", "5", "6", "7", "8"};
        if(color.equals("BLACK")){
            sides =new String[]{"8", "7", "6", "5", "4", "3", "2", "1"};
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
                    }else{
                        bgF = SET_BG_COLOR_BLACK;
                    }
                }else{
                    if(i%2 == 0){
                        bgF = SET_BG_COLOR_BLACK;
                    }else{
                        bgF = SET_BG_COLOR_WHITE;
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
