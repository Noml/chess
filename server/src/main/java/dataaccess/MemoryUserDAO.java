//package dataaccess;
//
//import model.UserData;
//import server.Database;
//
//public class MemoryUserDAO extends UserDAO {
//
//    public MemoryUserDAO(Database db){
//        super(db);
//    }
//
//    public void createUser(UserData userData){
//        db.addUserData(userData);
//    }
//
//    public UserData getUserByUsername(String username){
//        for( var i : db.getAllUserData()){
//            if(i.username().equals(username)){
//                return i;
//            }
//        }
//        return null;
//    }
//
//    public void clearUserData(){
//        db.deleteData(Database.DataType.USERDATA);
//    }
//
//}
