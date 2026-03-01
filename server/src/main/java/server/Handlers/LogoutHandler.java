package server.Handlers;

import com.google.gson.Gson;
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
    public void handle(@NotNull Context context) throws Exception {
        Gson gson = new Gson();
        LogoutRequest request = new LogoutRequest(context.header("authorization"));
        if(request == null || request.authToken() == null || request.authToken().isEmpty()){//invalid input
            context.status(500);//invalid input
            ErrorResponse r = new ErrorResponse("Error: bad request");
            context.result(gson.toJson(r));
            return;
        }
        boolean logoutCheck = service.logout(request);
        if(logoutCheck){
            context.status(200);
            context.result("{}");
        }else{
          context.status(401);
          ErrorResponse r = new ErrorResponse("Error: unauthorized");
          context.result(gson.toJson(r));
        }
    }
}
