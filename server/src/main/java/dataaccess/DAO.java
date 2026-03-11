package dataaccess;

import server.Database;

public abstract class DAO {
    protected DatabaseManager db;
    protected Database dbMemory;
    public DAO(DatabaseManager db){
        this.db = db;
    }
//    public DAO(Database db){
//        this.dbMemory = db;
//    }
}
