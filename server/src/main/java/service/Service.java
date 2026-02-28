package service;

import java.util.UUID;

public class Service {

    public Service(){

    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    public void register() {



    }
}
