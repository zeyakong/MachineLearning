
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.*;

/**
 * This class will generate the decision tree for the mushroom data given by Dr.Allen. The state variable
 * trainSize is the size of the training data-set which will be inputted by the user. Same as increment.
 * The List called attributes stores all the attributes loaded at file called properties.
 * I use a String[] to store all the info for one attribute. For example,
 * attributes.get(i) means get the #i attribute. For this attribute, all of the
 * values stored as String. For example, {b,c,x,f,k,s} are the values of properties #0 in the data-set.
 * The examples list is same to the attributes list. The size of the examples list is the size of the
 * data-set. For each example, Using String to store all the information, including the result P or E.
 * Because the attributes for this question is fixed, using those data structure makes the implementation
 * easier. The branchCount is used to store the number of branch which will be printed in the command line.
 */
public class DecisionTree {
    private int trainSize;
    private int increment;
    private List<String[]> attributes;
    private List<String[]> examples;
    private int branchCount;

    /**
     * The following constructor will initialize some the state variables.
     */
    public DecisionTree() {
        attributes = new ArrayList<>();
        examples = new ArrayList<>();
        branchCount = 0;
    }

    /**
     * The following method will check if input(string format) is number or not. If input is a integer, it will
     * return true. False otherwise.
     *
     * @param str the input string which will be checked.
     * @return the result of the validation.
     */
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * The following method will ask the user to input two integer values for the state variable
     * trainSize and increment. This method will validate the input. If the user's input is
     * wrong(whatever the type or range of the values are), the system will ask the user to try again.
     *
     * @param scanner the system input scanner.
     */
    private void getInput(Scanner scanner) {
        System.out.print("Welcome, this program will generate the mushrooms' decision tree.\n" +
                "Please enter a training set size (a positive multiple of 250 that is <= 1000): ");
        String input = "";
        while (scanner.hasNext()) {
            input = scanner.nextLine();
            //validate the input format.
            if (isNumeric(input) && Integer.parseInt(input) % 250 == 0) {
                //assign the value to the state variable trainSize.
                trainSize = Integer.parseInt(input);
                break;
            } else {
                System.out.println("Invalid input type!");
                System.out.print("Please enter a training set size (a positive multiple of 250 that is <= 1000): ");
            }
        }
        System.out.print("Please enter a training increment (either 10, 25, or 50): ");
        while (scanner.hasNext()) {
            input = scanner.nextLine();
            int i = 0;
            if (isNumeric(input) && (i = Integer.parseInt(input)) == 10 || i == 25 || i == 50) {
                increment = Integer.parseInt(input);
                break;
            } else {
                System.out.println("Invalid input type!");
                System.out.print("Please enter a training increment (either 10, 25, or 50): ");
            }
        }
    }

    /**
     * The following method will load two local txt files into the system. The data is
     * provided by Dr.Allen. The file must be named mushroom_data.txt and properties.txt.
     * The path of the file must be in the root(/src/). This method will store each case
     * as a java object and use a list to store all the information.
     */
    private void loadData() {
        System.out.println();
        loadProperties();
        loadExamples();
    }

    /**
     * The following method will load the data from the local .txt file named properties.txt
     */
    private void loadProperties() {
        System.out.println("Loading Property Information from file.");
        try {
            FileInputStream fileInputStream = new FileInputStream("properties.txt");
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String strTmp = "";
            //read line by line
            while ((strTmp = bufferedReader.readLine()) != null) {
                String[] temp = strTmp.substring(strTmp.indexOf(":") + 2).split(" ");
                attributes.add(temp);
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * The following method will store all the mushroom info into state variable examples from the
     * local file.
     */
    private void loadExamples() {
        System.out.println("Loading Data from database.");
        try {
            FileInputStream fileInputStream = new FileInputStream("mushroom_data.txt");
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String strTmp = "";
            while ((strTmp = bufferedReader.readLine()) != null) {
                String[] temp = strTmp.split(" ");
                examples.add(temp);
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * The following method will find the plurality value from the given data.
     *
     * @param examples the data want to be found the plurality value.
     * @return the classification E or P .
     */
    private String pluralityValue(List<String[]> examples) {
        int count = 0;
        for (String[] e : examples) {
            if (e[e.length - 1].equals("e")) {
                count++;
            }
        }
        //if the count is over or equals to the half data size . return this.
        if (count * 2 >= examples.size()) {
            return "e";
        } else {
            return "p";
        }
    }

    /**
     * The following method will check if the input data has the same classification or not.
     *
     * @param examples input data-set
     * @return the boolean result. True : same | false : not same.
     */
    private boolean isSameClassification(List<String[]> examples) {
        if (examples == null || examples.size() == 0) {
            return false;
        }
        String flag = examples.get(0)[examples.get(0).length - 1];
        for (String[] one : examples) {
            if (!flag.equals(one[one.length - 1])) return false;
        }
        return true;
    }

    /**
     * The following method will check if all of the attributes are used or not. It will check the used table
     * to find the answer.
     *
     * @param used the used table for all the attributes.
     * @return
     */
    private boolean allAttrUsed(boolean[] used) {
        for (boolean b : used) {
            if (!b) return false;
        }
        return true;
    }

    /**
     * The following method is the decision tree finding algorithm. The parameter example is the input data-set
     * and the boolean[] used is the table for all the attributes. Because this class connect the
     * List<String[]> examples and List<String[]> attributes by using attribute index. So when the algorithm goes,
     * it should not change the attributes structure. The output for this method is a tree structure which
     * will store the attrib # and the value ect...
     *
     * @param examples        input data-set
     * @param used            the used table for all the attributes.
     * @param parent_examples parent data-set which will be used to find the plurality value when the
     *                        example is null
     * @return Tree.
     */
    private Tree decisionTreeLearning(List<String[]> examples, boolean[] used, List<String[]> parent_examples) {
        //empty example case:
        if (examples == null || examples.size() == 0) {
            return new Tree(pluralityValue(parent_examples));
        }
        //all the data have the same classification.
        else if (isSameClassification(examples)) {
            return new Tree(examples.get(0)[examples.get(0).length - 1]);
        }
        //all the attributes are used.
        else if (allAttrUsed(used)) {
            return new Tree(pluralityValue(examples));
        } else {
            //find attribute A which have the biggest importance
            //Using double to make calculate more precision.
            double max = 0;
            //the index of the max important attribute.
            int maxIndex = 0;
            for (int i = 0; i < attributes.size(); i++) {
                if (used[i]) continue;
                double importanceTemp = importance(examples, i);
                if (importanceTemp > max) {
                    max = importanceTemp;
                    maxIndex = i;
                }
            }

            //create a tree which node is the maxIndex of the attribute
            Tree t = new Tree(maxIndex);
            //find this attribute.
            String[] att = attributes.get(maxIndex);
            //for each value vk.
            for (String vk : att) {
                List<String[]> exs = new ArrayList<>();
                for (String[] e : examples) {
                    if (e[maxIndex].equals(vk)) {
                        //separate the example data by vk
                        exs.add(e);
                    }
                }
                //change the used table to show this attribute is used.
                used[maxIndex] = true;
                //create the subtree
                Tree subTree = decisionTreeLearning(exs, used, examples);
                t.addSubTree(subTree, vk);
            }
            return t;
        }
    }

    /**
     * The following method will calculate the importance for the given data and attribute number(called
     * attrIndex). The importance is Gain(a) = H(Goal) - Reminder(a). Because the H(Goal) of each loop is
     * same, this method just ignore the H(Goal) calculation and give H(Goal) a static value to make
     * calculation easier.
     *
     * @param data      The data list will be calculated.
     * @param attrIndex the attribute.
     * @return the value( double) of Gain(a).
     */
    private double importance(List<String[]> data, int attrIndex) {
        String[] attribute = attributes.get(attrIndex);
        //H(Goal). Give a reasonable  value.
        double H = 1.0;
        //Reminder(a)
        double r = 0.0;
        //pk and nk .the positive number and negative number.
        double pk = 0, nk = 0;
        for (String att : attribute) {
            for (String[] one : data) {
                if (one[attrIndex].equals(att)) {
                    if (one[one.length - 1].equals("e")) {
                        pk++;
                    } else {
                        nk++;
                    }
                }
            }
            if ((pk + nk) != 0) {
                r = (pk + nk) / data.size() * B((pk / (pk + nk))) + r;
            }
            pk = 0;
            nk = 0;
        }
        return (H - r);
    }

    /**
     * Java don't have api for log2 n. Using formula loga b = ln a / ln b to calculate.
     *
     * @param n the number for log2 n
     * @return the value of log2 n.
     */
    private double log2(double n) {
        return Math.log(n) / Math.log(2);
    }

    /**
     * This method will calculate the bool probability entropy.
     *
     * @param q the probability.
     * @return the value for B(q)
     */
    private double B(double q) {
        if (q == 0.0 || q == 1.0) return 0.0;
        return -(q * log2(q) + (1 - q) * log2(1 - q));
    }

    /**
     * The following method will test the decision tree algorithm and print all the info into the
     * command line.
     */
    private void run() {
        Scanner scanner = new Scanner(System.in);
        getInput(scanner);
        scanner.close();
        loadData();
        System.out.println();

        //choose the train data randomly.
        Collections.shuffle(examples);

        System.out.println("Collecting set of " + trainSize + " examples.");
        List<String[]> trainSet = examples.subList(0, trainSize);

        //remove all the train data
        examples.removeAll(trainSet);
        //one training loop
        List<String[]> oneLoop;
        //to store the last tree which will be print into the command line.
        Tree finalT = null;
        //this will store the statistics info.
        List<double[]> statisticsInfo = new ArrayList<>();

        //increase the train data to find the accuracy change.
        for (int i = increment; i <= trainSize; i = i + increment) {
            double[] statistics = new double[2];
            System.out.println("\nRunning with " + i + " examples in training set.");
            oneLoop = trainSet.subList(0, i);
            //default is false. means all the attrib are never used.
            boolean[] used = new boolean[attributes.size()];
            Tree t = decisionTreeLearning(oneLoop, used, oneLoop);
            finalT = t;
            int rightCount = getAccuracyNumber(t, examples);
            DecimalFormat df = new DecimalFormat("#.####");
            statistics[0] = i;
            statistics[1] = (double) rightCount / examples.size() * 100;

            statisticsInfo.add(statistics);
            System.out.println("\nGiven current tree, there are " + rightCount + " correct classifications\n" +
                    "out of " + examples.size() + " possible (a success rate of " + df.format(statistics[1]) + " percent).");

        }
        System.out.println("\n     -------------------\n" +
                "     Final Decision Tree\n" +
                "     -------------------\n");
        printBranch("", finalT);
        System.out.println("\n     ----------\n" +
                "     Statistics\n" +
                "     ----------");
        System.out.println();
        for (double[] s : statisticsInfo) {
            DecimalFormat df = new DecimalFormat("#.###");
            System.out.println("Training set size: " + (int) s[0] + ".  Success:  " + df.format(s[1]) + " percent.");
        }

    }

    /**
     * The following method will return the right number by using given tree and test data-set.
     *
     * @param t        the decision tree
     * @param examples the test data-set.
     * @return the number of the right predict.
     */
    private int getAccuracyNumber(Tree t, List<String[]> examples) {
        int rightCount = 0;
        for (String[] e : examples) {
            if (t.getResult(e).equals(e[e.length - 1])) {
                rightCount++;
            }
        }
        return rightCount;
    }

    /**
     * The following will print all the Branch info into command line.
     *
     * @param pre the pre node string
     * @param t   the decision tree
     */
    private void printBranch(String pre, Tree t) {
        //check if the tree is the leaf.
        if (t.isLeaf()) {
            String s = t.getLeaf();
            if (s.equals("e")) {
                System.out.print("Branch[" + branchCount + "]:    " + pre + " Edible.\n");
                branchCount++;
            } else {
                System.out.print("Branch[" + branchCount + "]:    " + pre + " Poison.\n");
                branchCount++;
            }
        } else {
            int attrIndex = t.getAttributeIndex();

            String[] attribute = attributes.get(attrIndex);
            for (String a : attribute) {
                String temp = pre + " Attrib #" + attrIndex;
                temp = temp + ": " + a + ";";
                Tree sub = t.getSubTree(a);
                printBranch(temp, sub);
            }
        }
    }

    /**
     * The following method is the entrance of this class.
     *
     * @param args default running args.
     */
    public static void main(String[] args) {
        DecisionTree decisionTree = new DecisionTree();
        decisionTree.run();
    }
}
