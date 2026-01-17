package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook extends PieceMovesCalculator{
    public Rook(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(goUntilBlocked(board, position, 1, 0));//up
        possibleMoves.addAll(goUntilBlocked(board, position, -1, 0));//down
        possibleMoves.addAll(goUntilBlocked(board, position,0,-1));//left
        possibleMoves.addAll(goUntilBlocked(board, position,0,1));//right
        return possibleMoves;
    }
}
