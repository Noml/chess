package dataAccess;

import model.AuthData;
import model.UserData;
import server.Database;

public class UserDAO extends DAO {

    public UserDAO(Database db){
        super(db);
    }

    public void createUser(UserData userData){
        db.addUserData(userData);
    }

    public void addAuthData(AuthData authData){
        db.addAuthData(authData);
    }

    public UserData getUserByUsername(String username){
        for( var i : db.getAllUserData()){
            if(i.username().equals(username)){
                return i;
            }
        }
        return null;
    }

    public void clearUserData(){
        db.deleteData(Database.DataType.USERDATA);
    }

}
