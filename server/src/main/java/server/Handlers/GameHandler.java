package server.Handlers;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.Service;
import service.requests.LogoutRequest;
import service.results.CreateGameResult;
import service.results.ErrorResponse;

import java.util.ArrayList;
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
        Gson gson = new Gson();
        String authToken = context.header("authorization");
        record JoinRequest(String playerColor, int gameID){}
        JoinRequest joinRequest  = gson.fromJson(context.body(), JoinRequest.class);
        if(joinRequest == null || joinRequest.playerColor == null ||
                !(joinRequest.playerColor.equals("WHITE") || joinRequest.playerColor.equals("BLACK")) ||
                 authToken == null || authToken.isEmpty() || joinRequest.gameID <1){
            ErrorResponse r = new ErrorResponse("Error: bad request");
            context.status(400);
            context.result(gson.toJson(r));
            return;
        }
        String playerColor = joinRequest.playerColor;
        int gameID = joinRequest.gameID;
        try {
            record JoinGameResult(GameData gameData) { }
            JoinGameResult j = new JoinGameResult(service.joinGame(authToken, playerColor, gameID));
            context.status(200);
            context.result(gson.toJson(j));

        }catch(Exception e){
            if(e.getMessage() == "Error: bad request"){
                context.status(400);
            }else if(e.getMessage() == "Error: unauthorized"){
                context.status(401);
            }else if(e.getMessage() == "Error: already taken"){
                context.status(403);
            }
            ErrorResponse r = new ErrorResponse(e.getMessage());
            context.result(gson.toJson(r));
        }



    }

    private void listGames(Context context) throws Exception{
        Gson gson = new Gson();
        String authToken = context.header("authorization");
        if(authToken == null){
            context.status(401);
            ErrorResponse r = new ErrorResponse("Error: unauthorized");
            context.result(gson.toJson(r));
        }
        try {
            record listedGames(ArrayList<GameData> games) { }
            listedGames l = new listedGames(service.listGames(authToken));
            context.status(200);
            context.result(gson.toJson(l));

        }catch(Exception e){
            context.status(401);
            ErrorResponse r = new ErrorResponse(e.getMessage());
            context.result(gson.toJson(r));
        }
//
//
//        Gson gson = new Gson();
//        String authToken = context.header("authorization");
//        Map m = gson.fromJson(context.body(), Map.class);
//        if(m == null || m.get("playerColor") == null ||
//                !(m.get("playerColor") == "WHITE" || m.get("playerColor") == "BLACK") ||
//                authToken == null || authToken.isEmpty()){
//            ErrorResponse r = new ErrorResponse("Error: bad request");
//            context.status(400);
//            context.result(gson.toJson(r));
//            return;
//        }
    }

    private void createGame(Context context) throws Exception{
        Gson gson = new Gson();
        String authToken = context.header("authorization");
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
