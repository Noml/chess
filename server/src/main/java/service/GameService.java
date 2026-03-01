package service;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.GameData;
import service.results.CreateGameResult;

public class GameService extends Service{
    private GameDAO gDAO;

    public GameService(Service s){
        super(s.getDb());
        gDAO = new GameDAO(db);
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

}
