package dataaccess;

import server.Database;

public class AuthDAO extends DAO{
    public AuthDAO(DatabaseManager db){
        super(db);
    }

    public void clearAuthData() throws DataAccessException{
        db.deleteData(DatabaseManager.DataType.AUTHDATA);
    }

}
