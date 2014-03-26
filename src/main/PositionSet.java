package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author tobin
 */
public class PositionSet implements Iterable<Position>
{
    private static final int ROW = 0, COL = 1, BOX = 2, FDIAG = 3, BDIAG = 4;
    private int type;
    private int row, col;
    private final int boxSize;

    /**
     * Convenience constructor if you are not making a box set. Calls this(type,
     * row, col, -1)
     *
     * @param type The type of set to make
     * @param row The starting row
     * @param col The starting column
     */
    private PositionSet(int type, int row, int col)
    {
        this(type, row, col, -1);
        if (type == BOX)
        {
            throw new IllegalArgumentException(
                    "Box set requires a box size");
        }
        else
        {
            if ((type == FDIAG || type == BDIAG) && row != 1)
            {
                throw new IllegalArgumentException(
                        "Diagonal set requires row to be 1");
            }
        }
    }

    /**
     * Makes a new position set with the given starting row and column. The type
     * of set determines how the iterator will advance. The box size is only
     * needed if this is a BOX type set.
     *
     * @param type The type of set
     * @param row The starting row
     * @param col The starting column
     * @param boxSize The size of the boxes for this puzzle
     */
    private PositionSet(int type, int row, int col, int boxSize)
    {
        this.boxSize = boxSize;
        this.type = type;
        this.row = row;
        this.col = col;
    }

    /**
     * *** Factory Getters ****
     */
    public static PositionSet rowSet(int row)
    {
//                System.out.println("Rowset starting at " + row);
        return new PositionSet(ROW, row, 1);
    }

    public static PositionSet colSet(int col)
    {
//                System.out.println("Colset starting at " + col);
        return new PositionSet(COL, 1, col);
    }

    public static PositionSet boxSet(int row, int col, int boxSize)
    {
        int rowStart = ((row - 1) / boxSize) * boxSize + 1;
        int colStart = ((col - 1) / boxSize) * boxSize + 1;
//                System.out.println("Boxset starting at " + rowStart + ", " + colStart);
        return new PositionSet(BOX, rowStart, colStart, boxSize);
    }

    public static PositionSet fdiagSet(int row, int col)
    {
        if (row == col)
        {
            return new PositionSet(FDIAG, 1, 1);
        }
        else
        {
            return null;
        }
    }

    public static PositionSet bdiagSet(int row, int col)
    {
        if (row == 10 - col)
        {
            return new PositionSet(BDIAG, 1, 9);
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets a list of all sets the given position is in.
     *
     * @param row The row position
     * @param col The column position
     * @param boxSize The size of boxes for this puzzle
     * @return A list of all sets the position is in
     */
    public static List<PositionSet> getAllSets(int row, int col, int boxSize)
    {
        ArrayList<PositionSet> sets = new ArrayList<>(3);
        sets.add(rowSet(row));
        sets.add(colSet(col));
        sets.add(boxSet(row, col, boxSize));

        // add the diagonal sets
        if (Puzzle.diag)
        {
            PositionSet tmp_diag = fdiagSet(row, col);
            if (tmp_diag != null)
            {
                sets.add(tmp_diag);
            }
            tmp_diag = bdiagSet(row, col);
            if (tmp_diag != null)
            {
                sets.add(tmp_diag);
            }
        }
        return sets;
    }

    @Override
    public Iterator<Position> iterator()
    {
        return new Iterator<Position>()
        {
            @Override
            public boolean hasNext()
            {
                switch (type)
                {
                    case ROW:
                        return col <= Puzzle.cols;
                    case COL:
                        return row <= Puzzle.rows;
                    case BOX:
                        // get next will invalidate the data if it is
                        // done
                        return !(col == -1 || row == -1);
                    case FDIAG:
                    case BDIAG:
                        return row <= Puzzle.rows;
                    default:
                        throw new IllegalArgumentException(
                                "Unrecognized type: " + type);
                }
            }

            @Override
            public Position next()
            {
                switch (type)
                {
                    case ROW:
                        return new Position(row, col++);
                    case COL:
                        return new Position(row++, col);
                    case BOX:
                        // traverse box like reading
                        Position p = new Position(row, col);
                        if ((col - 1) % boxSize == boxSize - 1)
                        {
                            if ((row - 1) % boxSize == boxSize - 1)
                            {
                                // we are now done, so invalidate the
                                // position
                                row = col = -1;
                            }
                            else
                            {
                                row++;
                                col -= (boxSize - 1);
                            }
                        }
                        else
                        {
                            col++;
                        }
                        return p;
                    case FDIAG:
                        return new Position(row++, col++);
                    case BDIAG:
                        return new Position(row++, col--);
                    default:
                        throw new IllegalArgumentException(
                                "Unrecognized type: " + type);
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException(
                        "Cannot remove from a position set");
            }
        };
    }
}
