package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.PieceType;

public class PieceMovesCalculator {
    protected ChessPiece piece;

    public PieceMovesCalculator(ChessBoard board, ChessPosition position){
        this.piece = board.getPiece(position);
    }

    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        return switch (board.getPiece(position).getPieceType()) {//call pieceMoves on whichever piece it is
            case PieceType.ROOK -> new Rook(board, position).pieceMoves(board, position);
            case PieceType.BISHOP -> new Bishop(board, position).pieceMoves(board, position);
            case PieceType.KING -> new King(board, position).pieceMoves(board, position);
            case PieceType.QUEEN -> new Queen(board, position).pieceMoves(board, position);
            case PieceType.KNIGHT -> new Knight(board, position).pieceMoves(board, position);
            case PieceType.PAWN -> new Pawn(board, position).pieceMoves(board, position);
        };
    }

    /**
     * Iterate through moves in a direction until you hit a wall or a piece
     * This is for Bishop, Rook, and Queen calculators
     * @param y {1,-1,0} up, down, or no movement
     * @param x {1,-1,0} right, left, or no movement
     **/
    protected Collection<ChessMove> goUntilBlocked(ChessBoard board, ChessPosition myPosition, int y, int x){//iterate until blocked (for Bishop, Rook, Queen)
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        if(x==y && y==0) {
            return possibleMoves;//Empty if no movement
        }
        ChessPosition p = new ChessPosition(myPosition.getRow()+y,myPosition.getColumn()+x);
        int j=1;//multiplication factor for moves
        while(p.isvalidPos() && board.getPiece(p) == null){
            possibleMoves.add(new ChessMove(myPosition,p,null));//pawns don't need iterated moves
            j++;
            p = new ChessPosition(myPosition.getRow()+y*j,myPosition.getColumn()+x*j);//Look at the next position in the desired direction
        }
        //Look one step farther
        if(p.isvalidPos() && board.getPiece(p).getTeamColor() != piece.getTeamColor()) possibleMoves.add(new ChessMove(myPosition,p,null));

        return possibleMoves;
    }

}
