package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.Database;

public class UserDAO extends DAO{
    public UserDAO(DatabaseManager db){
        super(db);
    }
    public void clearUserData() throws DataAccessException {
        db.deleteData(DatabaseManager.DataType.USERDATA);
    }
    public void createUser(UserData userData) throws DataAccessException{
        String clearTextPassword = userData.password();
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        UserData secure = new UserData(userData.username(),hashedPassword,userData.email());
        db.addUserData(secure);
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
