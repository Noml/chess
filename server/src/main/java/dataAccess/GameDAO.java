package dataAccess;

import ModelTypes.GameData;
import server.Database;

public class GameDAO extends DAO {
    private GameData gameData;
    public GameDAO(Database db){
        super(db);
    }
}
