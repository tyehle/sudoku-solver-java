package main;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tobin
 */
public class Puzzle
{
    public static final int rows = 9, cols = 9, boxSize = 3;
    
    public static boolean diag = false;
    /**
     * The array that stores the state of the puzzle
     */
    private int[][] data;
    private boolean solved;

    /**
     * Constructs a new puzzle with the given data. A -1 signifies an unknown
     * entry in the table. All entries in the array should be between -1 and 9
     *
     * @param data An array containing the state of the puzzle
     */
    public Puzzle(int[][] data)
    {
        /**
         * check validity *
         */
        for (int i = 0; i < data.length; i++)
        {
            for (int j = 0; j < data[i].length; j++)
            {
                // check location
                checkBounds(j + 1, i + 1);
                // check the contents of the array
                checkRange(data[i][j]);
            }
        }

        this.data = data;
        solved = false;
    }

    /**
     * Gets the contents of a cell in the puzzle.
     *
     * @param row The row to get
     * @param col The column to get
     * @throws IllegalArgumentException if the position is invalid
     * @return The value in that cell. If the cell is not filled returns a -1.
     */
    public int get(int row, int col)
    {
        checkBounds(row, col);
        return data[col - 1][row - 1];
    }

    /**
     * Gets the contents of a cell in the puzzle.
     *
     * @param p The position to get the value of
     * @throws IllegalArgumentException if the position is invalid
     * @return The value in that cell. If the cell is not filled returns a -1.
     */
    public int get(Position p)
    {
        return get(p.row, p.col);
    }

    /**
     * Sets the entry at the given position to the given value.
     *
     * @param row The row of the entry to set
     * @param col The column of the entry to set
     * @param n The value to set the the entry to
     * @throws IllegalArgumentException if the position is not valid, or if the
     * value is not valid
     */
    public void set(int row, int col, int n)
    {
        checkBounds(row, col);
        checkRange(n);
        data[col - 1][row - 1] = n;
    }

    /**
     * Sets the entry at the given position to the given value.
     *
     * @param p The position to set the value of
     * @param n The value to set the the entry to
     * @throws IllegalArgumentException if the position is not valid, or if the
     * value is not valid
     */
    public void set(Position p, int n)
    {
        set(p.row, p.col, n);
    }

    /**
     * @return If the puzzle is solved or not
     */
    public boolean isSolved()
    {
        return solved;
    }

    /**
     * Solves the puzzle.
     *
     * @return if the puzzle is solved successfully
     */
    public boolean solve()
    {
//            System.out.println(this);
        while (solveBySource() || solveBySink())
        {
            // wheee!
//                System.out.println(this);
        }
        
        // veryfiy the solution is valid
        verify();
        
        // we can solve no more, check the state
        for (int row = 1; row <= rows; row++)
        {
            for (int col = 1; col <= cols; col++)
            {
                int n = get(row, col);
                if (n == -1)
                {
                    // the puzzle is not solved
                    return false;
                }
            }
        }
        // all boxes have valid entries
        solved = true;
        return true;
    }
    
    private void verify()
    {
        for (int row = 1; row <= rows; row++)
        {
            for (int col = 1; col <= cols; col++)
            {
                int n = get(row, col);
                set(row, col, -1);
                if(n != -1 && !isLegal(row, col, n))
                {
                    // the puzzle is invalid
                    throw new IllegalStateException("("+row+", "+col+"):"+n+" invalid");
                }
                set(row, col, n);
            }
        }
    }

    /**
     * Only one number can fill a location. TUNNEL OF LIGHTS!!!
     *
     * @return if the puzzle has changed
     */
    private boolean solveBySink()
    {
//            System.out.println("TUNNEL OF LIGHTS");
        boolean changed = false;

        // loop through each box and rule out entries
        for (int row = 1; row <= rows; row++)
        {
            for (int col = 1; col <= cols; col++)
            {
                if (get(row, col) != -1)
                {
                    // there is already something in this box
                    continue;
                }

                Set<Integer> possible = new HashSet<>((int) (rows / .75));
                for (int n = 1; n <= rows; n++)
                {
                    if (isLegal(row, col, n))
                    {
                        possible.add(n);
                    }
                }

                if (possible.isEmpty())
                {
                    throw new IllegalStateException(
                            "Puzzle is now unsolvable!");
                }
                else
                {
                    if (possible.size() == 1)
                    {
                        set(row, col, possible.iterator().next());
                        changed = true;
                    }
                }
            }
        }

        return changed;
    }

    /**
     * A specific number can only go in one location. CALM ENERGY!!!!
     *
     * @return
     */
    private boolean solveBySource()
    {
//            System.out.println("CALM ENERGY");
        boolean changed = false;

        // loop through each position
        for (int col = 1; col <= cols; col++)
        {
            position:
            for (int row = 1; row <= rows; row++)
            {
                if (get(row, col) != -1)
                {
                    // we already know what is in this position
                    continue;
                }
                // check if each number can go anywhere else
                for (int n = 1; n <= rows; n++)
                {
                    if (!isLegal(row, col, n))
                    {
                        // we can't put this number here
                        continue;
                    }

                    for (PositionSet set :
                            PositionSet.getAllSets(row, col, boxSize))
                    {
                        Set<Position> possible = getPossible(n, set);
                        if (possible.size() == 1)
                        {
                            // there is only one possible position for n
                            set(possible.iterator().next(), n);
                            changed = true;
                            // go to the next posiiton
                            continue position;
                        }
                    }
                }
            }
        }

        return changed;
    }

    /**
     * Gets all possible positions of the given number in the set.
     *
     * @param n The number to check
     * @param set The set of positions to check
     * @return The set of positions the given number can fill in the given set
     */
    private Set<Position> getPossible(int n, PositionSet set)
    {
        Set<Position> possible = new HashSet<>((int) (rows / .75));

        for (Position p : set)
        {
            if (get(p) == -1 && isLegal(p.row, p.col, n))
            {
                possible.add(p);
            }
        }

        return possible;
    }

    /**
     * Tests if the given change to the puzzle is legal.
     *
     * @param row The row to change
     * @param col The col to change
     * @param n The number to change it to
     * @return If the change is legal
     */
    private boolean isLegal(int row, int col, int n)
    {
        for (PositionSet set : PositionSet.getAllSets(row, col, boxSize))
        {
            if (contains(set, n))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Tests if the given number appears in the given set of positions.
     *
     * @param set The set of positions to check
     * @param n The number to look for
     * @return If the number appears in the set of positions
     */
    private boolean contains(PositionSet set, int n)
    {
//            System.out.println(">>> contains");
        for (Position p : set)
        {
//                System.out.println(">>>> checking " + p);
            if (get(p) == n)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Throws an
     * <code>IllegalArgumentException</code> if the row or column is out of
     * bounds.
     *
     * @param row The row number
     * @param col The column number
     */
    private void checkBounds(int row, int col)
    {
        if (row <= 0 || row > rows)
        {
            throw new IllegalArgumentException("Illegal row number: " + row);
        }
        if (col <= 0 || col > cols)
        {
            throw new IllegalArgumentException(
                    "Illegal column number:" + col);
        }
    }

    /**
     * Tests if the given number is expected in the table. -1 signifies an
     * unknown entry.
     *
     * @param n
     */
    private void checkRange(int n)
    {
        if (n != -1 && (n <= 0 || n > rows))
        {
            throw new IllegalArgumentException("Entries must be -1, "
                    + "or between 1 and " + rows + ": " + n);
        }
    }

    @Override
    public String toString()
    {
        String rowSep = "+-------+-------+-------+\n";
        String out = rowSep;
        for (int row = 1; row <= rows; row++)
        {
            out += "|";
            for (int col = 1; col <= cols; col++)
            {
                int n = get(col, row);
//                    System.out.println(n);
                if (n == -1)
                {
                    out += " .";
                }
                else
                {
                    out += " " + n;
                }
                if ((col - 1) % boxSize == boxSize - 1)
                {
//                        System.out.println("putting |");
                    out += " |";
                }
            }
            out += "\n";
            if ((row - 1) % boxSize == boxSize - 1)
            {
//                    System.out.println("putting row break");
                out += rowSep;
            }
        }

        return out;
    }

    /**
     * Does a deep copy of this object. Changes to either the returned object or
     * the cloned object's data will not affect the other.
     */
    public Puzzle deepCopy()
    {
        // data must be square
        int[][] newData = new int[data.length][data.length];
        // manual arraycopy because I don't know if the built in handles the 2d arrays right
        for (int i = 0; i < newData.length; i++)
        {
            for (int j = 0; j < newData.length; j++)
            {
                newData[i][j] = data[i][j];
            }
        }
        return new Puzzle(newData);
    }
}
