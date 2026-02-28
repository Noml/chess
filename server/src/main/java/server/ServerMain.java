package server;

import chess.*;
import service.Service;

public class ServerMain {
    static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
//            DataAccess dataAccess = new MemoryDataAccess();
//            if (args.length >= 2 && args[1].equals("sql")) {
//                dataAccess = new MySqlDataAccess();
//            }
            var service = new Service();//(dataAccess);
            port = new Server(service).run(port);
            System.out.printf("Server started on port %d with %s%n", port,null);//, dataAccess.getClass());
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                Pet Server:
                java ServerMain <port> [sql]
                """);
    }
}
