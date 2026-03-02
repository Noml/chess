package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.GameData;
import service.results.CreateGameResult;

import java.util.ArrayList;

public class GameService extends Service{
    private GameDAO gDAO;
    private AuthDAO aDAO;

    public GameService(Service s){
        super(s.getDb());
        gDAO = new GameDAO(db);
        aDAO = new AuthDAO(db);

    }

    public CreateGameResult createGame(String authToken, String gameName) throws Exception{
        AuthDAO aDAO = new AuthDAO(db);
        AuthData authData = aDAO.getAuthData(authToken);
        if(authData != null){
            int gameID = gDAO.getID();
            GameData gameData = new GameData(gameID,null,null,gameName,new ChessGame());
            gDAO.addGameData(gameData);
            return new CreateGameResult(gameID);
        }else{
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public ArrayList<GameData> listGames(String authToken) throws Exception{
        AuthData authData = aDAO.getAuthData(authToken);
        if(authData != null){
            return gDAO.getAllGameData(authToken);
        }else{
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public GameData joinGame(String authToken, String playerColor, int gameID) throws Exception{
        AuthData authData = aDAO.getAuthData(authToken);
        if(authData != null){
            GameData gameData = gDAO.findGame(gameID);
            switch (playerColor){
                case "BLACK":
                    if(gameData.blackUsername()!= null){
                        throw new DataAccessException("Error: already taken");
                    }
                case "WHITE":
                    if(gameData.whiteUsername() != null){
                        throw new DataAccessException("Error: already taken");
                    }
            }
            gameData = gDAO.addPlayer(gameID, playerColor, authData.username());
            return gameData;
        }else{
            throw new DataAccessException("Error: unauthorized");
        }

    }

}
