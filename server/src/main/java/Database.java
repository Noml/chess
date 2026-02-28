import ModelTypes.AuthData;
import ModelTypes.GameData;
import ModelTypes.UserData;
import org.eclipse.jetty.server.Authentication;

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

    public boolean userExists(String username){
        for( var i : allUserData){
            if(i.username().equals(username)){
                return true;
            }
        }
        return false;
    }


}
