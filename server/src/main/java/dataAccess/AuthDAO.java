package dataAccess;

import ModelTypes.AuthData;
import server.Database;

public class AuthDAO extends DAO {
    private AuthData authData;
    public AuthDAO(Database db){
        super(db);
    }

}
