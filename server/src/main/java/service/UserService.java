package service;

import ModelTypes.AuthData;
import ModelTypes.UserData;
import dataAccess.UserDAO;
import server.Database;
import service.requests.*;
import service.results.*;

public class UserService extends Service {
    private UserDAO uDAO;


    public UserService(Database db) {
        super(db);
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData userData = registerReqToUserData(registerRequest);
        uDAO = new UserDAO(db);
        UserData userDataResult = uDAO.getUser(userData);
        if(userDataResult == null){
            uDAO.createUser(userData);
            uDAO.addAuthData(new AuthData(generateAuthToken(),userData.username()));
        }else{
            //send status code for alreadyTakenException
        }

        return null;
    }
    public LoginResult login(LoginRequest loginRequest) {
        //take a username and password and check them against the database
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {
        //delete authToken
    }

    private UserData registerReqToUserData(RegisterRequest r){
        return new UserData(r.username(),r.password(),r.email());
    }
}

