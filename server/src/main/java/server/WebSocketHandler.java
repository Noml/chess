package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
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

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connectionManager;
    private final Gson gson;

    public WebSocketHandler(DatabaseManager dbM){
        authDAO = new AuthDAO(dbM);
        gameDAO = new GameDAO(dbM);
        connectionManager = new ConnectionManager();
        gson = new Gson();
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext){
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext wsConnectContext){
        wsConnectContext.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        UserGameCommand userGameCommand = new Gson().fromJson(ctx.message(),UserGameCommand.class);
        if(!isValid(userGameCommand)){
            ctx.send(gson.toJson(new ErrorMessage(ERROR, "Error: invalid information")));
            return;
        }
        int gameID = userGameCommand.getGameID();
        GameData gameData = gameDAO.findGame(gameID);
        ChessGame game = gameData.game();
        boolean playable = game.isPlayable();

        String auth = userGameCommand.getAuthToken();
        String username = authDAO.getAuthData(auth).username();
        String color = userGameCommand.getColor();
        if(color == null || color.isEmpty()){
            String b = gameData.blackUsername();
            String w = gameData.whiteUsername();
            if(w!= null && gameData.whiteUsername().equals(username)){
                color = "WHITE";
            }else if(b!= null && gameData.blackUsername().equals(username)){
                color = "BLACK";
            }else{
                color = "observing";
            }
        }
        Notification n;
        switch (userGameCommand.getCommandType()){
            case CONNECT:
                connectionManager.add(ctx.session,gameID);
                if(!color.equals("observing")){
                    n = new Notification(username + " joined as "+ color);
                }else{
                    n = new Notification(username + " is observing");
                }
                connectionManager.broadcast(ctx.session,n,gameID);
                ctx.send(gson.toJson(new LoadGameMessage(LOAD_GAME, gameData.game())));
                break;
            case LEAVE:
                String a = "";
                if(color.equals("observing")){
                    a = color;
                }
                n = new Notification(username + " left "+a);
                connectionManager.broadcast(ctx.session,n,gameID);
                connectionManager.remove(ctx.session,gameID);
                if(!color.equals("observing")){
                    gameDAO.removePlayer(gameID,color,username);
                }
                break;
            case RESIGN:
                if(!playable){
                    ErrorMessage errorMessage = new ErrorMessage(ERROR,"Error: tried to resign after game over");
                    ctx.send(gson.toJson(errorMessage));
                    break;
                }
                if(color.equals("observing")){
                    ErrorMessage errorMessage = new ErrorMessage(ERROR,"Error: tried to resign as observer");
                    ctx.send(gson.toJson(errorMessage));
                    break;
                }
                n = new Notification("User "+username + " resigned\n");
                connectionManager.broadcast(null,n,gameID);
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
                    makeMove(gameData, userGameCommand, color, ctx, connectionManager);
                    connectionManager.broadcast(null, new LoadGameMessage(LOAD_GAME, game),gameID);
                }catch (Exception e){
                    ctx.send(gson.toJson(new ErrorMessage(ERROR, e.getMessage())));
                }
                break;
        }
    }

    private boolean isValid(UserGameCommand userGameCommand){
        if(userGameCommand.getGameID() == null){
            return false;
        }else if(userGameCommand.getAuthToken() == null){
            return false;
        }
        try{
            GameData g = gameDAO.findGame(userGameCommand.getGameID());
            if(g == null){
                throw new Exception("Invalid gameID");
            }
            AuthData a = authDAO.getAuthData(userGameCommand.getAuthToken());
            String black = g.blackUsername();
            String white = g.whiteUsername();
            if(black != null && black.equals(a.username())){
                return true;
            }
            if(white != null && white.equals(a.username())){
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void makeMove(GameData gameData,
             UserGameCommand userGameCommand,
             String color,
             WsMessageContext ctx,
             ConnectionManager connectionManager)
                throws Exception {
        ChessBoard board = gameData.game().getBoard();
        int gameID = userGameCommand.getGameID();
        ChessGame game = gameData.game();
        boolean playable = game.isPlayable();
        String auth = userGameCommand.getAuthToken();
        String username = authDAO.getAuthData(auth).username();
        Notification n;
        if(color.equals("observing")){
            throw new Exception("Attempted to play as observer");
        }
        ChessMove move = userGameCommand.getMove();
        try{
            if(!playable){
                throw new Exception("unplayable game");
            }
            ChessPiece p = board.getPiece(move.getStartPosition());
            String s = p.toString();
            if(color.equals("WHITE") && p.getTeamColor() == ChessGame.TeamColor.BLACK
                ||color.equals("BLACK") && p.getTeamColor() == ChessGame.TeamColor.WHITE){
                throw new Exception("Attempted to play as other color");
            }
            game.makeMove(move);
            n = new Notification(username + " moved "+s + ": " + move);
            connectionManager.broadcast(ctx.session,n,gameID);
            if(game.isInCheck(ChessGame.TeamColor.BLACK)){
                n = new Notification("BLACK is in check!");
                if(game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    n = new Notification("BLACK is in checkmate!");
                    game.unplayable();
                }
                if(game.isInStalemate(ChessGame.TeamColor.BLACK)){
                    n = new Notification("BLACK is in stalemate!");
                    game.unplayable();
                }
                connectionManager.broadcast(null,n,gameID);
            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                n = new Notification("WHITE is in check!");
                if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    n = new Notification("WHITE is in checkmate!");
                    game.unplayable();
                }
                if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                    n = new Notification("WHITE is in stalemate!");
                    game.unplayable();
                }
                connectionManager.broadcast(null,n,gameID);
            }
            gameData = new GameData(gameID,gameData.whiteUsername(),
                    gameData.blackUsername(),gameData.gameName(),game);
            gameDAO.updateGame(gameData);
        }catch (Exception e){
            ErrorMessage r = new ErrorMessage(ERROR, "Error: "+ e.getMessage());
            ctx.send(gson.toJson(r));
        }
    }

}
