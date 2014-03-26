package main;

/**
 * Represents a position in the puzzle. This class contains two fields; row, and
 * col. These fields cannot be changed. (This is functionally a tuple, but java
 * doesn't have one booo!)
 * @author tobin
 */
public class Position
{
    // These are final fields, so it is safe for the public to
    // access them

    public final int row, col;

    /**
     * Make a new position with the given row and column.
     *
     * @param row The row
     * @param col The column
     */
    public Position(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString()
    {
        return "(" + row + ", " + col + ")";
    }
}