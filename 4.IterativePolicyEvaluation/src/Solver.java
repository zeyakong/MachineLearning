import java.io.*;
import java.util.*;

/**
 * Created by Zeya Kong
 * On 2017/11/6 14:43.
 */
public class Solver implements MazeSolver {

    private List<String> destination;
    private List<String> state;


    int[][] maze;
    int size =  0;
    int startX,startY;
    int endX, endY;
//    MazeMDP mazeMDP

    public static void main(String[] args) {
        Solver solver = new Solver();
        MazeWindow window = new MazeWindow(solver);
        window.makeWindow();
    }

    @Override
    public int[][] getMaze(File mazeFile) {
        int[][] maze = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(mazeFile);
        } catch (FileNotFoundException e) {
            System.out.println("FilePath is wrong!" + e.getStackTrace());
            System.exit(0);
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);
        String line;
        try {
            //First get the maze size.
            line = br.readLine();
            String[] size = line.split(" ");
            maze = new int[Integer.parseInt(size[0])][Integer.parseInt(size[1])];
            for (int i = 0; i < maze.length; i++) {
                line = br.readLine();
                String[] temp = line.split(" ");
                int j = 0 ;
                for (int k = 0; k < temp.length; k++) {
                    if (!temp[k].equals(" ")&&!temp[k].equals("")) {
                        maze[i][j] = Integer.parseInt(temp[k]);
                        j++;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File type is wrong!" + e.getStackTrace());
            System.exit(0);
        }
        this.maze = maze;
        return maze;
    }

    @Override
    public ArrayList<String> getSolution(int capacity) {

        MazeMDP mazeMDP = initialMDP(capacity);
        HashMap<Integer , String> policies= policyIteration(mazeMDP);

        ArrayList<String> solution = new ArrayList<>();
        int start =  findKeyByValue(mazeMDP.getS(),startX+" "+startY);
        int end = findKeyByValue(mazeMDP.getS(),endX+" "+endY+" "+maze[endX][endY]);
        int next = getNextIndex(mazeMDP,start,policies.get(start));
        while(start!=end){
            solution.add(policies.get(start));
            start = next;
            next = getNextIndex(mazeMDP,start,policies.get(start));
        }
        return solution;
    }

    /**
     * This method is used to initialize the MDP.
     * @param capacity the capacity of this problem
     * @return the Markov decission process
     */
    private MazeMDP initialMDP(int capacity){
        HashMap<Integer, String> states = new HashMap<>();
        HashMap<String ,Double> rewards = new HashMap<>();

        List<Integer> destIndex = new ArrayList<>();
        HashMap<Integer , String> dest = new HashMap<>();

        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[maze.length-1].length; j++) {
                if(maze[i][j]==1){
                    startX = i;
                    startY = j;
                }
                if(maze[i][j]>1){
                    destIndex.add(maze[i][j]);
                    dest.put(maze[i][j],i+" "+j);
                }
            }
        }

        Collections.sort(destIndex);

        if(capacity<=destIndex.size()){
            for (int i = 0; i <capacity ; i++) {
                String temp = dest.get(destIndex.get(destIndex.size()-1-i));
                destination.add(temp);
            }
        }else{
            for (int i = 0; i <destIndex.size() ; i++) {
                String temp = dest.get(destIndex.get(destIndex.size()-1-i));
                destination.add(temp);
            }
        }

        int index = 0 ;
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[maze.length-1].length; j++) {
                if(maze[i][j]>= 0 ){
                    if(maze[i][j]==destIndex.get(destIndex.size()-1)){
                        states.put(index ,i+" "+j+" "+destIndex.get(destIndex.size()-1));
                        rewards.put(i+" "+j,1.0);
                        index++;
                    }else{
                        states.put(index, i+" "+j+" "+destIndex.get(destIndex.size()-1));
                        index++;
                        states.put(index, i+" "+j);
                        index++;
                        rewards.put(i+" "+j,-1.0);
                    }
                }
            }
        }

        System.out.println("From"+startX+","+startY+" to "+endX+","+endY+" States is "+states.toString());
        System.out.println(rewards.size()+" Rewards "+rewards.toString());
        System.out.println("Destination is :"+destination);
        size = index;
        return new MazeMDP(states, null , rewards);
    }

    /**
     * Do policy iteration
     * @param mazeMDP the MazeMDP
     * @return all of the set of pi
     */
    private HashMap<Integer , String> policyIteration(MazeMDP mazeMDP){
        //U, a vector of utility values for each state,
        HashMap<Integer , Double> U = new HashMap<>();

        //pi, a policy to be updated
        HashMap<Integer , String> policies = new HashMap();

        //s0 <- S : U(s0) = 0 and pi(s) = a random action.
        for (int i = 0; i < size; i++) {
            U.put(i,0.0);
        }
        for(int i = 0; i < size; i++){
            policies.put(i,getRandomMove());
        }


        boolean changed = true;
        while(changed){
            //U Policy-Evaluation(mdp, pi);
            U = policyEvaluation(mazeMDP ,policies);
            changed = false;

            //all si <- S find max ....
            for (int i = 0; i < size; i++) {
                String policy = "Up";
                double max = getU(mazeMDP,i,U,"Up");

                if(getU(mazeMDP,i,U,"Down")>max){
                    max = getU(mazeMDP,i,U,"Down");
                    policy = "Down";
                }
                if(getU(mazeMDP,i,U,"Right")>max){
                    max = getU(mazeMDP,i,U,"Right");
                    policy = "Right";
                }
                if(getU(mazeMDP,i,U,"Left")>max){
                    max = getU(mazeMDP,i,U,"Left");
                    policy = "Left";
                }
                //unchanged? <- false
                if(!policy.equals(policies.get(i))){
                    policies.put(i,policy);
                    changed = true;
                }
            }
        }

        return policies;
    }


    /**
     *This method is used to calculate the value of the U(si)
     * @param mazeMDP the MDP
     * @param startIndex the index of start state
     * @param U  the set of all the utilities.
     * @param move the current policy.
     * @return
     */
    private double getU(MazeMDP mazeMDP,int startIndex,HashMap<Integer , Double> U,String move){
        String str  = mazeMDP.getS().get(startIndex);
        HashMap<String , Double> rewards = mazeMDP.getR();

        String[] temp = str.split(" ");
        int x = Integer.parseInt(temp[0].trim());
        int y = Integer.parseInt(temp[1].trim());
        int[] next = getDestination(x,y,move);
        String state = "";
        if(temp.length>2){
            state = state+" "+temp;
        }
        int nextIndex = findKeyByValue(mazeMDP.getS(),next[0]+" "+next[1]+state);
        return rewards.get(next[0]+" "+next[1])+U.get(nextIndex);
    }

    /**
     * This method is used to calculate the index of next state when do the policy move
     * @param mazeMDP
     * @param startIndex the index of start state
     * @param move the current policy.
     * @return
     */
    private int getNextIndex(MazeMDP mazeMDP,int startIndex,String move){
        String str  = mazeMDP.getS().get(startIndex);
        HashMap<String , Double> rewards = mazeMDP.getR();

        String[] temp = str.split(" ");
        int x = Integer.parseInt(temp[0].trim());
        int y = Integer.parseInt(temp[1].trim());

        int[] next = getDestination(x,y,move);
        String state = "";
        if(temp.length>2){
            state = state+" "+temp;
        }
        return findKeyByValue(mazeMDP.getS(),next[0]+" "+next[1]+state);
    }


    /**
     * This method is used to evaluate the policy.
     * @param mazeMDP
     * @param policies set of policies
     * @return
     */
    private HashMap<Integer , Double> policyEvaluation(MazeMDP mazeMDP , HashMap<Integer , String> policies){
        //s0 <- S : U(s0) = 0
        HashMap<Integer , Double> U = new HashMap<>();
        for (int i = 0; i < size; i++) {
            U.put(i,0.0);
        }

        double flag = 0.1;
        HashMap<String,Double> rewards = mazeMDP.getR();

        while(flag>0){
            flag = 0;

            //ALL si <-S
            for (int i = 0; i < size; i++) {

                //u <- U(s)
                double u = U.get(i);

                String str  = mazeMDP.getS().get(i);
                String[] temp = str.split(" ");
                int x = Integer.parseInt(temp[0].trim());
                int y = Integer.parseInt(temp[1].trim());

                int[] next = getDestination(x,y,policies.get(i));
                double reward =rewards.get(next[0]+" "+next[1]);
                String state = "";
                if(temp.length>2){
                    state = state+" "+temp[temp.length-1];
                }
                if(next[0]==endX&&next[1]==endY&&!state.endsWith("10")){
                    state = state +" "+ 10;
                }
                int nextIndex = findKeyByValue(mazeMDP.getS(),next[0]+" "+next[1]+state);
                U.put(i,reward+U.get(nextIndex));

                //find max
                if(Math.abs(reward+U.get(nextIndex)-u)>flag)flag = Math.abs(reward+U.get(nextIndex)-u);
            }
        }
        return U;
    }

    /**
     * Get the random move from the action.(Up,Down,Left,Right)
     * @return the random move.
     */
    private String getRandomMove(){
        int num = (int)((Math.random())*4);
        if(num == 0 ){
            return "Up";
        }
        if(num == 1 ){
            return "Down";
        }
        if(num == 2 ){
            return "Right";
        }
        if(num == 3 ){
            return "Left";
        }
        return "Left";
    }

    /**
     * Get the position of the next state when do the action move
     * @param x the start X
     * @param y the start Y
     * @param move the action
     * @return
     */
    private int[] getDestination(int x,int y, String move){
        int[] result = new int[2];
        result[0]=x;
        result[1]=y;

        if(move.equals("Up")){
            if(x>1&& maze[x-1][y]>=0){
                result[0]= x-1;
                result[1]=y;
                return result;
            }else{
                return result;
            }
        }
        if(move.equals("Down")){
            if(x<maze.length-1&& maze[x+1][y]>=0){
                result[0]=x+1;
                result[1]=y;
                return result;
            }else{
                return result;
            }
        }
        if(move.equals("Left")){
            if(y>1&& maze[x][y-1]>=0){
                result[0]=x;
                result[1]=y-1;
                return result;
            }else{
                return result;
            }
        }
        if(move.equals("Right")){
            y=y+1;
            if(y<maze[maze.length-1].length-1&& maze[x][y+1]>=0){
                result[0]=x;
                result[1]=y+1;
                return result;
            }else{
                return result;
            }
        }
        return result;
    }

    /**
     * This method is used to find the key by the value.I assume all the value and key are unique so , I can do this method.
     * @param hashMap the query source
     * @param value the search value
     * @return the key (type is Integer)
     */
    private int findKeyByValue(HashMap<Integer,String > hashMap,String value){
        Set s = hashMap.keySet();
        for(Iterator i = s.iterator();i.hasNext();){
            int index = (int)i .next();
            if(hashMap.get(index).equals(value)) return index;
        }
        return -1;
    }
}
