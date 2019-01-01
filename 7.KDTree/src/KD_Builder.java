import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * This class will builder a KDTree for the given train data-set and use the generated
 * KDTree to find the nearest neighbor and calculate the distance by using test data-set.
 * The state variables data_dimension records the dimension of the train data-set.
 * The min_size records the input S(minimum size for each set in the algorithm) by a user.
 * The printCount is used to count the number of the print tree set method.
 *
 * Notice: this system don't have any verification for any user's inputs. So the user must input
 * correct string(file name, min size, ect...). if the user input an incorrect input such as a negative S,
 * this system will crash.
 */
public class KD_Builder {
    private int data_dimension;
    private int min_size;
    private int printCount;

    public KD_Builder() {
    }

    /**
     * This is the entrance of this app. It needs two run parameters for the train file name
     * and the min size for each KDTree's set.
     * @param args
     */
    public static void main(String[] args) {
        String file_path = args[0];
        String min_size = args[1];
        Scanner scanner = new Scanner(System.in);
        KD_Builder kd_builder = new KD_Builder();
        kd_builder.run(file_path, min_size, scanner);
    }

    /**
     * The following method contains all the command line's interaction with the user.
     * First, it requires the user to input the file name of the train data-set.
     * After, it will ask the use if print the tree set info or not.
     * Finally, it will ask the use if test the tree or not and if the user answered yes,
     * the system will require the user input the test file name.
     * @param file_name the train file name
     * @param min_size the min size of each set of the KDTree.
     * @param scanner system input scanner.
     */
    private void run(String file_name, String min_size, Scanner scanner) {
        //use double[] to store the point information.
        //the length of double[] is the dimension of the point.
        //Notice: double[i+1] represents the value of the dimension i of this point.
        List<double[]> trainSet = new ArrayList<>();
        loadData(file_name,trainSet);
        this.min_size = Integer.parseInt(min_size);
        KDTree tree = buildTree(trainSet, 1);
        System.out.print("\nPrint tree leaves? (Enter Y for yes, anything else for no): ");
        String input = scanner.next();
        printCount = 0;
        if (input.equalsIgnoreCase("y")) {
            System.out.println("\n------------------------\n");
            printLeaves(tree, "");
            System.out.println("\n------------------------\n");
        }
        System.out.print("Test data? (Enter Y for yes, anything else to quit): ");
        input = scanner.next();
        if (input.equalsIgnoreCase("y")) {
            System.out.print("Name of data-file: ");
            input = scanner.next();
            List<double[]> testSet = new ArrayList<>();
            loadData(input,testSet);
            System.out.println("\n------------------------\n");
            printTestResult(tree , testSet);
            System.out.println("\n------------------------\n");
        }
        //polite finishing
        System.out.println("Good bye.");
    }

    /**
     * The following method will Travers all the points and print the nearest node and distance
     * info into the command line.
     * @param tree the KDTree trained by the same dimension data.
     * @param testSet the set of test points.
     */
    private void printTestResult(KDTree tree, List<double[]> testSet) {
        for(double[] one: testSet){
            tree.findPointSet(one);
        }
    }


    /**
     * This method will print all the leafs' info, including boundary, and all the points for each set.
     * It will use the state variable printCount to record the set number.
     * @param tree the trained KDTree.
     * @param pre the prefix string which used to store the L or R info.
     */
    private void printLeaves(KDTree tree, String pre) {
        if (tree != null) {
            if (tree.getLeftTree() == null && tree.getRightTree() == null && tree.getPointSet().size() > 0) {
                System.out.println(printCount + ". " + pre +": Bounding Box: "+ tree.getBoundaryString() + "\nData in leaf: " + tree.toStringSet() + "\n");
                printCount++;
            } else {
                printLeaves(tree.getLeftTree(), pre + "L");
                printLeaves(tree.getRightTree(), pre + "R");
            }
        }
    }

    /**
     * This method will load the data which name was given by the first parameter and
     * assign to the second parameter's list.
     * @param file_name the load file name
     * @param examples the list will store all the data.
     */
    private void loadData(String file_name,List<double[]> examples) {
        //hard-code path. assume all the input data was stored at this root.
        String file_path = "inputData/" + file_name;
        try {
            FileInputStream fileInputStream = new FileInputStream(file_path);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String strTmp = "";
            String[] temp;
            data_dimension = Integer.parseInt(bufferedReader.readLine());
            while ((strTmp = bufferedReader.readLine()) != null) {
                temp = strTmp.split(" ");
                double[] temp_double = new double[data_dimension];
                for (int i = 0; i < data_dimension; i++) {
                    temp_double[i] = Double.parseDouble(temp[i]);
                }
                examples.add(temp_double);
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This is the core code for the KDTree generation.
     * It follows the class PPT. It needs two parameters as the train data and the depth(current dimension)
     * @param data_points the training data-set, list format.
     * @param depth the current dimension.
     * @return KDTree with left, right subtree or a node only have value/ dimension of this value.
     */
    private KDTree buildTree(List<double[]> data_points, int depth) {
        //if the size of this set is less than or equals to this input min size, the loop is finished.
        if (data_points.size() <= min_size) {
            return new KDTree(data_points);
        } else {
            //get the median value for all the points of this loop.
            double median = getMedian(data_points, depth);
            //find all of the points witch less than median value and assign them into left tree.
            //same as right.
            List<double[]> leftData = new ArrayList<>(), rightData = new ArrayList<>();
            for (double[] point : data_points) {
                //less than or equals to.
                if (point[depth - 1] <= median) {
                    leftData.add(point);
                } else {
                    rightData.add(point);
                }
            }
            //this dimension was just finished, go next dimension.
            int split_dimension = (depth % data_dimension) + 1;
            //create subtree ofr left and right. Notice: it might be empty.
            KDTree leftTree = buildTree(leftData, split_dimension);
            KDTree rightTree = buildTree(rightData, split_dimension);
            KDTree tree = new KDTree(median,depth);
            tree.setLeftTree(leftTree);
            tree.setRightTree(rightTree);
            //return the tree.
            return tree;
        }
    }

    /**
     * The following method will return the medina value for the given data.
     * The calculation of median value depends on the size of the data.
     * For example, if the length of the data is odd number. the median is the value at the
     * middle position. Otherwise, the median is the average value of the two middle values for
     * the even size numbers.
     * @param data the input points
     * @param dimension the calculation dimension.
     * @return the double value of the median value for this dimension of the data.
     */
    private double getMedian(List<double[]> data, int dimension) {
        List<Double> numbers = new ArrayList<>();
        for (double[] point : data) {
            numbers.add(point[dimension - 1]);
        }
        Collections.sort(numbers);
        //even length, get the average of the two middle values
        if (numbers.size() % 2 == 0) {
            return (numbers.get(numbers.size() / 2) + numbers.get(numbers.size() / 2 - 1)) / 2;
        } else {
            //odd length, just get the middle value.
            return numbers.get(numbers.size() / 2);
        }
    }
}
