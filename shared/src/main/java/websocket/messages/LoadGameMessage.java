package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private ChessGame chessGame;
    public LoadGameMessage(ServerMessageType type, ChessGame chessGame) {
        super(type);
        this.chessGame = chessGame;
    }
    public ChessGame getChessGame(){
        return chessGame;
    }
}
