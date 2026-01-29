package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        this.currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ArrayList<ChessPosition> piecePositions = this.getAllPiecePositions();
        ChessPosition kingPos = getKingPosition(piecePositions,teamColor);
        Map<ChessPosition, Collection<ChessMove>> boardMoves = this.getAllMoves(piecePositions);
        if(!kingPos.isvalidPos()){
            return false; //no king found
        }
        for(ChessPosition pos : piecePositions){
            if(board.getPiece(pos).getTeamColor() != teamColor){
                for(ChessMove m : boardMoves.get(pos)){
                    if(m!= null && kingPos.equals(m.getEndPosition())){
                        return true;//if any piece from the other team can attack the king, it is in check
                    }
                }
            }
        }
        return false;//if no piece threatens the king
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        ArrayList<ChessPosition> piecePositions = getAllPiecePositions();
        Map<ChessPosition, Collection<ChessMove>> boardMoves = getAllMoves(piecePositions);
        ChessPosition kingInCheckPos = getKingPosition(piecePositions,teamColor);
        for(ChessMove kM : boardMoves.get(kingInCheckPos)){
            boolean validMove = true;
            for(ChessPosition p : piecePositions){
                for(ChessMove m : boardMoves.get(p)){
                    if(kM.getEndPosition() == m.getEndPosition()){
                        validMove = false;
                        break;
                    }
                }
            }
            if(validMove){
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ArrayList<ChessPosition> getAllPiecePositions(){
        ArrayList<ChessPosition> piecePositions = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPosition pos = new ChessPosition(i+1,j+1);
                if(board.getPiece(pos) != null){
                    piecePositions.add(pos);
                }
            }
        }
        return piecePositions;
    }

    private Map<ChessPosition, Collection<ChessMove>> getAllMoves(Collection<ChessPosition> piecePositions){
        Map<ChessPosition, Collection<ChessMove>> boardMoves = new HashMap<>();
        for(ChessPosition p : piecePositions){
            boardMoves.put(p,validMoves(p));
        }
        return boardMoves;
    }

    private ChessPosition getKingPosition(Collection<ChessPosition> piecePositions, TeamColor teamColor){
        ChessPiece king = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        for(ChessPosition p : piecePositions){
            if(king.equals(board.getPiece(p))){
                return p;
            }
        }
        return new ChessPosition();//invalid
    }

}
