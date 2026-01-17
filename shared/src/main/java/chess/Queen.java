package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen extends PieceMovesCalculator{
    public Queen(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        board.addPiece(position,new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
        possibleMoves.addAll(super.pieceMoves(board, position));
        board.addPiece(position,new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.BISHOP));
        possibleMoves.addAll(super.pieceMoves(board, position));
        return possibleMoves;
    }
}
