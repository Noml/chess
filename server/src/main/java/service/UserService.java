package service;

import dataAccess.AuthDAO;
import model.AuthData;
import model.UserData;
import dataAccess.UserDAO;
import org.eclipse.jetty.server.Authentication;
import server.Database;
import service.requests.*;
import service.results.*;

public class UserService extends Service {
    private UserDAO uDAO;
    private AuthDAO aDAO;

    public UserService(Service s){
        super(s.getDb());
        uDAO = new UserDAO(db);
        aDAO = new AuthDAO(db);
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData userData = registerReqToUserData(registerRequest);
        UserData userDataResult = uDAO.getUserByUsername(userData.username());
        if(userDataResult == null){
            uDAO.createUser(userData);
            AuthData authData = new AuthData(generateAuthToken(),userData.username());
            aDAO.addAuthData(authData);
            return new RegisterResult(authData.username(),authData.authToken());
        }else{
            return new RegisterResult("Error", "Error: already taken");
        }
    }
    public LoginResult login(LoginRequest loginRequest) {
        UserData userData = new UserData(loginRequest.username(), loginRequest.password(), null);
        UserData userDataResult = uDAO.getUserByUsername(userData.username());
        if(userDataResult != null){//found username in db
            if(!userDataResult.password().equals(userData.password())){//mismatched password
                return new LoginResult("Error", "Error: unauthorized");
            }
            AuthData authData = new AuthData(generateAuthToken(),userData.username());
            aDAO.addAuthData(authData);
            return new LoginResult(authData.username(),authData.authToken());
        }else{
            return new LoginResult("Error", "Error: unauthorized");
        }
    }

    public boolean logout(LogoutRequest logoutRequest) {
        AuthData authData = aDAO.getAuthData(logoutRequest.authToken());
        if(authData != null){
            return aDAO.deleteAuth(authData);
        }else{
            return false;//throw exception for unauthorized?
        }
    }


    private UserData registerReqToUserData(RegisterRequest r){
        return new UserData(r.username(),r.password(),r.email());
    }
}
