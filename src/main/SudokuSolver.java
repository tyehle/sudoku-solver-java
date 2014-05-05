package main;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import ui.SudokuFrame;

/**
 *
 * @author Tobin
 */
public class SudokuSolver
{

    private static final int[][] EMPTY = 
    {
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1},
        {-1, -1, -1,   -1, -1, -1,   -1, -1, -1}
    };
    
    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        setBestLAF();
        
//        Scanner scanner = new Scanner(new File("puzzles.txt"));
//        ArrayList<Puzzle> puzzles = new ArrayList(250);
//        while(scanner.hasNext())
//        {
//            String s = scanner.next();
//            int[][] data = new int[Puzzle.cols][Puzzle.rows];
//            for(int i = 0; i < s.length(); i++)
//            {
//                int n = Integer.parseInt(s.charAt(i) + "");
//                n = (n == 0 ? -1 : n);
//                data[i / data.length][i % data.length] = n;
//            }
//            Puzzle p = new Puzzle(data);
//            puzzles.add(p);
//        }
//        
//        System.out.println("Starting");
//        Puzzle p = new Puzzle(EXAMPLE);
        
//        System.out.println("Timing ...");
//        System.out.println(time(p) + " seconds");
        
//        Puzzle p = puzzles.get(0);
        
        
//        System.out.println(p);
//        p.solve();
//        System.out.println(p);
        
        SudokuFrame frame = new SudokuFrame(new Puzzle(EMPTY));
        frame.setVisible(true);
    }
    
    public static double time(Puzzle p)
    {
        int times = 1000;
        
        // let the program warm up
        long end, mid, start = System.nanoTime();
        while(System.nanoTime() - start < 1000000000) { }
        
        ArrayList<Puzzle> puzzles = new ArrayList<>(times);
        // populate the array of puzzles
        for(int i = 0; i < times; i++)
        {
            puzzles.add(p.deepCopy());
        }
        
        start = System.nanoTime();
        
        for(int i = 0; i < times; i++)
        {
            puzzles.get(i).solve();
        }
        
        mid = System.nanoTime();
        
        for (int i = 0; i < times; i++) { }
        
        end = System.nanoTime();
        
        return (mid - start - (end - mid))/(double)times / 1e9;
    }
    
    /**
     * Set the best available look-and-feel into use.
     */
    public static void setBestLAF()
    {
        /*
         * Set the look-and-feel.  On Linux, Motif/Metal is sometimes incorrectly used
         * which is butt-ugly, so if the system l&f is Motif/Metal, we search for a few
         * other alternatives.
         */
        try
        {
            // Set system L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Check whether we have an ugly L&F
            LookAndFeel laf = UIManager.getLookAndFeel();
            if (laf == null || laf.getName().matches(".*[mM][oO][tT][iI][fF].*") || laf.getName().matches(".*[mM][eE][tT][aA][lL].*"))
            {

                // Search for better LAF
                UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();

                String lafNames[] =
                {
                        ".*[gG][tT][kK].*",
                        ".*[wW][iI][nN].*",
                        ".*[mM][aA][cC].*",
                        ".*[aA][qQ][uU][aA].*",
                        ".*[nN][iI][mM][bB].*"
                };

                lf: for (String lafName: lafNames)
                {
                    for (UIManager.LookAndFeelInfo l: info)
                    {
                        if (l.getName().matches(lafName))
                        {
                            UIManager.setLookAndFeel(l.getClassName());
                            break lf;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("Error setting LAF: " + e);
        }
    }
}
