package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Bishop extends PieceMovesCalculator {
    public Bishop(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(goUntilBlocked(board, position, 1, 1));//up right
        possibleMoves.addAll(goUntilBlocked(board, position, 1, -1));//up left
        possibleMoves.addAll(goUntilBlocked(board, position,-1,1));//down right
        possibleMoves.addAll(goUntilBlocked(board, position,-1,-1));//down left
        return possibleMoves;
    }

}

