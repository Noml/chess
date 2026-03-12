package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;
import service.UserService;
import service.requests.RegisterRequest;
import service.results.ErrorResponse;
import service.results.RegisterResult;

public class RegisterHandler implements Handler {
    private UserService service;

    public RegisterHandler(Service service){
        this.service = new UserService(service);
    }

    @Override
    public void handle(@NotNull Context context){
        Gson gson = new Gson();
        RegisterRequest request = gson.fromJson(context.body(),RegisterRequest.class);
        if(request.email() == null || request.password() == null ||request.username() == null ||
            request.email().isEmpty() || request.password().isEmpty() || request.username().isEmpty()){

            context.status(400);//invalid input
            ErrorResponse r = new ErrorResponse("Error: bad request");
            context.result(gson.toJson(r));
            return;
        }
        try{
            RegisterResult result = service.register(request);
            context.status(200);
            context.result(gson.toJson(result));

        }catch (DataAccessException e){
            new ErrorHandler(e,context);
        }
    }

}
