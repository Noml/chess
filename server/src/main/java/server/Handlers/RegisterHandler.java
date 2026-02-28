package server.Handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;
import service.UserService;
import service.requests.RegisterRequest;

import java.util.Objects;

public class RegisterHandler implements Handler {
    private UserService service;

    public RegisterHandler(UserService service){
        this.service = service;
    }
    @Override
    public void handle(@NotNull Context context) throws Exception {
        RegisterRequest request = new Gson().fromJson(context.body(),RegisterRequest.class);
        if(Objects.equals(request.email(), "") || Objects.equals(request.password(), "") || Objects.equals(request.username(), "")){
            context.status(400);//invalid input
            context.result("{ \"message\": \"Error: bad request\" }");
            return;
        }
        service.register(request);

        context.result("Trying to register an account");
    }
}
