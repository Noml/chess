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
        board.resetBoard();//set the board to the starting setup
        setTeamTurn(TeamColor.WHITE);//start with White
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
        Collection<ChessMove> validM = new ArrayList<>();
        for(ChessMove m : piece.pieceMoves(board, startPosition)){
            ChessBoard newBoard = new ChessBoard(board);
            newBoard.addPiece(m.getStartPosition(), null);
            newBoard.addPiece(m.getEndPosition(), new ChessPiece(piece.getTeamColor(), piece.getPieceType()));//act like the king is there
            ChessGame possibleMove = new ChessGame();
            possibleMove.setBoard(newBoard);
            if(!possibleMove.isInCheck(piece.getTeamColor())){
                validM.add(m);//if moving there doesn't put your king in check, it's a valid move
            }
        }
        return validM;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        if(board.getPiece(start) == null){
            throw new InvalidMoveException("No piece at " + start.toString());
        }
        if(board.getPiece(start).getTeamColor() != currentTurn){
            throw new InvalidMoveException("Can't move a different colored piece.");
        }
        ChessPiece piece = board.getPiece(start);
        Collection<ChessMove> possibleMoves = validMoves(start);
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for(ChessMove m : possibleMoves){
            endPositions.add(m.getEndPosition());
        }
        if(!endPositions.contains(end)){
            throw new InvalidMoveException("Invalid end position");
        }
        if(move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(),move.getPromotionPiece());
        }
        board.addPiece(end, piece);
        board.addPiece(start,null);
        if(currentTurn == TeamColor.BLACK){
            setTeamTurn(TeamColor.WHITE);
        }else{
            setTeamTurn(TeamColor.BLACK);
        }
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
        ChessPiece kingInCheck = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        ArrayList<ChessPosition> piecePositions = getAllPiecePositions();
        Map<ChessPosition, Collection<ChessMove>> boardMoves = getAllMoves(piecePositions);
        ChessPosition kingInCheckPos = getKingPosition(piecePositions,teamColor);
        Collection<ChessMove> kingMoves = validMoves(kingInCheckPos);
        if(!kingMoves.isEmpty()){
            return false;//If the king can move, then it's not in check
        }
        for(ChessPosition pos : piecePositions){
            if(board.getPiece(pos).getTeamColor() == teamColor && !kingInCheck.equals(board.getPiece(pos))){
                //only look at your team's piece color (and disregard the king's moves)
                Collection<ChessMove> pieceMoves = validMoves(pos);
                if(!pieceMoves.isEmpty()){
                    return false;//If any piece can move, then it's not in check
                }
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
        if(isInCheck(teamColor)){
            return false;
        }
        ArrayList<ChessPosition> piecePositions = this.getAllPiecePositions();
        ChessPosition kingPos = getKingPosition(piecePositions,teamColor);
        Map<ChessPosition, Collection<ChessMove>> boardMoves = this.getAllMoves(piecePositions);
        for(ChessPosition pos : piecePositions){
            if(board.getPiece(pos).getTeamColor() == teamColor){//only look at your team's piece color
                Collection<ChessMove> pieceMoves = validMoves(pos);
                if(!pieceMoves.isEmpty()){
                    return false;//If any piece can move, then it's not in stalemate
                }
            }
        }
        return true;

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
            ChessPiece piece = board.getPiece(p);
            boardMoves.put(p,piece.pieceMoves(board, p));
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
        return new ChessPosition();//invalid position
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }
}