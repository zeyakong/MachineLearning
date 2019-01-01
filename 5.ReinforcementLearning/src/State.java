/**
 * Created by Zeya Kong
 * On 2017/11/28 20:21.
 */

/**
 * The MDP s:state.
 * I never created a MDP class to store MDP because I think in this hw, it is simple and I just store other information
 * such as reward and action in the Util class as a class variable.
 * This class just means state. It store three attributes.
 * 1. 2. position of the state
 * 3. the type of the state. Because I used the char[][] maze to store the iceWorld,I use char to store the type info
 */
public class State {
    private int x;
    private int y;
    private char type;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "State{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }
}
