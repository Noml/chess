package server;

import chess.*;

public class ServerMain {
    static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            port = new Server().run(port);
            System.out.printf("Server started on port %d with %s%n", port,null);
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                Chess Server:
                java ServerMain <port> [sql]
                """);
    }
}
