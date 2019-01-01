
/**
 * Class that creates GUI interface, and can take loaded maze-file data and turn
 * it into an on-screen maze. Communicates with MazeSolver object that generates
 * actual maze solutions.
 *
 * @author M. Allen
 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

@SuppressWarnings( "serial" )
public class MazeWindow extends JFrame implements ActionListener
{
    // Public constants: can be used in static fashion in other classes to
    // ensure agreement, e.g., for consistent representation of actions.
    public static final int WALL = -1;
    public static final int AGENT = 1;
    public static final int EMPTY = 0;

    public static final String UP = "Up";
    public static final String DOWN = "Down";
    public static final String LEFT = "Left";
    public static final String RIGHT = "Right";

    // Operational variables.
    private int[][] originalMaze;
    private Square[][] maze;
    private int startRow, startCol, capacity;
    private MazeSolver solver;
    private boolean mazeClear;

    // GUI elements.
    private Label info;
    private JPanel panel = new JPanel( null );
    private JButton load, solve;
    private JFileChooser chooser;
    private File mazeFile;
    private JTextField capacityChoice;

    // Internal constants.
    private final int sideBuffer = 10;
    private final int topBuffer = 50;
    private final int gridWidth = 600;
    private final int gridHeight = 600;
    private final int windowWidth = gridWidth + ( 2 * sideBuffer );
    private final int windowHeight = gridHeight + topBuffer + ( 2 * sideBuffer );
    private final Color gainsboro = new Color( 220, 220, 220 );
    private final String agentText = "A";
    private final int minCapacity = 1;
    private final int maxCapacity = 10;

    /**
     * Constructor; binds window instance to MazeSolver instance for
     * communication/visualization purposes.
     * 
     * @param solver A class implementing the MazeSolver interface; required so
     *            the MazeWindow instance can display the maze read in by the
     *            solver, activate the solver to solve a problem for a given
     *            carrying capacity, and display the optimal solution found by
     *            the solver.
     */
    public MazeWindow( MazeSolver solver )
    {
        this.solver = solver;
    }

    /**
     * Constructs all the GUI elements and sets up overall program
     * functionality.
     */
    public void makeWindow()
    {
        setTitle( "A-maze-in'!" );
        setVisible( true );
        setLayout( null );
        getContentPane().setBackground( gainsboro );
        setResizable( false );
        setBounds( 50, 50, windowWidth, windowHeight + getInsets().top );
        setDefaultCloseOperation( EXIT_ON_CLOSE );

        // chooser for files
        chooser = new JFileChooser();
        chooser.setCurrentDirectory( new File( "." ) );
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                                      "Text Files", "txt" );
        chooser.setFileFilter( filter );

        // labels/buttons/what have you
        int buttonWidth = 70;
        int buttonHeight = 25;
        int yLoc = 10;
        load = new JButton( "Load" );
        load.setBounds( windowWidth / 2 - 2 * buttonWidth, yLoc, buttonWidth, buttonHeight );
        load.addActionListener( this );
        add( load, 0 );
        solve = new JButton( "Solve" );
        solve.setBounds( windowWidth / 2 + buttonWidth, yLoc, buttonWidth, buttonHeight );
        solve.addActionListener( this );
        add( solve, 0 );
        Integer[] choices = new Integer[maxCapacity];
        for ( int i = 0; i < choices.length; i++ )
        {
            choices[i] = minCapacity + i;
        }
        capacityChoice = new JTextField( "1" );
        capacityChoice.setBounds( ( windowWidth - buttonWidth ) / 2, yLoc, buttonWidth, buttonHeight );
        capacityChoice.addActionListener( this );
        add( capacityChoice, 0 );

        info = new Label( "No maze loaded." );
        info.setBackground( getContentPane().getBackground() );
        info.setBounds( 20, 40, gridWidth, 20 );
        add( info, 0 );

        repaint();
    }

    /**
     * Generates responses to GUI actions.
     * 
     * @see
     *      ActionListener#actionPerformed(ActionEvent)
     */
    @Override
    public void actionPerformed( ActionEvent e )
    {
        if ( e.getSource() == load )
        {
            openFile();
            if ( mazeFile != null )
            {
                buildMaze( solver.getMaze( mazeFile ) );
            }
        }
        else if ( mazeFile != null )
        {
            buildMaze( originalMaze );
            getSolution();
        }
    }

    /**
     * Post: mazeFile == file chosen by user (if any). Displays name of chosen
     * file in GUI.
     */
    private void openFile()
    {
        if ( chooser.showOpenDialog( this ) == JFileChooser.APPROVE_OPTION )
        {
            mazeFile = chooser.getSelectedFile();
            info.setText( "Maze file " + mazeFile.getName() + " is loaded." );
        }
    }

    /**
     * Displays maze in the on-screen area, scaled to fit window region as well
     * as possible. This maze will be read in and delivered by the MazeSolver
     * with which this instance communicates.
     *
     * @param intMaze A rectangular array of integer values corresponding to
     *            maze layout, loaded from an input file.
     */
    private void buildMaze( int[][] intMaze )
    {
        originalMaze = intMaze;
        mazeClear = true;

        // Sets dimensions for maze.
        int rows = intMaze.length;
        int cols = intMaze[0].length;
        int w = gridWidth / cols;
        int h = gridHeight / rows;
        int off = ( gridWidth - ( cols * w ) ) / 2;

        // Clears existing maze (if any).
        panel.removeAll();
        panel.setBounds( sideBuffer + off, topBuffer + sideBuffer, cols * w,
                         rows * h );
        panel.setBackground( getContentPane().getBackground() );
        add( panel, 0 );

        // Lays out the new maze.
        maze = new Square[rows][cols];
        for ( int r = 0; r < rows; r++ )
            for ( int c = 0; c < cols; c++ )
            {
                maze[r][c] = new Square();
                maze[r][c].setBackground( chooseColor( intMaze[r][c] ) );
                maze[r][c].setBounds( c * w, r * h, w, h );
                if ( intMaze[r][c] == AGENT )
                {
                    maze[r][c].label.setText( agentText );
                    startRow = r;
                    startCol = c;
                }
                else if ( !( intMaze[r][c] == WALL || intMaze[r][c] == EMPTY ) )
                {
                    maze[r][c].label.setText( Integer.toString( intMaze[r][c] ) );
                }
                panel.add( maze[r][c] );
            }

        repaint();
    }

    /**
     * Utility method for setting colors in the maze image.
     * 
     * @param type Value occupying some square in the maze.
     * 
     * @return A color that depends upon the value contained.
     */
    private Color chooseColor( int type )
    {
        if ( type == WALL )
            return Color.black;
        if ( type == EMPTY )
            return Color.white;
        if ( type == AGENT )
            return Color.yellow;

        return Color.green;
    }

    /**
     * Uses MazeSolver to generate optimal solution for the current carrying
     * capacity. If capacity is not specified properly as an integer value,
     * capacity == 1 is used. Values outside of the valid range [1,10] are set
     * to either 1 (if too small) or 10 (if too large).
     */
    private void getSolution()
    {

        Scanner scan = new Scanner( capacityChoice.getText() );
        capacity = 1;
        if ( scan.hasNextInt() )
        {
            capacity = scan.nextInt();
            capacity = Math.max( capacity, minCapacity );
            capacity = Math.min( capacity, maxCapacity );
        }
        scan.close();

        ArrayList<String> policy = solver.getSolution( capacity );
        showSolution( policy );
    }

    /**
     * Displays the resulting policy on-screen.
     * 
     * For proper functionality, these Strings should describe, in order, the
     * series of moves to make that take the agent from its starting
     * position through the maze so that it can pick up the most valuable
     * objects (up to its capacity).
     * 
     * Actions must each be one of the predefined constants:
     * 
     * MazeWindow.UP
     * MazeWindow.DOWN
     * MazeWindow.RIGHT
     * MazeWindow.LEFT
     * 
     * @param policy A list of Strings describing the optimal shortest-path
     *            solution found by the solver.
     */
    private void showSolution( ArrayList<String> policy )
    {
        if ( !mazeClear )
        {
            buildMaze( originalMaze );
        }
        mazeClear = false;

        int row = startRow;
        int col = startCol;
        ArrayList<Integer> values = new ArrayList<>();
        for ( String action : policy )
        {
            String contents = maze[row][col].label.getText();
            if ( isAction( contents ) )
            {
                maze[row][col].label.setText( contents + "/" + action );
            }
            else if ( !contents.equals( agentText ) && !contents.isEmpty() )
            {
                values.add( Integer.parseInt( contents ) );
                maze[row][col].label.setText( action );
            }
            else
            {
                maze[row][col].label.setText( action );
            }

            if ( action.equals( MazeWindow.DOWN ) )
            {
                row++ ;
            }
            else if ( action.equals( MazeWindow.UP ) )
            {
                row-- ;
            }
            else if ( action.equals( MazeWindow.RIGHT ) )
            {
                col++ ;
            }
            else
            {
                col-- ;
            }
        }

        // last step of policy is taken
        values.add( Integer.parseInt( maze[row][col].label.getText() ) );
        maze[row][col].label.setText( "End" );

        Collections.sort( values );
        int value = 0;
        String picked = "";
        if ( capacity > 1 )
        {
            if ( capacity > values.size() )
            {
                picked = values.toString();
            }
            else
            {
                picked = values.subList( values.size() - capacity, values.size() ).toString();
            }
        }
        for ( int c = 0; c < capacity && !values.isEmpty(); c++ )
        {
            value += values.remove( values.size() - 1 );
        }
        info.setText( String.format( "Solution of length %d found; value = %d %s", policy.size(), value, picked ) );
        repaint();
    }

    /**
     * Utility to determine whether a String corresponds to a possible action,
     * or series of actions.
     * 
     * @param s A String that may contain one of the pre-defined constant
     *            actions, e.g. MazeWindow.UP.
     * 
     * @return true if and only if the input contains one of the pre-defined
     *         constants denoting actions.
     */
    private boolean isAction( String s )
    {
        return s.contains( DOWN ) || s.contains( LEFT ) || s.contains( RIGHT ) || s.contains( UP );
    }

    /**
     * Utility class for maze-squares. Each square is a graphical rectangle with
     * an associated label for text display.
     */
    private static class Square extends JComponent
    {
        private JLabel label;

        private Square()
        {
            label = new JLabel();
            label.setBounds( 0, 0, 0, 0 );
            label.setHorizontalAlignment( SwingConstants.CENTER );
            this.add( label );
        }

        /* Sets bounds on object and its associated label */
        @Override
        public void setBounds( int x, int y, int w, int h )
        {
            super.setBounds( x, y, w, h );
            label.setBounds( 0, 0, w, h );
        }

        /* Utility method called implictly to draw object on-screen. */
        @Override
        public void paint( Graphics g )
        {
            g.setColor( getBackground() );
            g.fillRect( 1, 1, getWidth() - 2, getHeight() - 2 );
            paintChildren( g );
        }
    }
}
