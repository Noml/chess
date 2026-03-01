package server.Handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.Service;
import service.requests.LogoutRequest;
import service.results.CreateGameResult;
import service.results.ErrorResponse;

import java.util.Map;

public class GameHandler implements Handler {
    private GameService service;

    public GameHandler(Service service){
        this.service = new GameService(service);
    }
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

    private void joinGame(Context context) throws Exception{
        context.result("Trying to join a game");
    }

    private void listGames(Context context) throws Exception{
        context.result("Trying to list games");
    }

    private void createGame(Context context) throws Exception{
        Gson gson = new Gson();
        String authToken = context.header("authorization");
        record GetGameName(String gameName){}
        Map m = gson.fromJson(context.body(), Map.class);
        if(m == null || m.get("gameName") == null || authToken == null || authToken.isEmpty()){
            ErrorResponse r = new ErrorResponse("Error: bad request");
            context.status(400);
            context.result(gson.toJson(r));
            return;
        }
        String gameName = m.get("gameName").toString();
        try{
            CreateGameResult gameResult =  service.createGame(authToken,gameName);
            context.status(200);
            context.result(gson.toJson(gameResult));
        }catch (Exception e){
            context.status(401);
            ErrorResponse r = new ErrorResponse(e.getMessage());
            String s = gson.toJson(r);
            context.result(gson.toJson(r));

        }
    }

}
