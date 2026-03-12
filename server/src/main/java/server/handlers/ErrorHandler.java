package server.handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.results.ErrorResponse;

public class ErrorHandler {
    private DataAccessException e;
    private Context c;
    Gson gson;

    public ErrorHandler(DataAccessException e, Context context){
        this.e = e;
        c = context;
        gson = new Gson();
        handleError();
    }

    public void handleError() {
        switch (e.getMessage()) {
            case "Error: unauthorized" -> c.status(401);
            case "Error: bad request" -> c.status(400);
            case "Error: already taken" -> c.status(403);
            default -> c.status(500);
        }
        ErrorResponse r = new ErrorResponse(e.getMessage());
        c.result(gson.toJson(r));
    }
}
