package chess;

import java.util.Collection;

public class Queen extends PieceMovesCalculator{
    public Queen(ChessBoard board, ChessPosition position){
        super(board, position);
        this.type = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        return null;
    }
}
