package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.*;

public class PieceMovesCalculator {
    protected ChessPiece piece;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position){
        this.piece = board.getPiece(position);
    }

    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        switch(board.getPiece(position).getPieceType()){
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

    protected Collection<ChessMove> goUntilBlocked(ChessBoard board, ChessPosition myPosition, int y, int x){//iterate until blocked (Bishop, Rook, Queen)
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int j=1;//factor for moves
        ChessPosition p = new ChessPosition(myPosition.getRow()+y*j,myPosition.getColumn()+x*j);//Up Right
        while(p.isvalidPos() && board.getPiece(p) == null){
            possibleMoves.add(new ChessMove(myPosition,p,null));
            j++;
            p = new ChessPosition(myPosition.getRow()+y*j,myPosition.getColumn()+x*j);
        }
        if(p.isvalidPos() && board.getPiece(p).getTeamColor() != piece.getTeamColor()){//Look one further
            possibleMoves.add(new ChessMove(myPosition,p,null));
        }

        return possibleMoves;
    }

}
