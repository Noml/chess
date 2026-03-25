package client;


import service.requests.LoginRequest;
import service.results.LoginResult;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class ChessClient {
    enum State{
        PRELOGIN,POSTLOGIN
    }
    private ServerFacade server;
    private State state = State.PRELOGIN;
    private String authToken;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to chess!");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        switch(state){
            case PRELOGIN:
                preloginRepl(scanner);
            case POSTLOGIN:
                postloginRepl(scanner);
        }
    }

    public String help(){
        String help;
        System.out.println("Enter one of the following valid commands:");
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

    public void preloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("quit")) {
            String input = scanner.nextLine();
            try{
                result = preloginEval(input);
                if(result!= "quit"){
                    System.out.println(result);
                }
            }catch (Throwable e){
                System.out.println(e.toString());
            }
        }
        System.out.println("Thank you for playing chess! \n***Quitting***");
    }

    public void postloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("logout")) {
            String input = scanner.nextLine();
            try{
                result = postloginEval(input);
                System.out.println(result);
            }catch (Throwable e){
                System.out.println(e.toString());
            }
        }
    }

    public String preloginEval(String input){
        try {
            String inputLower = input.toLowerCase();
            String cmd = "help";
            if(!inputLower.isEmpty()){
                cmd = inputLower;
            }
            return switch (cmd) {
                case "login" -> login();
                case "register" -> register();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String login(){
        String password;
        System.out.println("Enter your username: ");
        Scanner scan = new Scanner(System.in);
        String username = scan.nextLine();
        if(username.equals("quit")){
            return username;
        }
        System.out.println("Enter your password: ");
        password = scan.nextLine();
        try{
            LoginResult result = server.login(new LoginRequest(username,password));
            authToken = result.authToken();
            password = "";
            return "You logged on as: "+result.username();
        }catch(Exception e){
            if(e.getMessage().equals("Error: unauthorized")){
                System.out.println("No user was found with those credentials. Please try again or type quit for the username.\n");
            }
            else{
                System.out.println("    Error: "+e.toString());
            }
            return login();
        }
    }

    public String register(){

        return "";
    }

    public String postloginEval(String input){
        try {
            String inputLower = input.toLowerCase();
            String cmd = "help";
            if(!inputLower.isEmpty()){
                cmd = inputLower;
            }
            return switch (cmd) {
                case "logout" -> logout();
                case "create game" -> createGame();
                case "list games" -> listGames();
                case "play game" -> playGame();
                case "observe game" -> observeGame();
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String logout() {
        return "";
    }

    private String createGame() {
        return "";
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
}
