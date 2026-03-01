package server.Handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;

public class ClearHandler implements Handler {
    private Service service;
    private Gson gson;

    public ClearHandler(Service service){
        this.service = service;
        gson = new Gson();
    }
    @Override
    public void handle(@NotNull Context context) throws Exception {
        service.clear();
        context.status(200);
        context.result("{}");
    }
}
