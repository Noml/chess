package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;
import service.UserService;
import service.requests.LoginRequest;
import service.results.ErrorResponse;
import service.results.LoginResult;

public class LoginHandler implements Handler {
    private UserService service;

    public LoginHandler(Service service){
        this.service = new UserService(service);
    }

    @Override
    public void handle(@NotNull Context context){
        Gson gson = new Gson();
        LoginRequest request = gson.fromJson(context.body(),LoginRequest.class);
        if(request.password() == null ||request.username() == null ||
            request.password().isEmpty() || request.username().isEmpty()){//invalid input
            context.status(400);//invalid input
            ErrorResponse r = new ErrorResponse("Error: bad request");
            context.result(gson.toJson(r));
            return;
        }
        try {
            LoginResult result = service.login(request);
            context.status(200);
            context.result(gson.toJson(result));
        } catch (DataAccessException e) {
            new ErrorHandler(e,context);
        }

    }
}
