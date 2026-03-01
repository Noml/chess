package server;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class Database {
    private ArrayList<GameData> allGameData;
    private ArrayList<UserData> allUserData;
    private ArrayList<AuthData> allAuthData;
    public Database(){
        allGameData = new ArrayList<>();
        allUserData = new ArrayList<>();
        allAuthData = new ArrayList<>();
    }

    public void addUserData(UserData u){
        allUserData.add(u);
    }
    public void addGameData(GameData g){
        allGameData.add(g);
    }
    public void addAuthData(AuthData a){
        allAuthData.add(a);
    }

    public ArrayList<UserData> getAllUserData(){
        return allUserData;
    }

    public ArrayList<AuthData> getAllAuthData() {
        return allAuthData;
    }

    public ArrayList<GameData> getAllGameData() {
        return allGameData;
    }

    public void deleteData(DataType type){
        switch (type){
            case GAMEDATA -> allGameData.clear();
            case AUTHDATA -> allAuthData.clear();
            case USERDATA -> allUserData.clear();
        }
    }

    public enum DataType{
        GAMEDATA,
        AUTHDATA,
        USERDATA;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Database database = (Database) o;
        return Objects.equals(allGameData, database.allGameData) && Objects.equals(allUserData, database.allUserData) && Objects.equals(allAuthData, database.allAuthData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allGameData, allUserData, allAuthData);
    }
}

