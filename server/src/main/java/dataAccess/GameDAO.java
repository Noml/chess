package dataAccess;

import model.AuthData;
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

    public int getID(){
        return db.getAllGameData().size()+1;
    }

    public void addGameData(GameData gameData){
        db.addGameData(gameData);
    }

}
