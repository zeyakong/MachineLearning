/**
 * Interface for the dynamic programming solver in the maze problem.
 * Must be implemented by any solver class that wishes to communicate and
 * visualize its results via the MazeWindow class.
 * 
 * @author M. Allen
 */

public interface MazeSolver
{
    /**
     * Produces a 2-dimensional array of integers corresponding to the grid in
     * an associated maze-specification textfile.
     * 
     * @param mazeFile A file containing a maze specification in the format
     *            given in the homework description.
     * 
     * @return A 2-dimensional array of integers corresponding to the grid
     *         described by the mazeFile.
     */
    public int[][] getMaze( java.io.File mazeFile );

    /**
     * Generates a solution to the maze problem. Solutions are returned as a
     * list of Strings describing the optimal shortest-path solution found.
     * 
     * For proper functionality, these Strings should describe, in order, the
     * series of moves to make that take the agent from its starting position
     * through the maze so that it can pick up the most valuable objects (up to
     * its capacity). Actions must each be one of the predefined constants:
     * 
     * MazeWindow.UP
     * MazeWindow.DOWN
     * MazeWindow.RIGHT
     * MazeWindow.LEFT
     * 
     * @param capacity The carrying capacity of the agent, as specified by the
     *            user via the GUI.
     * 
     * @return The series of moves in the grid that takes the agent, in any
     *         order, through the squares containing the most valuable objects,
     *         until either the carrying capacity is reached, or all objects are
     *         picked up, whichever comes first. Objects can be picked up in any
     *         order, but the overall path should be as short as possible to
     *         achieve this.
     */
    public java.util.ArrayList<String> getSolution( int capacity );
}
