package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import dataaccess.UserDAO;
import service.requests.*;
import service.results.*;

public class UserService extends Service {
    private UserDAO uDAO;
    private AuthDAO aDAO;

    public UserService(Service s){
        super(s.getDb());
        uDAO = new UserDAO(dbManager);
        aDAO = new AuthDAO(dbManager);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException{
        try{
            UserData userData = registerReqToUserData(registerRequest);
            UserData userDataResult = uDAO.getUserByUsername(userData.username());
            if(userDataResult == null){
                uDAO.createUser(userData);
                AuthData authData = new AuthData(generateAuthToken(),userData.username());
                aDAO.addAuthData(authData);
                return new RegisterResult(authData.username(),authData.authToken());
            }else{
                throw new DataAccessException("Error: already taken");
//                return new RegisterResult("Error", "Error: already taken");
            }
        }catch (DataAccessException e){
            throw new DataAccessException("Error: SQL error");
        }
    }
//    public LoginResult login(LoginRequest loginRequest) {
//        UserData userData = new UserData(loginRequest.username(), loginRequest.password(), null);
//        UserData userDataResult = uDAO.getUserByUsername(userData.username());
//        if(userDataResult != null){//found username in db
//            if(!userDataResult.password().equals(userData.password())){//mismatched password
//                return new LoginResult("Error", "Error: unauthorized");
//            }
//            AuthData authData = new AuthData(generateAuthToken(),userData.username());
//            aDAO.addAuthData(authData);
//            return new LoginResult(authData.username(),authData.authToken());
//        }else{
//            return new LoginResult("Error", "Error: unauthorized");
//        }
//    }

//    public boolean logout(LogoutRequest logoutRequest) {
//        AuthData authData = aDAO.getAuthData(logoutRequest.authToken());
//        if(authData != null){
//            return aDAO.deleteAuth(authData);
//        }else{
//            return false;//throw exception for unauthorized?
//        }
//    }

    private UserData registerReqToUserData(RegisterRequest r){
        return new UserData(r.username(),r.password(),r.email());
    }
}
