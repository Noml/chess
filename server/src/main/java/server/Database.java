package server;

import ModelTypes.AuthData;
import ModelTypes.GameData;
import ModelTypes.UserData;

import java.util.ArrayList;

public class Database {
    private ArrayList<GameData> allGameData;
    private ArrayList<UserData> allUserData;
    private ArrayList<AuthData> allAuthData;

    public Database(){

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

    public UserData getUserByUsername(String username){
        for( var i : allUserData){
            if(i.username().equals(username)){
                return i;
            }
        }
        return null;
    }


}
