package dataaccess;

import model.UserData;
import server.Database;

public class UserDAO extends DAO{
    public UserDAO(DatabaseManager db){
        super(db);
    }
    public void clearUserData() throws DataAccessException {
        db.deleteData(DatabaseManager.DataType.USERDATA);
    }
    public void createUser(UserData userData) throws DataAccessException{
        db.addUserData(userData);
    }

    public UserData getUserByUsername(String username) throws DataAccessException{
        for( var i : db.getAllUserData()){
            if(i.username().equals(username)){
                return i;
            }
        }
        return null;
    }
}
