package client;


import service.requests.LoginRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.CreateGameResult;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ChessClient {
    enum State{
        PRELOGIN,POSTLOGIN
    }
    private ServerFacade server;
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
                System.out.println(e.toString());
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
                System.out.println(e.toString());
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
            switch (cmd) {
                case "login":
                    return login(scanner);
                case "register":
                    return register(scanner);
                case "quit":
                    return "quit";
                case "help":
                    return "";
                default:
                    return "Invalid input, try again";
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String login(Scanner scan){
        String password;
        System.out.print("Enter your username: ");
        String username = scan.nextLine();
        if(username.equals("quit")){
            return username;
        }
        System.out.print("Enter your password: ");
        password = scan.nextLine();
        try{
            LoginResult result = server.login(new LoginRequest(username,password));
            authToken = result.authToken();
            password = "";
            state = State.POSTLOGIN;
            if(username.equals("admin")){
                admin = true;
            }
            return "You logged in as: "+result.username() + "\n";
        }catch(Exception e){
            if(e.getMessage().equals("Error: unauthorized")){
                System.out.println("No user was found with those credentials. Please try again or type quit for the username to exit.\n");
            }
            else{
                System.out.println("    Error: "+e.toString());
            }
            return login(scan);
        }
    }

    private String register(Scanner scan){
        String password;
        System.out.print("Enter a username: ");
        String username = scan.nextLine();
        if(username.equals("quit")){
            return username;
        }
        System.out.print("Enter a password: ");
        password = scan.nextLine();
        System.out.print("Enter an email address: ");
        String email = scan.nextLine();
        try{
            RegisterResult result = server.register(new RegisterRequest(username,password,email));
            authToken = result.authToken();
            password = "";
            state = State.POSTLOGIN;
            if(username.equals("admin")){
                admin = true;
            }
            return "You registered with the username: "+result.username()+", your password, and the email: "+email;
        }catch(Exception e){
            if(e.getMessage().equals("Error: bad request")){
                System.out.println("You were unable to register. Please try again or type quit for the username to exit.\n");
            }else if(e.getMessage().equals("Error: already taken")){
                System.out.println("The username "+username+" is already taken. Please try again or type quit for the username to exit.\n");
            }
            else{
                System.out.println("    Error: "+e.toString());
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
            switch (cmd) {
                case "logout":
                    return logout();
                case "create game":
                    return createGame(scanner);
                case "list games":
                    return listGames();
                case "play game":
                    return playGame();
                case "observe game":
                    return observeGame();
                case "help":
                    return "";
                default:
                    if(admin && cmd.equals("clear")){
                        return clear();
                    }
                    return "Invalid input, try again";
            }
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
        CreateGameResult c;
        try{
            c = server.createGame(authToken,gameName);
        } catch (Exception e) {
            if(e.getMessage().equals("Error: bad request")){
                System.out.println("You were unable to create the game. Please try again or type quit for the username to exit.\n");
            }
            else{
                System.out.println("    Error: "+e.toString());
            }
            return createGame(scan);
        }
        return "Created "+gameName+ " with gameID "+c.gameID();
    }

    private String listGames() {
        return "";
    }

    private String playGame() {
        return "";
    }

    private String observeGame() {
        return "";
    }

    private String clear() {
        if(admin) {
            try {
                server.logout(new LogoutRequest(authToken));
                server.clear();
                state = State.PRELOGIN;
            } catch (Exception e) {
                System.out.println("Error" + e.toString());
            }
            return "Cleared the database";
        }
        return "nice try. How'd you get here?";
    }

    private void clearScreen(){
        System.out.print(ERASE_SCREEN);
    }
}
