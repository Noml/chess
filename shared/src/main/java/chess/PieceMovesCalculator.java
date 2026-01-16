package chess;

import java.util.Collection;

import static chess.ChessPiece.*;

public class PieceMovesCalculator {
    protected ChessPiece type;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position){
        this.type = board.getPiece(position);
    }

    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        switch(this.type){
            case PieceType.ROOK:
                return new Rook(board, position).pieceMoves(board, position);
            case PieceType.BISHOP:
                return new Bishop(board, position).pieceMoves(board, position);
            case PieceType.KING:
                return new King(board, position).pieceMoves(board, position);
            case PieceType.QUEEN:
                return new Queen(board, position).pieceMoves(board, position);
            case PieceType.KNIGHT:
                return new Knight(board, position).pieceMoves(board, position);
            case PieceType.PAWN:
                return new Pawn(board, position).pieceMoves(board, position);
            default:
                return null;
        }
    }



}
