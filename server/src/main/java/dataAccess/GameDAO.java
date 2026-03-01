package dataAccess;

import model.GameData;
import server.Database;

public class GameDAO extends DAO {
    private GameData gameData;
    public GameDAO(Database db){
        super(db);
    }


    public void clearGameData(){
        db.deleteData(Database.DataType.GAMEDATA);
    }
}
