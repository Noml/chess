package service;

import dataaccess.*;
import server.Database;

import java.util.UUID;

public class Service {
    protected Database db;
    protected DatabaseManager dbManager;
    public Service(Database db){
        this.db = db;
    }
    public Service(DatabaseManager dbManager){
        this.dbManager = dbManager;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public boolean clear() throws DataAccessException{
//        MemoryAuthDAO a = new MemoryAuthDAO(db);
//        MemoryGameDAO g = new MemoryGameDAO(db);
//        MemoryUserDAO u = new MemoryUserDAO(db);
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
