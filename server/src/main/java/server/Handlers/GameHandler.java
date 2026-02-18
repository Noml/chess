package server.Handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class GameHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        var type = context.method();
        switch (type.toString()){
            case "POST":
                createGame(context);
                break;
            case "GET":
                listGames(context);
                break;
            case "PUT":
                joinGame(context);
                break;
            case null, default:
                context.status(404);
                context.result("Invalid game request");
        }

    }

    private void joinGame(Context cxt) throws Exception{
        cxt.result("Trying to join a game");
    }

    private void listGames(Context cxt) throws Exception{
        cxt.result("Trying to list games");
    }

    private void createGame(Context cxt) throws Exception{
        cxt.result("Trying to create a game");
    }

}
