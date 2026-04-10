package dataaccess;

import model.GameData;

import java.util.ArrayList;

public class GameDAO extends DAO{
    public GameDAO(DatabaseManager db){
        super(db);
    }
    public void clearGameData() throws DataAccessException {
        db.deleteData(DatabaseManager.DataType.GAMEDATA);
    }
    public int getNewID() throws DataAccessException{
        return db.getAllGameData().size()+1;
    }

    public void addGameData(GameData gameData) throws DataAccessException{
        if(gameData == null){
            throw new DataAccessException("Error: null input");
        }
        db.addGameData(gameData);
    }

    public ArrayList<GameData> getAllGameData() throws DataAccessException{
        return db.getAllGameData();
    }

    public GameData findGame(int gameID) throws DataAccessException{
        for(GameData d : db.getAllGameData()){
            if(gameID == d.gameID()){
                return d;
            }
        }
        return null;
    }

    public GameData addPlayer(int gameID, String playerColor, String username) throws DataAccessException{
        ArrayList<GameData> allGameData = db.getAllGameData();
        for (GameData g : allGameData) {
            if (gameID == g.gameID()) {
                switch (playerColor) {
                    case "BLACK":
                        g = new GameData(gameID, g.whiteUsername(), username, g.gameName(), g.game());
                        break;
                    case "WHITE":
                        g = new GameData(gameID, username, g.blackUsername(), g.gameName(), g.game());
                        break;
                    case null, default:
                        return null;
                }
                db.updateGame(g);
                return g;
            }
        }
        return null;
    }

    public GameData removePlayer(int gameID, String playerColor, String username) throws DataAccessException{
        ArrayList<GameData> allGameData = db.getAllGameData();
        for (GameData g : allGameData) {
            if (gameID == g.gameID()) {
                switch (playerColor) {
                    case "BLACK":
                        g = new GameData(gameID, g.whiteUsername(), null, g.gameName(), g.game());
                        break;
                    case "WHITE":
                        g = new GameData(gameID, null, g.blackUsername(), g.gameName(), g.game());
                        break;
                    case null, default:
                        return null;
                }
                db.updateGame(g);
                return g;
            }
        }
        return null;
    }

    public void updateGame(GameData g) throws DataAccessException {
        db.updateGame(g);
    }


}
