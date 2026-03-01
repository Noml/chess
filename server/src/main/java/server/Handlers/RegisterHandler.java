package server.Handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;
import service.UserService;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

import java.util.Objects;

public class RegisterHandler implements Handler {
    private UserService service;

    public RegisterHandler(UserService service){
        this.service = service;
    }
    @Override
    public void handle(@NotNull Context context) throws Exception {
        Gson gson = new Gson();
        RegisterRequest request = gson.fromJson(context.body(),RegisterRequest.class);
        if(request.email() == null || request.password() == null ||request.username() == null ||
            request.email().isEmpty() || request.password().isEmpty() || request.username().isEmpty()){

            context.status(400);//invalid input
            ErrorResponse r = new ErrorResponse("Error: bad request");
            context.result(gson.toJson(r));
            return;
        }
        RegisterResult result = service.register(request);
        if (result.username().equals("Error")){
            if(result.authToken().equals("Error: already taken")){
                context.status(403);
            }else{
                context.status(500);
            }
            ErrorResponse r = new ErrorResponse(result.authToken());
            context.result(gson.toJson(r));
            return;
        }
        context.status(200);
        context.result(gson.toJson(result));
    }
}
