package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen extends PieceMovesCalculator{
    public Queen(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        board.addPiece(position,new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));//act like it's a rook
        Collection<ChessMove> possibleMoves = new ArrayList<>(super.pieceMoves(board, position));

        board.addPiece(position,new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.BISHOP));//act like it's a bishop
        possibleMoves.addAll(super.pieceMoves(board, position));

        board.addPiece(position,piece);//reset board with the queen
        return possibleMoves;
    }
}
