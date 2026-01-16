package chess;

import java.util.Collection;

public class King extends PieceMovesCalculator{
    public King(ChessBoard board, ChessPosition position){
        super(board, position);
        this.type = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return null;
    }
}
