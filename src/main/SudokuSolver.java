package main;

import java.util.HashSet;
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
    }
    
    public static class Puzzle
    {
        private final int rows = 9, cols = 9;
        
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
                    checkBounds(j, i);
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
         * @return The value in that cell.  If the cell is not filled returns a
         * -1.
         */
        public int get(int row, int col)
        {
            checkBounds(row, col);
            return data[col][row];
        }
        
        /**
         * Sets the entry at the given position to the given value.
         * @param row The row of the entry to set
         * @param col The column of the entry to set
         * @param n The value to set the the entry to
         * @throws IllegalArgumentException if the position is not valid, or if
         * the value is not valid
         */
        public void set(int row, int col, int n)
        {
            checkBounds(row, col);
            checkRange(n);
            data[col][row] = n;
        }
        
        
        public boolean solve()
        {
            
            return false;
        }
        
        /**
         * Only one number can fill a location.  TUNNEL OF LIGHTS!!!
         * @return if the puzzle has changed
         */
        private boolean solveBySink()
        {
            boolean changed = false;
            
            // loop through each box and rule out entries
            for(int row = 1; row < rows; row++)
            {
                for(int col = 1; col < cols; col++)
                {
                    Set<Integer> possible = new HashSet<>((int)(rows / .75));
                    for(int n = 1; n < rows; n++)
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
            return false;
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
            
            return  !isInRow(row, n) &&
                    !isInCol(col, n) &&
                    !isInBox(row, col, n);
        }
        
        /**
         * Tests if the given number is in the given row.
         * @param row the row to check
         * @param n the number to check
         * @return If the given number is in the row
         */
        private boolean isInRow(int row, int n)
        {
            for(int i = 1; i < cols; i++)
            {
                if(get(row, i) == n)
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Tests if the given number is in the given column.
         * @param col the column to check
         * @param n the number to check
         * @return If the given number is in the column
         */
        private boolean isInCol(int col, int n)
        {
            for(int i = 1; i < rows; i++)
            {
                if(get(i, col) == n)
                {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Tests if the given number is in the box containing the given
         * location.
         * @param row The row of the box to check
         * @param col The column of the box to check
         * @param n The number to check
         * @return If the number is contained in the given box
         */
        private boolean isInBox(int row, int col, int n)
        {
            int boxSize = (int)Math.sqrt(rows);
            int rowStart = ((row - 1) / boxSize) * boxSize + 1;
            int colStart = ((col - 1) / boxSize) * boxSize + 1;
            for(int i = rowStart; i < rowStart + boxSize; i++)
            {
                for(int j = colStart; j < colStart + boxSize; j++)
                {
                    if(get(i, j) == n)
                    {
                        return true;
                    }
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
            if(row < 0 || row >= rows)
            {
                throw new IllegalArgumentException("Illegal row number: "+ row);
            }
            if(col < 0 || col >= cols)
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
            if(n != -1 && n <= 0 && n > rows)
            {
                throw new IllegalArgumentException("Entries must be -1, " +
                        "or between 1 and " + rows + ": " + n);
            }
        }
    }
}
