package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tobin
 */
public class SudokuSolver
{
    private static final int[][] EXAMPLE = 
    {
        {7, -1, -1,   -1, 3, -1,   -1, 6, 1},
        {6, 1, -1,   -1, -1, 2,   -1, 3, -1},
        {-1, -1, 5,   1, -1, 6,   -1, -1, -1},
        
        {-1, -1, 3,   9, -1, -1,   -1, -1, 6},
        {-1, -1, 4,   -1, 7, -1,   -1, -1, 8},
        {-1, 6, -1,   -1, 5, -1,   4, -1, 2},
        
        {-1, 8, 9,   -1, 6, -1,   1, -1, -1},
        {-1, 7, -1,   2, -1, 5,   -1, -1, -1},
        {5, -1, -1,   8, -1, -1,   9, 2, -1}
    };
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Hello World!");
        Puzzle p = new Puzzle(EXAMPLE);
        p.solve();
    }
    
    public static class Puzzle
    {
        private static final int rows = 9, cols = 9, boxSize = 3;
        
        /** The array that stores the state of the puzzle */
        private int[][] data;
        
        /**
         * Constructs a new puzzle with the given data.  A -1 signifies an
         * unknown entry in the table.  All entries in the array should be
         * between -1 and 9
         * @param data An array containing the state of the puzzle
         */
        public Puzzle(int[][] data)
        {
            /** check validity **/
            for(int i = 0; i < data.length; i++)
            {
                for(int j = 0; j < data[i].length; j++)
                {
                    // check location
                    checkBounds(j + 1, i + 1);
                    // check the contents of the array
                    checkRange(data[i][j]);
                }
            }
            
            this.data = data;
        }
        
        /**
         * Gets the contents of a cell in the puzzle.
         * @param row The row to get
         * @param col The column to get
         * @throws IllegalArgumentException if the position is invalid
         * @return The value in that cell.  If the cell is not filled returns a
         * -1.
         */
        public int get(int row, int col)
        {
            checkBounds(row, col);
            return data[col - 1][row - 1];
        }
        
        /**
         * Gets the contents of a cell in the puzzle.
         * @param p The position to get the value of
         * @throws IllegalArgumentException if the position is invalid
         * @return The value in that cell.  If the cell is not filled returns a
         * -1.
         */
        public int get(Position p)
        {
            return get(p.row, p.col);
        }
        
        /**
         * Sets the entry at the given position to the given value.
         * @param row The row of the entry to set
         * @param col The column of the entry to set
         * @param n The value to set the the entry to
         * @throws IllegalArgumentException if the position is not valid, or if
         * the value is not valid
         */
        private void set(int row, int col, int n)
        {
            checkBounds(row, col);
            checkRange(n);
            data[col - 1][row - 1] = n;
        }
        
        /**
         * Sets the entry at the given position to the given value.
         * @param p The position to set the value of
         * @param n The value to set the the entry to
         * @throws IllegalArgumentException if the position is not valid, or if
         * the value is not valid
         */
        private void set(Position p, int n)
        {
            set(p.row, p.col, n);
        }
        
        /**
         * Solves the puzzle.
         * @return if the puzzle is solved successfully
         */
        public boolean solve()
        {
            System.out.println(this);
            while(solveBySource() || solveBySink())
            {
                // wheee!
                System.out.println(this);
            }
            
            // we can solve no more, check the state
            for(int row = 1; row <= rows; row++)
            {
                for(int col = 1; col <= cols; col++)
                {
                    int n = get(row, col);
                    if(n == -1 || !isLegal(row, col, n))
                    {
                        return false;
                    }
                }
            }
            // all boxes have valid entries
            return true;
        }
        
        /**
         * Only one number can fill a location.  TUNNEL OF LIGHTS!!!
         * @return if the puzzle has changed
         */
        private boolean solveBySink()
        {
            System.out.println("TUNNEL OF LIGHTS");
            boolean changed = false;
            
            // loop through each box and rule out entries
            for(int row = 1; row <= rows; row++)
            {
                for(int col = 1; col <= cols; col++)
                {
                    if(get(row, col) != -1)
                    {
                        // there is already something in this box
                        continue;
                    }
                    
                    Set<Integer> possible = new HashSet<>((int)(rows / .75));
                    for(int n = 1; n <= rows; n++)
                    {
                        if(isLegal(row, col, n))
                        {
                            possible.add(n);
                        }
                    }
                    
                    if(possible.isEmpty())
                    {
                        throw new IllegalStateException(
                                "Puzzle is now unsolvable!");
                    }
                    else if(possible.size() == 1)
                    {
                        set(row, col, possible.iterator().next());
                        changed = true;
                    }
                }
            }
            return changed;
        }
        
        /**
         * A specific number can only go in one location.  CALM ENERGY!!!!
         * @return 
         */
        private boolean solveBySource()
        {
            System.out.println("CALM ENERGY");
            boolean changed = false;
            
            // loop through each position
            for(int col = 1; col <= cols; col++)
            {
                position: for(int row = 1; row <= rows; row++)
                {
                    if(get(row, col) != -1)
                    {
                        // we already know what is in this position
                        continue;
                    }
                    // check if each number can go anywhere else
                    for(int n = 1; n <= rows; n++)
                    {
                        if(!isLegal(row, col, n))
                        {
                            // we can't put this number here
                            continue;
                        }
                        
                        for(PositionSet set :
                                PositionSet.getAllSets(row, col, boxSize))
                        {
                            Set<Position> possible = getPossible(n, set);
                            if(possible.size() == 1)
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
         * @param n The number to check
         * @param set The set of positions to check
         * @return The set of positions the given number can fill in the given
         * set
         */
        private Set<Position> getPossible(int n, PositionSet set)
        {
            Set<Position> possible = new HashSet<>((int)(rows / .75));
            
            for(Position p : set)
            {
                if(get(p) == -1 && isLegal(p.row, p.col, n))
                {
                    possible.add(p);
                }
            }
            
            return possible;
        }
        
        /**
         * Tests if the given change to the puzzle is legal.
         * @param row The row to change
         * @param col The col to change
         * @param n The number to change it to
         * @return If the change is legal
         */
        private boolean isLegal(int row, int col, int n)
        {
            // if the position is filled return false
            if(get(row, col) != -1)
            {
                return false;
            }
            
            for(PositionSet set : PositionSet.getAllSets(row, col, boxSize))
            {
                if(contains(set, n))
                {
                    return false;
                }
            }
            
            return true;
        }
        
        /**
         * Tests if the given number appears in the given set of positions.
         * @param set The set of positions to check
         * @param n The number to look for
         * @return If the number appears in the set of positions
         */
        private boolean contains(PositionSet set, int n)
        {
//            System.out.println(">>> contains");
            for(Position p : set)
            {
//                System.out.println(">>>> checking " + p);
                if(get(p) == n)
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Throws an <code>IllegalArgumentException</code> if the row or column
         * is out of bounds.
         * @param row The row number
         * @param col The column number
         */
        private void checkBounds(int row, int col)
        {
            if(row <= 0 || row > rows)
            {
                throw new IllegalArgumentException("Illegal row number: "+ row);
            }
            if(col <= 0 || col > cols)
            {
                throw new IllegalArgumentException(
                        "Illegal column number:" + col);
            }
        }
        
        /**
         * Tests if the given number is expected in the table.  -1 signifies an
         * unknown entry.  
         * @param n 
         */
        private void checkRange(int n)
        {
            if(n != -1 && (n <= 0 || n > rows))
            {
                throw new IllegalArgumentException("Entries must be -1, " +
                        "or between 1 and " + rows + ": " + n);
            }
        }
        
        
        @Override
        public String toString()
        {
            String rowSep = "+-------+-------+-------+\n";
            String out = rowSep;
            for(int row = 1; row <= rows; row++)
            {
                out += "|";
                for(int col = 1; col <= cols; col++)
                {
                    int n = get(col, row);
//                    System.out.println(n);
                    if(n == -1)
                    {
                        out += "  ";
                    }
                    else
                    {
                        out += " " + n;
                    }
                    if((col - 1) % boxSize == boxSize - 1)
                    {
//                        System.out.println("putting |");
                        out += " |";
                    }
                }
                out += "\n";
                if((row - 1) % boxSize == boxSize - 1)
                {
//                    System.out.println("putting row break");
                    out += rowSep;
                }
            }
            
            return out;
        }
        
        private static class PositionSet implements Iterable<Position>
        {
            private static final int ROW = 0, COL = 1, BOX = 2;
            
            private int type;
            private int row, col;
            private final int boxSize;
            
            /**
             * Convenience constructor if you are not making a box set.  Calls
             * this(type, row, col, -1)
             * @param type The type of set to make
             * @param row The starting row
             * @param col The starting column
             */
            private PositionSet(int type, int row, int col)
            {
                this(type, row, col, -1);
                if(type == BOX)
                {
                    throw new IllegalArgumentException(
                            "Box set requires a box size");
                }
            }
            
            /**
             * Makes a new position set with the given starting row and column.
             * The type of set determines how the iterator will advance.  The
             * box size is only needed if this is a BOX type set.
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
            
            /***** Factory Getters *****/
            private static PositionSet rowSet(int row)
            {
//                System.out.println("Rowset starting at " + row);
                return new PositionSet(ROW, row, 1);
            }
            
            private static PositionSet colSet(int col)
            {
//                System.out.println("Colset starting at " + col);
                return new PositionSet(COL, 1, col);
            }
            
            private static PositionSet boxSet(int row, int col, int boxSize)
            {
                int rowStart = ((row - 1) / boxSize) * boxSize + 1;
                int colStart = ((col - 1) / boxSize) * boxSize + 1;
//                System.out.println("Boxset starting at " + rowStart + ", " + colStart);
                return new PositionSet(BOX, rowStart, colStart, boxSize);
            }
            
            /**
             * Gets a list of all sets the given position is in.
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
                return sets;
            }
            
            @Override
            public Iterator<Position> iterator()
            {
                return new Iterator<Position>() {

                    @Override
                    public boolean hasNext()
                    {
                        switch(type)
                        {
                            case ROW:
                                return col <= Puzzle.cols;
                            case COL:
                                return row <= Puzzle.rows;
                            case BOX:
                                // get next will invalidate the data if it is
                                // done
                                return  !(col == -1 || row == -1);
                            default:
                                throw new IllegalArgumentException(
                                        "Unrecognized type: " + type);
                        }
                    }

                    @Override
                    public Position next()
                    {
                        switch(type)
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
                                    if((row - 1) % boxSize == boxSize - 1)
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
        
        /**
         * Represents a position in the puzzle.  This class contains two
         * fields; row, and col.  These fields cannot be changed.  (This is
         * functionally a tuple, but java doesn't have one booo!)
         */
        public static class Position
        {
            // These are final fields, so it is safe for the public to
            // access them
            public final int row, col;
            /**
             * Make a new position with the given row and column.
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
    }
}
