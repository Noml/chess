package dataAccess;

import ModelTypes.UserData;

public class UserDAO {
    private UserData userData;

    public UserDAO(UserData u){
        userData = u;
    }

    public UserData getUserData() {
        return userData;
    }

    public UserData getUser(){

    }
}
