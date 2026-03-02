package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.Database;

import java.util.UUID;

public class Service {
    protected Database db;
    public Service(Database db){
        this.db = db;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public boolean clear(){
        AuthDAO a = new AuthDAO(db);
        GameDAO g = new GameDAO(db);
        UserDAO u = new UserDAO(db);

        a.clearAuthData();
        g.clearGameData();
        u.clearUserData();
        return true;
    }

    public Database getDb(){
        return db;
    }
}
