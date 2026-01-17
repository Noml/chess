package chess;

import java.util.Collection;
import java.util.List;

public class King extends PieceMovesCalculator{
    public King(ChessBoard board, ChessPosition position){
        super(board, position);
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>();
        ChessPosition[] possibleEndPos = new ChessPosition[8];
        possibleEndPos[0] = new ChessPosition(position.getRow()+1,position.getColumn()+1);//up right
        possibleEndPos[1] = new ChessPosition(position.getRow()+1,position.getColumn());//up
        possibleEndPos[2] = new ChessPosition(position.getRow()+1,position.getColumn()-1);//up left
        possibleEndPos[3] = new ChessPosition(position.getRow(),position.getColumn()-1);//left
        possibleEndPos[4] = new ChessPosition(position.getRow(),position.getColumn()+1);//right
        possibleEndPos[5] = new ChessPosition(position.getRow()-1,position.getColumn()-1);//down left
        possibleEndPos[6] = new ChessPosition(position.getRow()-1,position.getColumn());//down
        possibleEndPos[7] = new ChessPosition(position.getRow()-1,position.getColumn()+1);//down right

        for(ChessPosition p : possibleEndPos){
            if(p.isvalidPos()){//see if it's on the board
                if(board.getPiece(p)== null) possibleMoves.add(new ChessMove(position,p,null));//empty
                else if(board.getPiece(p).getTeamColor()!=piece.getTeamColor()) possibleMoves.add(new ChessMove(position,p,null));//capture
            }
        }
        return possibleMoves;
    }
}
