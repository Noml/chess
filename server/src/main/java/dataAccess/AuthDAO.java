package dataAccess;

import model.AuthData;
import org.jetbrains.annotations.NotNull;
import server.Database;

import java.util.ArrayList;

public class AuthDAO extends DAO {
    public AuthDAO(Database db){
        super(db);
    }

    public void clearAuthData(){
        db.deleteData(Database.DataType.AUTHDATA);
    }

    public void addAuthData(AuthData authData){
        db.addAuthData(authData);
    }

    public AuthData getAuthData(@NotNull String authToken){
        ArrayList<AuthData> allAuthData = db.getAllAuthData();
        for(AuthData authData : allAuthData){
            if(authData.authToken().equals(authToken)){
                return authData;
            }
        }
        return null;
    }

    public boolean deleteAuth(AuthData authDataToDelete){
        ArrayList<AuthData> allAuthData = db.getAllAuthData();
        for(AuthData authData : allAuthData){
            if(authData.authToken().equals(authDataToDelete.authToken())){
                allAuthData.remove(authData);
                return true;
            }
        }
        return false;//not found, shouldn't happen because of earlier check
    }
}
