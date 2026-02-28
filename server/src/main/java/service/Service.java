package service;

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

    public void register() {



    }
}
