import java.text.DecimalFormat;
import java.util.List;

/**
 * This class represents the KDTree which includes five attributes. There are two kinds of
 * KDTrees. One is node with the median value and anther is leaf with the set of points(called
 * pointSet). For the first kind of KDTree, it has nodeValue and
 * the dimension of this value(named valueDimension) which will indicate which dimension of this test point to be
 * classified. For the second kind of KDTree, it has a List stores all the points(called pointSet)
 * which is the leaf of this tree and the left, right subtree. On conclusion, if the pointSet of the tree is not null,
 * the nodeValue and valueDimension are no meaningful.
 * The KDTree stores either value or right, left tree. The right, left subTree are also KDTree.
 */
public class KDTree {
    private double nodeValue;
    private int valueDimension;
    private List<double[]> pointSet;
    private KDTree leftTree;
    private KDTree rightTree;

    /**
     * The following constructor represents the non-leaf tree node for the KDTree.
     * It will be assigned two parameters as the median value and dimension of this value.
     *
     * @param value          the value of this node. in this specific case, it must be median value.
     * @param valueDimension the dimension of this value
     */
    public KDTree(double value, int valueDimension) {
        this.valueDimension = valueDimension;
        this.nodeValue = value;
    }


    /**
     * The following constructor represents the leaf of the KDTree. It allows a user
     * to assign a list of points to this leaf.
     *
     * @param pointSet the list of points.(point means a test/train case.)
     */
    public KDTree(List<double[]> pointSet) {
        this.pointSet = pointSet;
    }

    public List<double[]> getPointSet() {
        return pointSet;
    }

    public double getNodeValue() {
        return nodeValue;
    }

    public KDTree getLeftTree() {
        return leftTree;
    }

    public void setLeftTree(KDTree leftTree) {
        this.leftTree = leftTree;
    }

    public KDTree getRightTree() {
        return rightTree;
    }

    public void setRightTree(KDTree rightTree) {
        this.rightTree = rightTree;
    }


    /**
     * Only for the leaf KDTree. The following method will return the format string of the
     * set of points.
     *
     * @return the format string of all the set of points.
     */
    public String toStringSet() {
        String result = "[";
        for (double[] point : pointSet) {
            result = result + "(";
            for (double value : point) {
                result = result + value + " ";
            }
            //substring is to remove the last space
            result = result.substring(0, result.length() - 1) + "), ";
        }
        result = result.substring(0, result.length() - 2) + "]";
        return result;
    }

    /**
     * The following method is only for the leaf KDTree. It will return the format
     * String of the boundary value for all the set of points. The boundary is two points.
     * One has the minimum values for all dimensions and anther has the maximum values for
     * all dimensions.
     *
     * @return the format string of the coordinate of the two boundary points.
     */
    public String getBoundaryString() {
        //validate the pointSet.
        if (pointSet != null && pointSet.size() > 0) {

            int dimension = pointSet.get(0).length;
            //max and min points.
            double[] max = new double[dimension];
            double[] min = new double[dimension];
            //the default double value is 0.0, So, must assign a value to min point to find the min
            for (int i = 0; i < dimension; i++) {
                min[i] = pointSet.get(0)[i];
            }
            for (int i = 0; i < dimension; i++) {
                for (double[] one : pointSet) {
                    if (one[i] > max[i]) {
                        max[i] = one[i];
                    }
                    if (one[i] < min[i]) {
                        min[i] = one[i];
                    }
                }
            }
            String result = "[(";
            for (int i = 0; i < dimension; i++) {
                result = result + min[i] + " ";
            }
            result = result.substring(0, result.length() - 1) + "), (";
            for (int i = 0; i < dimension; i++) {
                result = result + max[i] + " ";
            }
            result = result.substring(0, result.length() - 1) + ")]";
            return result;
        }
        return "Error: null pointSet";
    }

    /**
     * The following method will find the set of given point and print the information into command line.
     *
     * @param point the given point want to find the set of the tree.
     */
    public void findPointSet(double[] point) {
        if (getPointSet() != null) {
            //str is the given point information
            String str = "(";
            for (double one : point) {
                str = str + one + " ";
            }
            str = str.substring(0, str.length() - 1) + ")";

            //check the tree set is empty or not.
            if (getPointSet().size() == 0) {
                System.out.println(str + " has no nearest neighbor (in an empty set).\n");
            } else {
                //assign the minDistance a infinity value to find the min value.
                double minDis = Double.POSITIVE_INFINITY;
                double distance = 0.0;

                //this index records the min point.
                double[] minNode = null;
                for (double[] one : getPointSet()) {
                    //calculate the Euclidean distance
                    for (int i = 0; i < one.length; i++) {
                        distance += Math.pow((point[i] - one[i]), 2);
                    }
                    distance = Math.sqrt(distance);
                    if (distance < minDis) {
                        minDis = distance;
                        minNode = one;
                    }
                    //recover the distance value for next loop calculation.
                    distance = 0.0;
                }
                //The temp string represents the nearest neighbor information.
                String temp = "(";
                for (double val : minNode) {
                    temp = temp + val + " ";
                }
                //output as a format string.
                DecimalFormat df = new DecimalFormat("#.######");
                temp = temp.substring(0, temp.length() - 1) + ")";
                System.out.println(str + " is in the set: " + toStringSet() + "\nNearest neighbor: " + temp + " (distance = " + df.format(minDis) + ")\n");
            }
        } else {
            //the dimension of value use index to represent. because the index of array is from
            //0, but the dimension begin from 1. So, the point[i-1] means the dimension i of this point.
            if (point[valueDimension - 1] <= getNodeValue()) {
                //if the value of given point is less then the median value, go left tree.
                getLeftTree().findPointSet(point);
            } else {
                //else go right tree.
                getRightTree().findPointSet(point);
            }
        }
    }
}
