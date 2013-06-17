package main;

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
        System.out.println("hello world!");
        Puzzle p = new Puzzle(EXAMPLE);
    }
    
    public static class Puzzle
    {
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
            return false;
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
         * Tests if the two positions are in the same box.  
         * @param rowA
         * @param colA
         * @param rowB
         * @param colB
         * @return 
         */
        public static boolean isInBox(int rowA, int colA, int rowB, int colB)
        {
            int boxSize = 3;
            return  (rowA - 1 % boxSize == rowB - 1 % boxSize) &&
                    (colA - 1 % boxSize == colB - 1 % boxSize);
        }
        
        /**
         * Throws an <code>IllegalArgumentException</code> if the row or column
         * is out of bounds.
         * @param row The row number
         * @param col The column number
         */
        private static void checkBounds(int row, int col)
        {
            if(row < 0 || row >= 9)
            {
                throw new IllegalArgumentException("Illegal row number: "+ row);
            }
            if(col < 0 || col >= 9)
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
        private static void checkRange(int n)
        {
            if(n != -1 && n <= 0 && n > 9)
            {
                throw new IllegalArgumentException(
                        "Entries must be -1, or between 1 and 9: " + n);
            }
        }
    }
}
