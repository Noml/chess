package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn extends PieceMovesCalculator{
    public Pawn(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int forward = 1;
        boolean promotion = false;
        if(piece.getTeamColor() == ChessGame.TeamColor.BLACK) forward = -1;//Black advances opposite of White
        if(position.getRow()+forward == 1 || position.getRow()+forward == 8) promotion = true;
        ChessPosition f     = new ChessPosition(position.getRow()+forward,position.getColumn());//forward 1
        ChessPosition fL    = new ChessPosition(position.getRow()+forward,position.getColumn()-1);//forward left
        ChessPosition fR    = new ChessPosition(position.getRow()+forward,position.getColumn()+1);//forward right
        ChessPosition ff    = new ChessPosition(position.getRow()+forward*2,position.getColumn());//forward 2
        if(f.isvalidPos() && board.getPiece(f) == null){//forward
            promotionLogic(position, possibleMoves, promotion, f);
            if(((position.getRow()==2 && piece.getTeamColor()== ChessGame.TeamColor.WHITE) ||
                    (position.getRow() == 7 && piece.getTeamColor() == ChessGame.TeamColor.BLACK)) && board.getPiece(ff) == null) {//move forward 2
                possibleMoves.add(new ChessMove(position,ff,null));
            }
        }if(fL.isvalidPos() && (board.getPiece(fL) != null && board.getPiece(fL).getTeamColor()!=piece.getTeamColor())) {//capture left
            promotionLogic(position, possibleMoves, promotion, fL);
        }if(fR.isvalidPos() && (board.getPiece(fR) != null && board.getPiece(fR).getTeamColor()!=piece.getTeamColor())) {//capture right
            promotionLogic(position, possibleMoves, promotion, fR);
        }
        return possibleMoves;
    }

    /**
     * When a pawn is about to promote, add the possible promotions in to the possible moves
     * @param promotion true if it can promote on movement, false otherwise
     * @param f final position in the ChessMove
     */
    private void promotionLogic(ChessPosition position, Collection<ChessMove> possibleMoves, boolean promotion, ChessPosition f) {
        if(!promotion){
            possibleMoves.add(new ChessMove(position,f,null));
        }else{
            possibleMoves.add(new ChessMove(position,f, ChessPiece.PieceType.KNIGHT));
            possibleMoves.add(new ChessMove(position,f, ChessPiece.PieceType.BISHOP));
            possibleMoves.add(new ChessMove(position,f, ChessPiece.PieceType.ROOK));
            possibleMoves.add(new ChessMove(position,f, ChessPiece.PieceType.QUEEN));
        }
    }


}
