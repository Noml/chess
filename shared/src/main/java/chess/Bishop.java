package chess;

import java.util.Collection;



public class Bishop extends PieceMovesCalculator {
    public Bishop(ChessBoard board, ChessPosition position){
        super(board, position);
        this.type = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves;
        ChessPosition[] possibleEndPos;
        int j = 1;
        ChessPosition p = new ChessPosition(myPosition.getRow()+j,myPosition.getColumn()+j);
        while(board.getPiece(p)==null || board.getPiece(p) == ){
        }



        for (int i = 1; i < 8; i++) {
            if(myPosition.getRow()+i <=8 && myPosition.getColumn()+i<=8) {//move up and to the right
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(myPosition.getRow()+i,myPosition.getColumn()+i),null));
            }
            if(myPosition.getRow()-i >=1 && myPosition.getColumn()-i >=1){//move down and to the left

            }
            if(myPosition.getRow()+i >=8 && myPosition.getColumn()-i >=1){//move up and to the left

            }
            if(myPosition.getRow()-i >=1 && myPosition.getColumn()+i >=8){}//move down and to the right

        }
        new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()+1);
        ChessMove a = new ChessMove(myPosition,);

//      possibleMoves.add();



        return possibleMoves;
    }

}
