package dataaccess;

import server.Database;

public class UserDAO extends DAO{
    public UserDAO(DatabaseManager db){
        super(db);
    }
    public void clearUserData() throws DataAccessException {
        db.deleteData(DatabaseManager.DataType.USERDATA);
    }
}
