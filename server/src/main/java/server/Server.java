package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import io.javalin.*;
import server.handlers.*;
import service.Service;

public class Server {
    private Service service;
    private final Javalin javalin;
    private DatabaseManager dbManager;

    public Server() {
        try{
            dbManager = new DatabaseManager();
        }catch(DataAccessException e){
            System.out.println(e.getMessage());
        }
        service = new Service(dbManager);
        javalin = Javalin.create(config -> config.staticFiles.add("/web"));
        javalin.post("/user", new RegisterHandler(service))
                .delete("/db", new ClearHandler(service))
                .post("/session", new LoginHandler(service))
                .delete("/session", new LogoutHandler(service))
                .get("/game", new GameHandler(service))
                .post("/game", new GameHandler(service))
                .put("/game", new GameHandler(service));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
