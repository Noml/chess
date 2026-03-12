package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.Service;
import service.results.ErrorResponse;

public class ClearHandler implements Handler {
    private Service service;
    private Gson gson;

    public ClearHandler(Service service){
        this.service = service;
    }
    @Override
    public void handle(@NotNull Context context) {
        gson = new Gson();
        try{
            service.clear();
            context.status(200);
            context.result("{}");
        }catch(DataAccessException e){
            context.status(500);
            ErrorResponse r = new ErrorResponse(e.getMessage());
            context.result(gson.toJson(r));
        }
    }
}
