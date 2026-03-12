package dataaccess;

public abstract class DAO {
    protected DatabaseManager db;
    public DAO(DatabaseManager db){
        this.db = db;
    }
}
