package dataAccess;

import server.Database;

public abstract class DAO {
    protected Database db;
    public DAO(Database db){
        this.db = db;
    }
}
