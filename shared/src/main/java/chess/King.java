package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 *  This is a calculator for the possible moves of a King
 *
 *
 */
public class King extends PieceMovesCalculator{
    public King(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }


    protected Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>();
        ArrayList<ChessPosition> possibleEndPos = new ArrayList<>();
        //Look at all 8 possible locations, evaluate
        possibleEndPos.add(new ChessPosition(position.getRow()+1,position.getColumn()+1));//up right
        possibleEndPos.add(new ChessPosition(position.getRow()+1,position.getColumn()));//up
        possibleEndPos.add(new ChessPosition(position.getRow()+1,position.getColumn()-1));//up left
        possibleEndPos.add(new ChessPosition(position.getRow(),position.getColumn()-1));//left
        possibleEndPos.add(new ChessPosition(position.getRow(),position.getColumn()+1));//right
        possibleEndPos.add(new ChessPosition(position.getRow()-1,position.getColumn()-1));//down left
        possibleEndPos.add(new ChessPosition(position.getRow()-1,position.getColumn()));//down
        possibleEndPos.add(new ChessPosition(position.getRow()-1,position.getColumn()+1));//down right

        for(ChessPosition p : possibleEndPos){
            if(p.isvalidPos()){//see if it's on the board
                if(board.getPiece(p)== null){
                    possibleMoves.add(new ChessMove(position,p,null));//empty
                }
                else if(board.getPiece(p).getTeamColor()!=piece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(position, p, null));//capture
                }
            }
        }
        return possibleMoves;
    }
}
