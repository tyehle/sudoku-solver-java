package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import main.Position;
import main.PositionSet;
import main.Puzzle;

/**
 *
 * @author tobin
 */
public class SudokuFrame extends JFrame
{

    private final int boxSpace = 10, cellSpace = 2;
    private JButton solve;
    private ArrayList<BoxButton> board;
    private BoxButton pressed = null;

    public SudokuFrame(Puzzle puzzle) throws HeadlessException
    {
        super("Sudoku");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        solve = new JButton("Solve");
        solve.addActionListener(new SolveHandler(puzzle));

        board = new ArrayList<>(Puzzle.rows * Puzzle.cols);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, boxSpace, boxSpace));
        for (int row = 1; row < Puzzle.rows; row += Puzzle.boxSize)
        {
            for (int col = 1; col < Puzzle.cols; col += Puzzle.boxSize)
            {
                PositionSet s = PositionSet.boxSet(row, col, Puzzle.boxSize);
                JPanel thisBox = new JPanel(new GridLayout(Puzzle.boxSize, Puzzle.boxSize, cellSpace, cellSpace));
                for (Position p : s)
                {
                    BoxButton b = new BoxButton(p, puzzle);
                    board.add(b);
                    // Careful! This requires the order of the position set and
                    // the grid layout to be the same. Which they are. Hehe.
                    thisBox.add(b);
                }

                boardPanel.add(thisBox);
            }
        }

        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        add(boardPanel, BorderLayout.CENTER);
        add(solve, BorderLayout.SOUTH);

        pack();

        updateBoard(puzzle);
    }

    private void updateBoard(Puzzle p)
    {
        System.out.println("Updating the board using\n" + p);
        for (BoxButton b : board)
        {
            b.setValue(p.get(b.position));
        }
    }

    private class BoxButton extends JToggleButton
    {

        private final Position position;
        private Puzzle puzzle;

        public BoxButton(Position position, Puzzle puzzle)
        {
            super("");
            this.position = position;
            this.puzzle = puzzle;
            BoxButtonHandler h = new BoxButtonHandler();
            addActionListener(h);
            addKeyListener(h);

            // the default width for a single digit
            setPreferredSize(new Dimension(41, 26));
        }

        public void setValue(int n)
        {
            puzzle.set(position, n);
            if (n == -1)
            {
                setText("");
            } else
            {
                setText(n + "");
            }
        }

        private class BoxButtonHandler implements ActionListener, KeyListener
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
//                System.out.println(position + " button pressed\n" + getPreferredSize());

//                // unpress all other buttons
//                for (BoxButton b : board)
//                {
//                    if (b != BoxButton.this)
//                    {
//                        b.setSelected(false);
//                    }
//                }



                // set the pressed button
                if (pressed == BoxButton.this)
                {
                    pressed = null;
                } else
                {
                    if (pressed != null)
                    {
                        // unpress the button that was pressed
                        pressed.setSelected(false);
                    }
                    pressed = BoxButton.this;
                }
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
                char typed = e.getKeyChar();
                if (isSelected() && typed >= '1' && typed <= '9')
                {
                    setValue((int) typed - 48);
                    solve.setEnabled(true);
//                    System.out.println(e.getKeyChar() + " typed");
                }
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if (isSelected() && (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                        || e.getKeyCode() == KeyEvent.VK_DELETE))
                {
                    setValue(-1);
                    solve.setEnabled(true);
//                    System.out.println("Remove typed");
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }
        }
    }

    private class SolveHandler implements ActionListener
    {

        private Puzzle p;

        public SolveHandler(Puzzle p)
        {
            this.p = p;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
//            System.out.println("Solve action");
            if (pressed != null)
            {
                pressed.setSelected(false);
                pressed = null;
            }

            Puzzle pCopy = p.deepCopy();
            try
            {
                System.out.println("Solving\n"+pCopy);
                pCopy.solve();
                p.solve();
                updateBoard(p);
            } catch (Exception ex)
            {
                System.out.println(ex.getMessage()+"\n"+pCopy);
                solve.setEnabled(false);
            }
        }
    }
}
