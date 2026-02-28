package service;

import ModelTypes.UserData;
import dataAccess.UserDAO;
import org.eclipse.jetty.server.Authentication;
import service.requests.*;
import service.results.*;

public class UserService extends Service {
    private UserDAO uDAO;

    public UserService() {

    }

    public RegisterResult register(RegisterRequest registerRequest) {
        uDAO = new UserDAO(registerReqToUserData(registerRequest));
        uDAO.getUser();

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

