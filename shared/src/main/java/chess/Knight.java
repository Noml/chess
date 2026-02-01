package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight extends PieceMovesCalculator{
    public Knight(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>();
        ArrayList<ChessPosition> possibleEndPos = new ArrayList<>();
        //Look at all 8 possible locations, evaluate
        possibleEndPos.add(new ChessPosition(position.getRow()+2,position.getColumn()+1));//up2 right
        possibleEndPos.add(new ChessPosition(position.getRow()+2,position.getColumn()-1));//up2 left
        possibleEndPos.add(new ChessPosition(position.getRow()-2,position.getColumn()+1));//down2 right
        possibleEndPos.add(new ChessPosition(position.getRow()-2,position.getColumn()-1));//down2 left
        possibleEndPos.add(new ChessPosition(position.getRow()+1,position.getColumn()+2));//up right2
        possibleEndPos.add(new ChessPosition(position.getRow()-1,position.getColumn()+2));//down right2
        possibleEndPos.add(new ChessPosition(position.getRow()+1,position.getColumn()-2));//up left2
        possibleEndPos.add(new ChessPosition(position.getRow()-1,position.getColumn()-2));//down left2

        for(ChessPosition p : possibleEndPos){
            if(p.isvalidPos()){//see if it's on the board
                if(board.getPiece(p)== null) {
                    possibleMoves.add(new ChessMove(position,p,null));//empty
                }
                else if(board.getPiece(p).getTeamColor()!=piece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(position,p,null));//capture
                }
            }
        }
        return possibleMoves;
    }
}
