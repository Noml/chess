package client;


import java.util.Scanner;

public class ChessClient {
    enum State{
        PRELOGIN,POSTLOGIN
    }
    private ServerFacade server;
    private State state = State.PRELOGIN;

    public ChessClient(String serverUrl){
        server = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println("Welcome to chess!");
        help();
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String input = scanner.nextLine();
            try{
                evaluate(input);
            }catch (Throwable e){
                System.out.print(e.toString());
            }
        }
    }

    public void evaluate(String input){


    }
    public void help(){
        String help;
        System.out.print("Enter one of the following commands:");
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
        System.out.print(help);
    }

}
