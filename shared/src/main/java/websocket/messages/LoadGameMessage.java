package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class LoadGameMessage extends ServerMessage {
    private ChessGame game;
    public LoadGameMessage(ServerMessageType type, ChessGame chessGame) {
        super(type);
        this.game = chessGame;
    }
    public ChessGame getChessGame(){
        return game;
    }
}
