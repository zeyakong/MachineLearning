import java.util.HashMap;

/**
 * This class is used to store the tree information for the decision tree algorithm.
 * The Tree has two types: isLeaf or not.
 * If the tree is leaf . only the variable leaf and isLeaf is meaningful.
 * IF the tree have sub tree, it will store the attributeIndex and nodes.
 * The attributeIndex is used to store the node attribute index.
 */
public class Tree{
    private int attributeIndex;
    private String leaf;
    private boolean isLeaf;
    //HashMap<attributeValue, Tree>
    private HashMap<String, Tree> nodes;

    /**
     * Leaf constructor. it will assign the isLeaf a true value and must have  parameter for the leaf value.
     *
     * @param leaf the leaf value
     */
    public Tree(String leaf) {
        isLeaf = true;
        this.leaf = leaf;
    }

    /**
     * not leaf constructor. It means this tree have sub trees. So it will initialize some variables.
     *
     * @param attributeIndex the index of the attribute.
     */
    public Tree(int attributeIndex) {
        this.attributeIndex = attributeIndex;
        nodes = new HashMap<>();
        isLeaf = false;
    }

    /**
     * Add a subtree for this value. If the tree is leaf, it will print error msg.
     * @param nextTree subTree
     * @param attributeValue the value for the attribute.
     */
    public void addSubTree(Tree nextTree, String attributeValue) {
        if (isLeaf) {
            System.out.println("CAN NOT ADD A SUB TREE INTO A LEAF!");
            return;
        }
        nodes.put(attributeValue, nextTree);
    }

    public Tree getSubTree(String attributeValue) {
        return nodes.get(attributeValue);
    }

    public int getAttributeIndex() {
        return attributeIndex;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public String getLeaf() {
        return leaf;
    }

    /**
     * The following method will give a prediction for the input one data.
     * @param example one example structured as String[]
     * @return the classification :P or E.
     */
    public String getResult(String[] example){
        if(isLeaf){
            return leaf;
        }else{
            return nodes.get(example[attributeIndex]).getResult(example);
        }
    }
}
