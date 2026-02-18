package server.Handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class RegisterHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.result("Trying to register an account");
    }
}
