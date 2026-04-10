package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private DatabaseManager dbM;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private ConnectionManager connectionManager;
    private Gson gson;


    public WebSocketHandler(DatabaseManager dbM){
        this.dbM = dbM;
        authDAO = new AuthDAO(dbM);
        gameDAO = new GameDAO(dbM);
        connectionManager = new ConnectionManager();
        gson = new Gson();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext) throws Exception {
        wsConnectContext.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(),UserGameCommand.class);
        if(!isValid(userGameCommand)){
            ctx.send("Error: invalid information");
            return;
        }
        int gameID = userGameCommand.getGameID();
        GameData gameData = gameDAO.findGame(gameID);
        String auth = userGameCommand.getAuthToken();
        String username = authDAO.getAuthData(auth).username();
        String color = userGameCommand.getColor();
        if(color.isEmpty()){
            ctx.send("Error: no color added");
            return;
        }
        Notification n;
        switch (userGameCommand.getCommandType()){
            case CONNECT:
                connectionManager.add(ctx.session);
                if(!color.equals("observing")){
                    n = new Notification(username + " joined as "+ color);
                }else{
                    n = new Notification(username + " is observing");
                }
                    connectionManager.broadcast(ctx.session,n);
                ctx.send(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gson.toJson(gameData)));
                break;
            case LEAVE:
                String a = "";
                if(color.equals("observing")){
                    a = color;
                }
                n = new Notification(username + "left "+a);
                connectionManager.broadcast(ctx.session,n);
                connectionManager.remove(ctx.session);
                if(!color.equals("observing")){
                    gameDAO.removePlayer(gameID,color,username);
                }
                break;
            case RESIGN:
                n = new Notification(username + "resigned");
                connectionManager.broadcast(ctx.session,n);
                //make it so that you can't do any more moves

                break;
            case MAKE_MOVE:
                //print who moved what
                //update board on each screen
                //if in check/checkmate, broadcast who is in it.
                break;
        }

        System.out.println("WS Response: " + ctx.message());
    }

    private boolean isValid(UserGameCommand userGameCommand){
        if(userGameCommand.getGameID() == null){
            return false;
        }else return userGameCommand.getAuthToken() != null;
    }

    private String findColor(GameData gameData, String username){
        if(gameData.blackUsername().equals(username)){
            return "BLACK";
        } else if (gameData.whiteUsername().equals(username)) {
            return "WHITE";
        }
        return "";
    }
}
