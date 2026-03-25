package client;

import chess.*;
import server.Server;

public class ClientMain {
    public static void main(String[] args) {
        var port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        var serverUrl = "http://localhost:"+port;
        System.out.println("Server started on "+ serverUrl);
        new ChessClient(serverUrl).run();
    }
}
