package dataaccess;

import model.AuthData;
import server.Database;

import java.util.ArrayList;

public class AuthDAO extends DAO{
    public AuthDAO(DatabaseManager db){
        super(db);
    }

    public void clearAuthData() throws DataAccessException{
        db.deleteData(DatabaseManager.DataType.AUTHDATA);
    }

    public void addAuthData(AuthData authData) throws DataAccessException{
        db.addAuthData(authData);
    }

    public AuthData getAuthData(String authToken) throws DataAccessException{
        ArrayList<AuthData> allAuthData = db.getAllAuthData();
        for(AuthData authData : allAuthData){
            if(authData.authToken().equals(authToken)){
                return authData;
            }
        }
        return null;
    }

    public void deleteAuth(AuthData authDataToDelete) throws DataAccessException{
        db.deleteData(authDataToDelete.authToken());
    }
}
