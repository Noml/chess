package server.Handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;

public class LogoutHandler implements Handler {
    private Service service;

    public LogoutHandler(Service service){
        this.service = service;
    }
    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.result("Trying to log out of an account");
    }
}
