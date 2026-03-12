package service;

import dataaccess.*;

import java.util.UUID;

public class Service {
    protected DatabaseManager dbManager;
    public Service(DatabaseManager dbManager){
        this.dbManager = dbManager;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public boolean clear() throws DataAccessException{
        UserDAO u = new UserDAO(dbManager);
        AuthDAO a = new AuthDAO(dbManager);
        GameDAO g = new GameDAO(dbManager);

        try{
            a.clearAuthData();
            g.clearGameData();
            u.clearUserData();
            return true;
        }catch(DataAccessException e){
            throw new DataAccessException("Error: "+e.getMessage());
        }
    }

    public DatabaseManager getDb(){
        return dbManager;
    }
}
