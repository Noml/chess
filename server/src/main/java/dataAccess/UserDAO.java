package dataAccess;

import ModelTypes.AuthData;
import ModelTypes.UserData;
import server.Database;

public class UserDAO extends DAO {

    public UserDAO(Database db){
        super(db);
    }

    public UserData getUser(UserData userData){
        return db.getUserByUsername(userData.username());
    }

    public void createUser(UserData userData){
        db.addUserData(userData);
    }

    public void addAuthData(AuthData authData){
        db.addAuthData(authData);
    }

}
