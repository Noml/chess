package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Database;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

public class ServiceUnitTests {
    private Service service;
    private Database db;

    @BeforeEach
    public void setUp(){
        db = new Database();
        service = new Service(db);
    }

    @Test
    public void testClear(){
        Database cleared = new Database();
        service.clear();
        Assertions.assertEquals(cleared, db);
    }

    @Test
    public void addUser(){
        UserService s =  new UserService(service);
        RegisterRequest  req = new RegisterRequest("Joenathan","12345","na@gmail.com");
        RegisterResult result = s.register(req);
        RegisterResult expected = new RegisterResult("Joenathan","a1u2t3h4T5o6k7e8nEX");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expected.username(),result.username());
        Assertions.assertNotNull(result.authToken());
    }

//    @Test
//    public void reRegister(){
//        UserService s =  new UserService(service);
//        RegisterRequest  req = new RegisterRequest("Joenathan","12345","na@gmail.com");
//        RegisterRequest  req2 = new RegisterRequest("Joenathan","12345","na@gmail.com");
//        RegisterResult result = s.register(req);
//        RegisterResult result2 = s.register(req2);
//
//
//    }

}
