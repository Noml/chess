package dataAccess;

import model.AuthData;
import server.Database;

public class AuthDAO extends DAO {
    private AuthData authData;
    public AuthDAO(Database db){
        super(db);
    }

    public void clearAuthData(){
        db.deleteData(Database.DataType.AUTHDATA);
    }

}
