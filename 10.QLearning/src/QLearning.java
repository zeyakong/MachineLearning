import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public class QLearning {
    //the three dimensional array is for basic Q learning only.
    //Q[row][col][action] = value
    //[row][col] indicate the point position. Each point is a state in this problem domain.
    //[action] = {0,1,2,3}, 0~3 means up, down, left, right.
    //for example: Q[2][4][3] = 0.8 means the Q value of state in row 2 col 4 go left is 0.8
    private double[][][] Q;

    //policy[row][col] indicate the best action in the state[row][col]
    //policy[3][4] = R means state[3][4] should go right(R).
    private char[][] policy;

    //Those parameters are used for basic Q and feature-based Q.
    private double learningRate;
    private double randomness;
    private double futureDiscount;

    //the entire maze was stored as a two dimensional array.
    private static final int MAZE_WIDTH = 37;
    private static final int MAZE_HEIGHT = 50;
    private char[][] maze;

    //Parameters for Feature-based Q only
    private double w1;
    private double w2;
    private static final double totalManhattanDistance = MAZE_HEIGHT - 1 + 18;

    //the init value was provided in the assignment description.
    public QLearning(){
        maze = new char[MAZE_HEIGHT][MAZE_WIDTH];
        policy = new char[MAZE_HEIGHT][MAZE_WIDTH];
        Q = new double[MAZE_HEIGHT][MAZE_WIDTH][4];
    }

    //the entrance of the program. it will load the local file and try to do basic Q leaning and feature-based Q learning.
    //it will also ask the user to store the average reward for 50 test run per 100 episode into disk.
    public static void main(String[] args) {
        QLearning q1 = new QLearning();
        QLearning q2 = new QLearning();
        q1.loadWorld("pipe_world.txt");
        q2.loadWorld("pipe_world.txt");
        System.out.println("---Basic Q learning---\n");
        q1.StartLearning(false);
        System.out.println("\n---feature-based Q learning---\n");
        q2.StartLearning(true);
    }

    //############## functions for both basic Q learning and feature-based Q learning.###########################
    /**
     * This function is used to load the file.
     * @param filePath src/
     */
    private void loadWorld(String filePath) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String strTmp;
            int row = 0;
            while ((strTmp = br.readLine()) != null) {
                maze[row] = strTmp.toCharArray();
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The function will calculate the total reward by following the best trained policy.
     * @param featured true: feature-based Q
     *                 false: basic Q
     * @return total reward value.
     */
    private double testRun(boolean featured) {
        double totalReward = 0;
        int row = 0;
        int col = 18;
        int action;
        for (int step = 0; step < MAZE_WIDTH * MAZE_HEIGHT; step++) {
            if (maze[row][col] == 'G' || maze[row][col] == 'M') return totalReward;
            if (featured) {
                action = getMaxFeatureBasedQAction(row, col);
            } else {
                action = getMaxQAction(row, col);
            }
            int[] next = getNextState(row, col, action,true);
            row = next[0];
            col = next[1];
            totalReward += getReward(row, col);
        }
        return totalReward;
    }

    /**
     * The following method will do the Q learning which contains the basic Q learning and
     * feature-based Q. All the paramaters are provided by the assignment description.
     * the randomness and learning rate will be chagned by the way of the assignment requirement.
     * @param featured true: feature-based Q
     *                 false: basic Q
     */
    private void StartLearning(boolean featured) {
        //init parameters.
        learningRate = 0.9;
        randomness = 0.9;
        futureDiscount = 0.9;
        w1 = 0;
        w2 = 0;

        //do 10000 episode to train
        for (int episode = 1; episode <= 10000; episode++) {
            if (episode == 10000) {
                //last run, no randomness
                randomness = 0;
            } else if (episode % 200 == 0) {
                //it will reduce the randomness by each 200 episode
                randomness = (0.9 / (double) (episode / 200 + 1));
            }
            if (episode % 1000 == 0) {
                //it will reduce the learningRate by each 1000 episode
                learningRate = (0.9 / (double) (episode / 1000 + 1));
            }
            //start state is row=0 , col = 18. s0
            int row = 0;
            int col = 18;
            int steps = 0;
            //three ways to finish the run.
            //steps > total maze
            //bomb!!! reaches the Mine.
            //reaches the goal.
            while (steps <= MAZE_WIDTH * MAZE_HEIGHT && maze[row][col] != 'G' && maze[row][col] != 'M') {
                //chose an action based on the e-greedy
                int action = getEGreedyAction(row, col, featured);
                //next state s'
                int[] next = getNextState(row, col, action,true);
                int nextRow = next[0];
                int nextCol = next[1];
                if (featured) {
                    //only update the weights
                    double delta = getReward(nextRow, nextCol) + futureDiscount * getMaxFeatureBasedQValue(nextRow, nextCol) - getFeatureBasedQ(row, col, action);
                    w1 = w1 + learningRate * delta * feature1(row, col, action);
                    w2 = w2 + learningRate * delta * feature2(row, col, action);
                } else {
                    //basic Q learning
                    Q[row][col][action] = Q[row][col][action] + learningRate * (getReward(nextRow, nextCol) + futureDiscount * getMaxQValue(nextRow, nextCol) - Q[row][col][action]);
                }
                //s0 <- new state
                row = nextRow;
                col = nextCol;
                steps++;
            }

            //during each 100 episode, test 50 times and calculate the average rewards.
            if (episode % 100 == 0) {
                double totalR = 0;
                for (int i = 0; i < 50; i++) {
                    totalR += testRun(featured);
                }
//                System.out.println(totalR / 50.0);
            }

            //print the last episode.
            if (episode == 10000) {
                System.out.println("After 10000 episode training, the policy matrix:");
                updatePolicy(featured);
                printPolicy();
            }
        }
    }

    /**
     * the function will return the next state position from state[row,col] do action [action]
     * @param row position
     * @param col position
     * @param action 0~3
     * @return array .the next state position. {row, col}
     */
    private int[] getNextState(int row, int col, int action,boolean slip) {
        int nextRow, nextCol;

        int slipValue = 0;
        if (slip){
            double r = Math.random();
            if (r < 0.1) {
                //slip to left
                slipValue = -1;
            } else if (r >= 0.9) {
                //slip to right
                slipValue = 1;
            }
        }
        switch (action) {
            //up
            case 0:
                nextRow = row - 1;
                nextCol = col + slipValue;
                break;
            //down
            case 1:
                nextRow = row + 1;
                nextCol = col - slipValue;
                break;
            //left
            case 2:
                nextRow = row - slipValue;
                nextCol = col - 1;
                break;
            //right
            case 3:
                nextRow = row + slipValue;
                nextCol = col + 1;
                break;
            //error
            default:
                System.out.println("error,a =" + action);
                return null;
        }
        //check the boundary
        if (nextCol <= 0) nextCol = 0;
        else if (nextCol >= MAZE_WIDTH) nextCol = MAZE_WIDTH - 1;
        if (nextRow <= 0) nextRow = 0;
        else if (nextRow >= MAZE_HEIGHT) nextRow = MAZE_HEIGHT - 1;
        return new int[]{nextRow, nextCol};
    }

    /*
    A printer method which used to test.
     */
    private void printMaze() {
        System.out.println("MAZE SHAPE:" + maze.length + ", " + maze[0].length);
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                System.out.print(maze[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * A printer to print the policy
     */
    private void printPolicy() {
        System.out.println("POLICY SHAPE:" + policy.length + ", " + policy[0].length);
        for (int i = 0; i < policy.length; i++) {
            for (int j = 0; j < policy[i].length; j++) {
                System.out.print(policy[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * The following function will return the reward for the current state.
     * it requires two position parameters to show the destination state position.
     * @param row the row of the destination state
     * @param col the col of the destination state
     * @return the value of the reward.
     */
    private double getReward(int row, int col) {
        //get the state of this position
        char state = maze[row][col];
        //there are four different kinds of states: M S G _
        switch (state) {
            case 'M':
                return -100.0;
            case 'G':
                return 0.0;
            default:
                return -1.0;
        }
    }

    /**
     *
     * @param row position row
     * @param col position row
     * @param featured true: featured-based Q
     *                 false: basic Q
     * @return the action type int(0~3)
     */
    private int getEGreedyAction(int row, int col, boolean featured) {
        double random = Math.random();
        if (random < randomness) {

            return getRandomAction();
        } else {
            if (featured) {
                return getMaxFeatureBasedQAction(row, col);
            } else {
                return getMaxQAction(row, col);
            }
        }
    }

    /**
     * Generate a random integer range of 0 to 3
     * @return 0~3 integer represents the action U D L R.
     */
    private int getRandomAction() {
        return ThreadLocalRandom.current().nextInt(0, 3 + 1);
    }

    /**
     * Update the policy matrix by the max Q value
     * @param featured featured or not
     */
    private void updatePolicy(boolean featured) {
        for (int i = 0; i < policy.length; i++) {
            for (int j = 0; j < policy[i].length; j++) {
                int action;
                if (featured) {
                    action = getMaxFeatureBasedQAction(i, j);
                } else {
                    action = getMaxQAction(i, j);
                }
                switch (action) {
                    //up
                    case 0:
                        policy[i][j] = 'U';
                        break;
                    //down
                    case 1:
                        policy[i][j] = 'D';
                        break;
                    //left
                    case 2:
                        policy[i][j] = 'L';
                        break;
                    //right
                    case 3:
                        policy[i][j] = 'R';
                        break;
                    //error
                    default:
                        policy[i][j] = '?';
                }
                if (maze[i][j] == 'G') policy[i][j] = 'G';
                if (maze[i][j] == 'M') policy[i][j] = 'M';
            }
        }
    }


    //############## functions for basic Q learning only.########################################################

    /**
     * The following function will return the best action by comparing to the Q array.
     * it will return the best action which represent as a integer(0,1,2,3)
     * @param row the state row
     * @param col the state col.
     * @return the type of action.{0,1,2,3} means {U,D,L,R}
     */
    private int getMaxQAction(int row, int col) {
        //first, find the state [row][col]
        double[] state = Q[row][col];
        //find the max action of the Q.
        int maxAction = 0;
        double maxValue = state[0];
        for (int i = 0; i < state.length; i++) {
            if (state[i] > maxValue) {
                maxValue = state[i];
                maxAction = i;
            }
        }
        return maxAction;
    }

    /**
     * Get the max Q value for the state [row ,col]
     * @param row state position row
     * @param col state position col
     * @return the value of the max Q, double type.
     */
    private double getMaxQValue(int row, int col) {
        //first, find the state [row][col]
        double[] state = Q[row][col];
        //find the max action of the Q.
        double maxValue = state[0];
        for (int i = 0; i < state.length; i++) {
            if (state[i] >= maxValue) {
                maxValue = state[i];
            }
        }
        return maxValue;
    }

    //############## functions for feature-based Q learning only.################################################

    /**
     * The f1 from the assignment description.
     * @param row position row of the state
     * @param col position col of the state
     * @param action (0~3)
     * @return value of the f1(s,a)
     */
    private double feature1(int row, int col, int action) {
        int[] next = getNextState(row,col,action,false);
        return  getManhattanDistance(next[0],next[1])/ totalManhattanDistance;
    }

    /**
     * * The f1 from the assignment description.
     * @param row position row of the state
     * @param col position col of the state
     * @param action (0~3)
     * @return value of the f2(s,a)
     */
    private double feature2(int row, int col, int action) {
        //left: action= 2, right action = 3, up 0 , down 1
        double goLeft = 1 / (double) col;
        double goRight = 1 / (double) (MAZE_WIDTH - col);
        if (action == 2) return goLeft;
        if (action == 3) return goRight;
        if (action < 2) return Math.min(goLeft, goRight);
        return -1;
    }

    /**
     * The function returns the Manhattan Distance without normalization.
     * @param row position row of the state
     * @param col position col of the state
     * @return the value of the distance
     */
    private double getManhattanDistance(int row, int col) {
        //the position of the goal: (49, 18)
        return Math.abs(49 - row) + Math.abs(18 - col);
    }

    /**
     * THe function calculates the value of the feature-based Q for Q(s,a)
     * @param row position row of the state
     * @param col position col of the state
     * @param action (0~3)
     * @return value of the feature-based Q for Q(s,a)
     */
    private double getFeatureBasedQ(int row, int col, int action) {
        return w1 * feature1(row, col, action) + w2 * feature2(row, col, action);
    }

    /**
     * Same to the basic Q learning. this function will return the max value of the feature-based Q
     * @param row position row of the state
     * @param col position col of the state
     * @return the max value of the feature-based Q in state S(row ,col)
     */
    private double getMaxFeatureBasedQValue(int row, int col) {
        double maxValue = getFeatureBasedQ(row, col, 0);
        double temp;
        if (maxValue < (temp = getFeatureBasedQ(row, col, 1))) {
            maxValue = temp;
        }
        if (maxValue < (temp = getFeatureBasedQ(row, col, 2))) {
            maxValue = temp;
        }
        if (maxValue < (temp = getFeatureBasedQ(row, col, 3))) {
            maxValue = temp;
        }
        return maxValue;
    }

    /**
     * Return the best policy for this state according to the feature-based Q value
     * @param row position row of the state
     * @param col position col of the state
     * @return the max value action (0~3)
     */
    private int getMaxFeatureBasedQAction(int row, int col) {
        double maxValue = getFeatureBasedQ(row, col, 0);
        double temp;
        int action = 0;
        if (maxValue < (temp = getFeatureBasedQ(row, col, 1))) {
            maxValue = temp;

            action = 1;
        }
        if (maxValue < (temp = getFeatureBasedQ(row, col, 2))) {
            maxValue = temp;
            action = 2;
        }
        if (maxValue < (temp = getFeatureBasedQ(row, col, 3))) {
            action = 3;
        }
        return action;
    }

    //test the result manually
    private void test() {
        w1 = -1.133206348949327;
        w2 = -179.70868857281798;
        System.out.println(getMaxFeatureBasedQAction(15, 8));
    }
}
