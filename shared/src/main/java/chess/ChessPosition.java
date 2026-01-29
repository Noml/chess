package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    private int row;
    private int col;
    private boolean validPos;

    public ChessPosition(){
        this.row = -1;
        this.col = -1;
        this.validPos = false;
    }

    public ChessPosition(int row, int col) {
        if(row<=8 && row >=1) {
            this.row = row;
            this.validPos = true;
        }else return;
        if(col<=8 && col >=1) {
            this.col = col;
        }else this.validPos = false;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return this.col;
    }
    
    public boolean isvalidPos(){
        return validPos;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        if(validPos){
            return String.format("[%d,%d]",row,col);
        }
        else {
            return String.format("Invalid: [%d,%d]", row, col);//Show what the invalid position would be
        }
    }
}
