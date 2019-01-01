import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Zeya Kong
 * On 2017/10/28 15:51.
 */
public class Node {
    //Use this variable to judge whether the node is {T,F} or{1.2.3,,,}
    private int numberValue;
    //Name of the node
    private String name;
    //I use the List to store the parents because the number of parents may more than one.
    //In the parents list, I also used the Node to store because we need the parents' cpt
    private ArrayList<Node> parents;

    //The cpt of my assignment is HashMap.
    // each cpt of variables depends on the type of nodes,the type of their parents and the number of their parents
    // there are two cases:(0 meas F, 1 means T)
    //1.{T,F} variables' cpt   eg: TF : 0.03  --->   <10,0.03>  <String , Double>
    //2.{1,2.3...} variables'cpt eg: T 1 2 0.2 0.3 0.4
    //Just add the value to the end of the String key.
    //<1121 , 0.2> ;<1122 , 0.3>;<1123 , 0.4> (0 meas F, 1 means T,sometimes 1 means value 1)
    private HashMap<String , Double> cpt;

    public ArrayList<Node> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Node> parents) {
        this.parents = parents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, Double> getCpt() {
        return cpt;
    }

    public void setCpt(HashMap<String, Double> cpt) {
        this.cpt = cpt;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(int numberValue) {
        this.numberValue = numberValue;
    }
}
