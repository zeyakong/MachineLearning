import org.junit.Test;

import java.io.*;
import java.util.*;

/**
 * Created by Zeya Kong
 * On 2017/11/26 21:39.
 */

/**
 * Any move into a hole will cause the agent to return to the location from which they just
 moved (after climbing out of the hole), and will incur a cost of −50; all other movements into
 open space or on icy surfaces incur a cost of −1. For any action that results in entering the
 absorbing goal state, the reward received is +100.
 To do the learning, you should implement each algorithm to use the following parameters:
 • An episode for learning is defined as min(M; N), where M is any number of moves that take
 the agent from the start state to the goal, and N = (10 × w × h), where w and h are the
 width and height of the grid-world, respectively. That is, each episode ends as soon as the
 agent reaches the goal; if that doesn’t happen after N time-steps (actions taken), then the
 episode terminates without reaching the goal. After each episode, the agent will return to
 the start state.
 1• The future discount factor is set to γ = 0:9; it never changes.
 • The policy randomness parameter is initially set to  = 0:9; every 10 episodes it is updated.
 In particular, if E is the number of episodes already past, then for all values E >= 10, we
 set  = 0:9=bE=10c. This means that after 1000 episodes,  = 0:009; at this point, we should
 set it to 0, and no longer act randomly.
 • The step-size parameter for learning updates is set to α = 1:0, and can be omitted from
 calculations; for this application, we do not need to reduce it, as we are not particularly
 interested in the values to which it converges, only the policy that is produced.
 For each algorithm, you will do 2000 episodes of learning. Thus, there will be 1000 episodes during
 which the agent will act randomly, and 1000 for which it will always act greedily (although those
 greedy actions can change over time, since it will still be updating values and learning).
 */

public class LearnUtil {
    //This is the size of the maze I use char to store all the information
    private int MAZELENGTH;
    private int MAZEWIDTH;
    private char[][] maze;

    //I used the List to store all of the states, Even some state can not be reached such as 'H'
    //In this problem, I just created a reward table to store all the information,but finally I found
    //that I can use the index of the list to represent all the position information, So I just used this way
    //To implement all the method, it is very simple.
    //For example: the index of List : 43 means the state in maze[4][3].So the index itself shows the position.
    private List<State> states;

    //This is never used ,But I test it and it is right. It can't show the direction of the actions , so I didn't want to use it
    private int[][] reward;

    /**
     * This constructor is used to get the maze size.
     *
     * @param length the length of the maze
     * @param width the width of the maze
     */
    public LearnUtil(int length, int width) {
        MAZELENGTH = length;
        MAZEWIDTH = width;
        maze = new char[MAZELENGTH][MAZEWIDTH];
        states = new ArrayList<>();
    }


    /**
     * read the file and store it into char[][] maze
     * @param filePath file path
     */
    public void readFile(String filePath)  {
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        int temp;
        try {
            for (int i = 0; i < MAZELENGTH; i++) {
                for (int j = 0; j < MAZELENGTH; j++) {
                    temp = br.read();
                    if(temp>0 && temp!='\n'){
                        maze[i][j] = (char)temp;
                    }else{
                        temp = br.read();
                        if(temp>0 && temp!='\n'){
                            maze[i][j] = (char)temp;
                        }
                    }
                }
            }
//            System.out.println("Done.\n");
//            for (int i = 0; i < MAZELENGTH; i++) {
//                for (int j = 0; j < MAZELENGTH; j++) {
//                    System.out.print(maze[i][j]);
//                }
//                System.out.println();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * after readFile and initialize rewards and states we can start the Q learning.
     */
    public void doQLearning() {
        //initialize the Q(s,a)
        //I use the double map to store the many to one relationship.
        //HashMap <index of the state, HashMap< "ACTION", value> >
        //For example <23,<"Up", -10> > means the state in 23.If it want to go up , The Q(23, up ) is -10.
        HashMap<Integer , HashMap<String , Double>> Q =new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            HashMap<String ,Double> temp = new HashMap<>();
            temp.put("Up",0.0);
            temp.put("Down",0.0);
            temp.put("Left",0.0);
            temp.put("Right",0.0);
            Q.put(i,temp);
        }

        //there are 2000 episodes.
        //in the first 1000 the agent might act random depends on the value of E(0.9 and it will decrease)
        //in the last 1000 episode the agant will act greedily.
        //repeat for each episode E:
        for (int i = 0; i < 2000; i++) {
            //set start-state s <--s0
            State s = states.get(70);

            //if it can't find the destination, it will stop after m*n*10=1000 times.
            int times = 0;
            //each episode ,I use this to store the whole reward.
            int reward = 0;

            //repeat for each time-step t of episode E, until s is terminal:
            while(s.getType()!='G'&& times<1000){

                //set action a, chosen E-greedily based on Q(s, a)
                String action;
                if(getEGreedy(i)){
                    //We should use the Q.
                    HashMap<String , Double> v= Q.get(s.getX()*10+s.getY());
                    action = getMaxAction(v);
                }else{
                    //Just get the random Action.
                    action =  getRandomAction();
                }

                //take action a
                State next = getNextState(s,action);

                //Q(s, a)  = reward + max{Q(s',a')}*0.9
                HashMap<String , Double> v= Q.get(next.getX()*10+next.getY());
                double value = getReward(next)+0.9*Q.get(next.getX()*10+next.getY()).get(getMaxAction(v));
                HashMap<String , Double> v2= Q.get(s.getX()*10+s.getY());
                v2.put(action,value);

                //s <- s'
                s= next;
                times++;
                reward += getReward(next);
            }

            //print all the information
            printPolicy(i,reward,Q);
        }
    }

    /**
     * print method
     * @param episode
     * @param reward
     * @param Q
     */
    private void printPolicy(int episode,int reward,HashMap<Integer , HashMap<String , Double>> Q ) {
        if((episode+1)%100!=0&&episode!=0) return;
        System.out.println("\nEpisode "+(episode+1)+",Reward = "+reward);
        char[][] policy = new char[10][10];
        for (int k = 0; k < 10; k++) {
            for (int j = 0; j < 10; j++) {

                if(maze[k][j]=='S')policy[k][j]='S';
                else{
                    String action = getMaxAction(Q.get(k*10+j));
                    char show=' ';

                    switch (action){
                        case "Up": show='U';
                            break;
                        case "Down": show='D';
                            break;
                        case "Left": show='L';
                            break;
                        case "Right": show ='R';
                            break;
                    }
                    policy[k][j] = show;
                }
                if(maze[k][j]=='H')policy[k][j]='H';
            }
        }

        for (int k = 0; k < 10; k++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(policy[k][j]);
            }
            System.out.println();
        }
    }

    /**
     * find the index of the maximum value in HashMap
     * @param v the hashmap
     * @return the index of the maximum value
     */
    private String getMaxAction(HashMap<String,Double> v){

        Collection<Double> c = v.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        double value = (double)obj[obj.length-1];
        Set s = v.keySet();
        for (Iterator i =s.iterator();i.hasNext();){
            String str = (String)i.next();
            if(v.get(str)==value){
//                System.out.println(v.toString()+"choose "+str);
                return str;
            }
        }
        return " ";
    }

    /**
     * Get the random action
     * @return the random action
     */
    private String getRandomAction() {
        Random r= new Random();
        int r0 = r.nextInt(4);
        if(r0==0)return "Up";
        if(r0==1)return "Down";
        if(r0==2)return "Left";
        if(r0==3)return "Right";
        return "Up";
    }

    /**
     *This method return the result shows whether we use random action or just follow the Q
     * @param i Episode number
     * @return whether use Q or just random. true means use Q.
     */
    private boolean getEGreedy(int i) {
        double e  = 0.9;
        double r = Math.random();
        if(i<10)return r>e;
        else if(i>=1000){
            return true;
        }else{
            e = 0.9/(i/10);
            return r>e;
        }
    }


    //All the data structure is siminar to the Q learning , So I just omitted some comments.
    public void doSARSA() {
        //initialize the Q(s,a)
        HashMap<Integer , HashMap<String , Double>> Q =new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            HashMap<String ,Double> temp = new HashMap<>();
            temp.put("Up",0.0);
            temp.put("Down",0.0);
            temp.put("Left",0.0);
            temp.put("Right",0.0);
            Q.put(i,temp);
        }

        //repeat for each episode E:
        for (int i = 0; i < 2000; i++) {
            //start-state s <--s0
            State s = states.get(70);

            //set action a, chosen E-greedily based on Q(s, a)
            String action;
            if(getEGreedy(i)){
                //We should use the Q.
                HashMap<String , Double> v= Q.get(s.getX()*10+s.getY());
                action = getMaxAction(v);
            }else{
                //Just get the random Action.
                action =  getRandomAction();
            }

            //if it can't find the destination, it will stop after m*n*10=1000 times.
            int times = 0;
            //each episode ,I use this to store the whole reward.
            int reward = 0;

            //repeat for each time-step t of episode E, until s is terminal:
            while(s.getType()!='G'&&times<1000){
                //take action a
                State next = getNextState(s,action);

                //set action next, chosen E-greedily based on Q(s', a')
                String nextAciton;
                if(getEGreedy(i)){
                    //We should use the Q.
                    HashMap<String , Double> v2= Q.get(next.getX()*10+next.getY());
                    nextAciton = getMaxAction(v2);
                }else{
                    //Just get the random Action.
                    nextAciton =  getRandomAction();
                }

                //Q(s, a)  = reward + Q(s',a')*0.9
                double value = getReward(next)+0.9*Q.get(next.getX()*10+next.getY()).get(nextAciton);

                HashMap<String , Double> v2= Q.get(s.getX()*10+s.getY());
                v2.put(action,value);

                //s = s';a = a'
                s= next;
                action = nextAciton;

                reward += getReward(next);
                times++;
            }
            printPolicy(i,reward,Q);
        }
    }

    /**
     * This method is used to initialize all the states. I use the List to store states.
     * In order to do reinforcement learning , this method should be run first.
     */

    public void initializeState() {
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[0].length; j++) {
                State s = new State();
                s.setX(i);
                s.setY(j);
                s.setType(maze[i][j]);
                states.add(s);
            }
        }
//
//        for (int i = 0; i < states.size(); i++) {
//            System.out.println(i+" "+states.get(i).toString());
//        }
    }

    /**
     * This method is used to initialize the reward. I use the array int[][] to store the reward
     * This method must run after the method initializeState() run.
     * Finally I found that this table is useless, But I didn't delete it because maybe in next project it will be useful.
     */

    public void initializeReward() {
        //all of the value is zero.
        System.out.println(states.size());
        reward = new int[states.size()][states.size()];
        char type;
        for (int i = 0; i < states.size(); i++) {
            //the index of states indicates the location information of the state
            //for example : index:29 ---> maze[2][9] = (2,9)
            //because the word is static(iceWorld). so I use the 10(the maze size)
            if(states.get(i).getType()!='H'){
                //get Up state i-10
                if(i>=10){
                    type = states.get(i-10).getType();
                    if(type=='O'|| type=='I'||type=='S'){
                        reward[i][i-10] = -1;
                    }
                    if(type=='G'){
                        reward[i][i-10] = 100;
                    }
                    if(type=='H'){
                        reward[i][i-10] = -50;
                    }
                }
                //get Down state i+10
                if(i<90){
                    type = states.get(i+10).getType();
                    if(type=='O'|| type=='I'||type=='S'){
                        reward[i][i+10] = -1;
                    }
                    if(type=='G'){
                        reward[i][i+10] = 100;
                    }
                    if(type=='H'){
                        reward[i][i+10] = -50;
                    }
                }
                //get Left state i-1
                if(i%10!=0){
                    type = states.get(i-1).getType();
                    if(type=='O'|| type=='I'||type=='S'){
                        reward[i][i-1] = -1;
                    }
                    if(type=='G'){
                        reward[i][i-1] = 100;
                    }
                    if(type=='H'){
                        reward[i][i-1] = -50;
                    }
                }
                //get Right state i+1
                if(i%10!=9){
                    type = states.get(i+1).getType();
                    if(type=='O'|| type=='I'||type=='S'){
                        reward[i][i+1] = -1;
                    }
                    if(type=='G'){
                        reward[i][i+1] = 100;
                    }
                    if(type=='H'){
                        reward[i][i+1] = -50;
                    }
                }
            }
        }

//        for (int i = 0; i < 100; i++) {
//            for (int j = 0; j < 100; j++) {
//                if(reward[i][j]!=0){
//                    System.out.println("from state:"+i+" to state"+j+" has reward "+reward[i][j]);
//                }
//            }
//        }
    }

    /**
     * Because of the ice one state do same action maybe cause different destination.
     * So get reward method just need the destination state after doing this action.
     * In method getNextState(..) we just get the next state.So we do not need to verify the state index.
     * @param s2 destination state
     * @return reward
     */
    private int getReward(State s2){
        char type = s2.getType();
        switch(type){
            case 'H': return -50;
            case 'I': return -1;
            case 'G': return 100;
            case 'O': return -1;
            case 'S': return -1;
        }
        return 0;
    }

    /**
     * This method is used to find the destination after state s do the action called direction.
     * @param s the state
     * @param direction the action
     * @return destination state
     */
    private State getNextState(State s , String direction){
        int index = s.getX()*10+s.getY();
        char type = s.getType();
        if(type=='I'){
            double d = Math.random();
            if(d<0.1){
                //Because we know in this map all Ice in the inner of the maze ,so we do not need to verify the value
                if(direction.equals("Up")){
                    return states.get(index-11);
                }
                if(direction.equals("Down")){
                    return states.get(index+11);
                }
                if(direction.equals("Left")){
                    return states.get(index+9);
                }
                if(direction.equals("Right")){
                    return states.get(index-9);
                }
            }else if(d>=0.9){
                if(direction.equals("Up")){
                    return states.get(index-9);
                }
                if(direction.equals("Down")){
                    return states.get(index+9);
                }
                if(direction.equals("Left")){
                    return states.get(index-11);
                }
                if(direction.equals("Right")){
                    return states.get(index+11);
                }
            }
        }
        if(direction.equals("Up")){
            if(index>=10&&states.get(index-10).getType()!='H') return  states.get(index-10);
        }
        if(direction.equals("Down")){
            if(index<90&&states.get(index+10).getType()!='H') return  states.get(index+10);
        }
        if(direction.equals("Left")){
            if(index%10!=0&&states.get(index-1).getType()!='H') return  states.get(index-1);
        }
        if(direction.equals("Right")){
            if(index%10!=9&&states.get(index+1).getType()!='H') return  states.get(index+1);
        }
        return s;
    }
}
