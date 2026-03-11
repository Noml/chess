package dataaccess;

import server.Database;

public class GameDAO extends DAO{
    public GameDAO(DatabaseManager db){
        super(db);
    }
    public void clearGameData() throws DataAccessException {
        db.deleteData(DatabaseManager.DataType.GAMEDATA);
    }

}
