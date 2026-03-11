//package dataaccess;
//
//import model.GameData;
//import server.Database;
//
//import java.util.ArrayList;
//
//public class MemoryGameDAO extends GameDAO {
//    public MemoryGameDAO(Database db){
//        super(db);
//    }
//
//
//    public void clearGameData(){
//        db.deleteData(Database.DataType.GAMEDATA);
//    }
//
//    public int getID(){
//        return db.getAllGameData().size()+1;
//    }
//
//    public void addGameData(GameData gameData){
//        db.addGameData(gameData);
//    }
//
//    public ArrayList<GameData> getAllGameData(){
//        return db.getAllGameData();
//    }
//
//    public GameData findGame(int gameID){
//        for(GameData d : db.getAllGameData()){
//            if(gameID == d.gameID()){
//                return d;
//            }
//        }
//        return null;
//    }
//
//    public GameData addPlayer(int gameID, String playerColor, String username){
//        ArrayList<GameData> allGameData = db.getAllGameData();
//        for (GameData g : allGameData) {
//            if (gameID == g.gameID()) {
//                switch (playerColor) {
//                    case "BLACK":
//                        g = new GameData(gameID, g.whiteUsername(), username, g.gameName(), g.game());
//                        break;
//                    case "WHITE":
//                        g = new GameData(gameID, username, g.blackUsername(), g.gameName(), g.game());
//                        break;
//                }
//                updateGame(g);
//                return g;
//            }
//        }
//        return null;
//    }
//
//    public void updateGame(GameData d){
//        var allGameData = db.getAllGameData();
//        allGameData.set(d.gameID()-1,d);
//    }
//
//}
