package server.Handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;

public class ClearHandler implements Handler {
    private Service service;

    public ClearHandler(Service service){
        this.service = service;
    }
    @Override
    public void handle(@NotNull Context context) throws Exception {
        context.result("Trying to clear all data");
    }
}
