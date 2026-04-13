package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) {
        var port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        var serverUrl = "http://localhost:"+port;
        System.out.println("Server started on "+ serverUrl);
        System.out.println("Welcome to chess!");
        try{
            new ChessClient(serverUrl).run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Thank you for playing chess! \n***Quitting***");
    }
}
