package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import dataaccess.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
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
            }
        }catch (DataAccessException e){
            if(e.getMessage().equals("Error: already taken")){
                throw e;
            }
            throw new DataAccessException("Error: SQL error");
        }
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData userData = new UserData(loginRequest.username(), loginRequest.password(), null);
        try {
            UserData userDataResult = uDAO.getUserByUsername(userData.username());
            if (userDataResult != null) {//found username in db
                String hashedPassword = userDataResult.password();
                String providedClearTextPassword = loginRequest.password();
                if (!BCrypt.checkpw(providedClearTextPassword, hashedPassword)) {//mismatched password
                    throw new DataAccessException("Error: unauthorized");
                }
                AuthData authData = new AuthData(generateAuthToken(), userData.username());
                aDAO.addAuthData(authData);
                return new LoginResult(authData.username(), authData.authToken());
            } else {
                throw new DataAccessException("Error: unauthorized");
            }
        }catch(DataAccessException e){
            if(e.getMessage().equals("Error: unauthorized")){
                throw e;
            }
            throw new DataAccessException("Error: SQL error");
        }
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        try{
            AuthData authData = aDAO.getAuthData(logoutRequest.authToken());//could throw an exception
            if(authData != null){
                aDAO.deleteAuth(authData);
            }else{
                throw new DataAccessException("Error: unauthorized");
            }
        } catch (DataAccessException e) {
            if(e.getMessage().equals("Error: unauthorized")){
                throw e;
            }
            throw new DataAccessException("Error: SQL error");
        }
    }

    private UserData registerReqToUserData(RegisterRequest r){
        return new UserData(r.username(),r.password(),r.email());
    }
}
