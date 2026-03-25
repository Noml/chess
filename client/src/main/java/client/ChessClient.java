package client;


import service.requests.LoginRequest;
import service.requests.RegisterRequest;
import service.results.LoginResult;
import service.results.RegisterResult;

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
        Scanner scanner = new Scanner(System.in);
        switch(state){
            case PRELOGIN:
                System.out.println("Welcome to chess!");
                System.out.print(help());
                preloginRepl(scanner);
            case POSTLOGIN:
                System.out.print(help());
                postloginRepl(scanner);
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
            String input = scanner.nextLine();
            try{
                result = preloginEval(input);
                if(result!= "quit"){
                    System.out.println(result);
                }
            }catch (Throwable e){
                System.out.println(e.toString());
            }
            if(state == State.POSTLOGIN){
                System.out.println("Entering postlogin UI");
                run();
            }
        }
        System.out.println("Thank you for playing chess! \n***Quitting***");
    }

    private void postloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("logout")) {
            String input = scanner.nextLine();
            try{
                result = postloginEval(input);
                System.out.println(result);
            }catch (Throwable e){
                System.out.println(e.toString());
            }
            if(state == State.PRELOGIN){
                System.out.println("Entering prelogin UI");
                run();
            }
        }

    }

    private String preloginEval(String input){
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

    private String login(){
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
            state = State.POSTLOGIN;
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

    private String register(){
        String password;
        System.out.print("Enter a username: ");
        Scanner scan = new Scanner(System.in);
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
            return login();
        }
    }

    private String postloginEval(String input){
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
