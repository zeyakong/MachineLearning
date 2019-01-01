import java.util.HashMap;
import java.util.Set;

/**
 * Created by Zeya Kong
 * On 2017/11/8 15:08.
 */
public class MazeMDP{
    //S, set of states.
    private HashMap<Integer,String> S;

    //A, set of actions.
    private HashMap<Integer,String> A;

    //R, reward in each state, it means all in-coming policy will get the same reward in each state.
    private HashMap<String,Double> R;

    public MazeMDP(HashMap<Integer,String> S,HashMap<Integer,String> A,HashMap<String,Double> R){
        this.A =A;
        this.R =R;
        this.S =S;
    }

    public HashMap<Integer, String> getS() {
        return S;
    }

    public HashMap<Integer, String> getA() {
        return A;
    }

    public HashMap<String, Double> getR() {
        return R;
    }
}
