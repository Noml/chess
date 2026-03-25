package client;


import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    enum State{
        PRELOGIN,POSTLOGIN
    }
    private ServerFacade server;
    private State state = State.PRELOGIN;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.print("Welcome to chess!");
        System.out.print(help());
        Scanner scanner = new Scanner(System.in);
        switch(state){
            case PRELOGIN:
                preloginRepl(scanner);
            case POSTLOGIN:
                postloginRepl(scanner);
        }
    }

    public void preloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("quit")) {
            String input = scanner.nextLine();
            try{
                result = preloginEval(input);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            }catch (Throwable e){
                System.out.print(e.toString());
            }
        }
    }

    public void postloginRepl(Scanner scanner){
        var result = "";
        while (!result.equals("logout")) {
            String input = scanner.nextLine();
            try{
                result = postloginEval(input);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            }catch (Throwable e){
                System.out.print(e.toString());
            }
        }
    }

    public String postloginEval(String input){
        return "";
    }

    public String preloginEval(String input){
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd;
            if(tokens.length > 0){
                cmd = tokens[0];
            }else{
                cmd = "help";
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

        return "";
    }

    public String register(){

        return "";
    }

    public String help(){
        String help;
        System.out.print("Enter one of the following valid commands:");
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

}
