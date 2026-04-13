package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.util.ArrayList;

import static websocket.messages.ServerMessage.ServerMessageType.*;

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
//        System.out.println("Message recieved");
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(),UserGameCommand.class);
        if(!isValid(userGameCommand)){
            ctx.send(gson.toJson(new ErrorMessage(ERROR, "Error: invalid information")));
            return;
        }
        int gameID = userGameCommand.getGameID();
        GameData gameData = gameDAO.findGame(gameID);
        ChessBoard board = gameData.game().getBoard();
        ChessGame game = gameData.game();
        boolean playable = game.isPlayable();

        String auth = userGameCommand.getAuthToken();
        String username = authDAO.getAuthData(auth).username();
        String color = userGameCommand.getColor();
        if(color == null || color.isEmpty()){
            ctx.send(gson.toJson(new ErrorMessage(ERROR, "Error: no color added")));
            //Edit to not throw an error. Assign color based on authtoken and gameID.
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
                ctx.send(gson.toJson(new LoadGameMessage(LOAD_GAME, gameData.game())));
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
                game.unplayable();
                gameData = new GameData(gameID,
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game);
                gameDAO.updateGame(gameData);
                break;
            case MAKE_MOVE:
                try {
                    ArrayList<Notification> notifications = makeMove(gameData, userGameCommand);
                    for(Notification x : notifications){
                        connectionManager.broadcast(ctx.session,x);
                    }
                    ctx.send(gson.toJson(new LoadGameMessage(LOAD_GAME, game)));
                }catch (Exception e){
                    ctx.send(gson.toJson(new ErrorMessage(ERROR, e.getMessage())));
                }
                break;
        }
    }

    private boolean isValid(UserGameCommand userGameCommand){
        if(userGameCommand.getGameID() == null){
            return false;
        }else return userGameCommand.getAuthToken() != null;
    }

    private ArrayList<Notification> makeMove(GameData gameData, UserGameCommand userGameCommand) throws Exception {
        ChessBoard board = gameData.game().getBoard();
        int gameID = userGameCommand.getGameID();
        ChessGame game = gameData.game();
        boolean playable = game.isPlayable();
        String auth = userGameCommand.getAuthToken();
        String username = authDAO.getAuthData(auth).username();
        String color = userGameCommand.getColor();
        Notification n;
        ArrayList<Notification> notifications = new ArrayList<>();

        ChessMove move = userGameCommand.getMove();
        try{
            if(!playable){
                throw new Exception("unplayable game");
            }
            game.makeMove(move);
            ChessPiece p = board.getPiece(move.getStartPosition());
            n = new Notification(username + " moved "+p.toString() + ": " + move.toString());
            notifications.add(n);
//            connectionManager.broadcast(ctx.session,n);
            if(game.isInCheck(ChessGame.TeamColor.BLACK)){
                n = new Notification("BLACK is in check!");
//                connectionManager.broadcast(ctx.session,n);
            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                n = new Notification("WHITE is in check!");
//                connectionManager.broadcast(ctx.session,n);
            }
            notifications.add(n);
            if(game.isInCheckmate(ChessGame.TeamColor.BLACK)){
                n = new Notification("BLACK is in checkmate!");
                game.unplayable();
//                connectionManager.broadcast(ctx.session,n);
            } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                n = new Notification("WHITE is in checkmate!");
//                connectionManager.broadcast(ctx.session,n);
                game.unplayable();
            }
            notifications.add(n);
            if(game.isInStalemate(ChessGame.TeamColor.BLACK)){
                n = new Notification("BLACK is in stalemate!");
//                connectionManager.broadcast(ctx.session,n);
                game.unplayable();
            } else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                n = new Notification("WHITE is in stalemate!");
//                connectionManager.broadcast(ctx.session,n);
                game.unplayable();
            }
            gameData = new GameData(gameID,gameData.whiteUsername(),
                    gameData.blackUsername(),gameData.gameName(),game);
            gameDAO.updateGame(gameData);

        }catch (Exception e){
            n = new Notification(gson.toJson(new ErrorMessage(ERROR, "Error: "+ e.getMessage())));
            throw new Exception(n.message());
        }
        return null;
    }

}
