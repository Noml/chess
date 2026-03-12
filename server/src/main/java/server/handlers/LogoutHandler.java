package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;
import service.UserService;
import service.requests.LogoutRequest;
import service.results.ErrorResponse;

public class LogoutHandler implements Handler {
    private UserService service;

    public LogoutHandler(Service service){
        this.service = new UserService(service);
    }
    @Override
    public void handle(@NotNull Context context){
        Gson gson = new Gson();
        LogoutRequest request = new LogoutRequest(context.header("authorization"));
        if(request.authToken() == null || request.authToken().isEmpty()){//invalid input
            context.status(500);//invalid input
            ErrorResponse r = new ErrorResponse("Error: bad request");
            context.result(gson.toJson(r));
            return;
        }
        try{
            service.logout(request);
            context.status(200);
            context.result("{}");
        }catch(DataAccessException e){
            new ErrorHandler(e,context);
        }
    }
}
