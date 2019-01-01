import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This class is the neural network builder class to build the network, train it and calculate
 * the accuracy of the network. All the data-set is img data and it is from Dr. Allen.
 * This class provides the command line UI which requires a user to give some inputs
 * when the program running.
 */
public class NeuralNetworkBuilder {
    //for each item(example): List.get(0): input layer double[] | List.get(1): output double[]

    /**
     * Main entrance for this class.
     *
     * @param args system args.
     */
    public static void main(String[] args) {
        NeuralNetworkBuilder neuralNetworkBuilder = new NeuralNetworkBuilder();
        Scanner scanner = new Scanner(System.in);
        neuralNetworkBuilder.run(scanner);
//        neuralNetworkBuilder.test();
    }

    /**
     * The following method is the command inputs of the NNBuilder.
     * it gains the information from a user and do the whole process.
     *
     * @param scanner the system input scanner.
     */
    private void run(Scanner scanner) {
        System.out.print("Enter L to load trained network, T to train a new one, Q to quit: ");
        String input = scanner.next();
        while (!input.equalsIgnoreCase("q")) {
            if (input.equalsIgnoreCase("l")) {
                System.out.print("\nNetwork file-name: ");
                String fileName = scanner.next();
                loadAndTestNetwork(fileName);
                System.out.print("\nEnter L to load trained network, T to train a new one, Q to quit: ");
                input = scanner.next();
            } else if (input.equalsIgnoreCase("t")) {
                System.out.print("\nResolution of data (5/10/15/20): ");
                while (scanner.hasNext()) {
                    input = scanner.next();
                    int i = 0;
                    if (isNumeric(input) && (i = Integer.parseInt(input)) == 10 || i == 15 || i == 20 || i == 5) {
                        int resolution = Integer.parseInt(input);
                        //create the data-set.
                        //each example is a List<double[]> stores the input and output layers
                        List<List<double[]>> trainData = new ArrayList<>();
                        List<List<double[]>> testData = new ArrayList<>();
                        String filename = "";
                        if (resolution == 5) filename = "05";
                        else filename = "" + resolution;
                        loadData("trainSet_data/trainSet_" + filename + ".dat", trainData);
                        loadData("testSet_data/testSet_" + filename + ".dat", testData);
                        dataNormalization(trainData, resolution);
                        dataNormalization(testData, resolution);
                        System.out.print("Number of hidden layers: ");
                        input = scanner.next();
                        int layersSize;
                        List<Integer> layers = new ArrayList<>();
                        //add the input layers.
                        layers.add(trainData.get(0).get(0).length);
                        if ((layersSize = Integer.parseInt(input)) > 0) {
                            for (int count = 1; count <= layersSize; count++) {
                                System.out.print("Size of hidden layer " + count + ":");
                                input = scanner.next();
                                layers.add(Integer.parseInt(input));
                            }
                        }
                        //add the output layers. The data set has the fixed output layer
                        layers.add(trainData.get(0).get(1).length);

                        //create the network by the given layers size.
                        NeuralNetwork neuralNetwork = new NeuralNetwork(layers);
                        System.out.println("Training on trainSet_" + filename + ".dat...");
                        trainNetwork(neuralNetwork, trainData);
                        System.out.println("Testing on trainSet_" + filename + ".dat/testSet_"+filename+"...");
                        double accuracyTrain = testNetwork(neuralNetwork, trainData);
                        double accuracyTest = testNetwork(neuralNetwork,testData);
                        System.out.println("Accuracy achieved: trainSet_"+filename+".dat: " + (accuracyTrain * 100+"").substring(0,5) +
                                "%| testSet_"+filename+".dat: "+(accuracyTest * 100+"").substring(0,5)+"%");
                        System.out.print("\nSave network (Y/N)? ");
                        input = scanner.next();
                        if (input.equalsIgnoreCase("y")) {
                            System.out.print("File-name: ");
                            input = scanner.next();
                            neuralNetwork.saveToFile(input);
                            System.out.println("Saving network...\n" +
                                    "Network saved to file: " + input);
                        }
                        System.out.print("\nEnter L to load trained network, T to train a new one, Q to quit: ");
                        input = scanner.next();
                        break;
                    } else {
                        System.out.println("Invalid input type!");
                        System.out.print("Please enter a training increment (either 5, 10, 15, or 20): ");
                    }
                }
            } else {
                System.out.println("Invalid input!");
                System.out.print("\nEnter L to load trained network, T to train a new one, Q to quit: ");
                input = scanner.next();
            }
        }
        System.out.println("\nGoodbye.");
    }

    /**
     * The following method will test a given neural network and return the accuracy from [0,1].
     *
     * @param neuralNetwork the neuralNetwork trained before.
     * @param testData      the test examples.
     * @return the accuracy : double from [0,1]
     */
    private double testNetwork(NeuralNetwork neuralNetwork, List<List<double[]>> testData) {
        double rightCount = 0.0;
        for (List<double[]> one : testData) {
            double[] output = neuralNetwork.predict(one.get(0));
//            System.out.println(Arrays.toString(output));
            int outputValue = 0;
            int expectedValue = 0;
            double max = 0;
            for (int i = 0; i < output.length; i++) {
                if (output[i] > max) {
                    max = output[i];
                    outputValue = i;
                }
                if (one.get(1)[i] == 1) {
                    expectedValue = i;
                }
            }
            if (outputValue == expectedValue) rightCount++;
        }
        return rightCount / testData.size();
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
     * The following method trains the network with the given training data-set.
     * The structure of training data-set must be same to the network. Fail Otherwise.
     *
     * @param neuralNetwork the neuralNetwork generated by random weights
     * @param trainData     the training examples stored as an array of double.
     */
    private void trainNetwork(NeuralNetwork neuralNetwork, List<List<double[]>> trainData) {
        for (List<double[]> one : trainData) {
            neuralNetwork.backPropagation(one.get(0), one.get(1));
        }
//        List<double[]> one = trainData.get(0);
//        double[] result = neuralNetwork.predict(one.get(0));
//        System.out.println("The First output:"+Arrays.toString(one.get(1))+",\noutput "+ Arrays.toString(result));
//        neuralNetwork.backPropagation(one.get(0),one.get(1));
//        result = neuralNetwork.predict(one.get(0));
//        System.out.println("After training 500times : "+ Arrays.toString(result));
    }

    /**
     * The following method loads the file from the src folder and the file name
     * given by the user. The file is text file and this method will read the file
     * line by line and generate the weights and bias arrays to create the network.
     * @param fileName
     */
    private void loadAndTestNetwork(String fileName) {
        System.out.println("\nLoading network from: " + fileName + "...");
        NeuralNetwork neuralNetwork = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String layersStr = bufferedReader.readLine();
            String weightStr = bufferedReader.readLine();
            String biasStr = bufferedReader.readLine();
            String[] temp = layersStr.split("\t");
            int[] layers = new int[temp.length];
            String hiddenLayerText = "";
            for (int i = 0; i < temp.length; i++) {
                layers[i] = Integer.parseInt(temp[i]);
                if (i > 0 && i < temp.length - 1) {
                    hiddenLayerText = hiddenLayerText + temp[i] + ", ";
                }
            }
            if (!hiddenLayerText.equals(""))
                hiddenLayerText = "\nHidden layer sizes: " + hiddenLayerText.substring(0, hiddenLayerText.length() - 2);
            System.out.println("Input layer size: " + layers[0] + " nodes"
                    + hiddenLayerText +
                    "\nOutput layer size: " + layers[layers.length - 1]);

            int resolution;
            String loadFileName;
            if (layers[0] == 65) {
                loadFileName = "Set_05.dat";
                resolution = 5;
            } else if (layers[0] == 140) {
                loadFileName = "Set_10.dat";
                resolution = 10;
            } else if (layers[0] == 265) {
                loadFileName = "Set_15.dat";
                resolution = 15;
            } else {
                loadFileName = "Set_20.dat";
                resolution = 20;
            }
            System.out.println("Testing on train/test"+loadFileName+"...");
            List<List<double[]>> testData = new ArrayList<>();
            List<List<double[]>> trainData = new ArrayList<>();
            loadData("testSet_data/test"+loadFileName, testData);
            loadData("trainSet_data/train"+loadFileName, trainData);
            dataNormalization(testData,resolution);
            dataNormalization(trainData,resolution);

            //create the weights and bias array.
            double[][][] weights = new double[layers.length - 1][][];
            double[][] bias = new double[layers.length][];

            for (int i = 0; i < layers.length; i++) {
                //no bias weight for the input layer
                if (i != 0) bias[i] = new double[layers[i]];
                if (i + 1 < layers.length) {
                    //for each layer i: we link each neuron to the neural of the layer i+1.
                    weights[i] = new double[layers[i]][layers[i + 1]];
                }
            }

            //assign values to them.
            temp = weightStr.split("\t");
            int count = 0;
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    for (int k = 0; k < weights[i][j].length; k++) {
                        weights[i][j][k] = Double.parseDouble(temp[count]);
                        count++;
                    }
                }
            }

            temp = biasStr.split("\t");
            count = 0;
            for (int j = 1; j < bias.length; j++) {
                for (int k = 0; k < bias[j].length; k++) {
                    bias[j][k] = Double.parseDouble(temp[count]);
                    count++;
                }
            }
            neuralNetwork = new NeuralNetwork(weights, bias);
            System.out.println("Accuracy achieved: train"+loadFileName+": " + (testNetwork(neuralNetwork, trainData) * 100+"").substring(0,5) +
                    "%| test"+loadFileName+": "+(testNetwork(neuralNetwork, testData) * 100+"").substring(0,5)+"%");
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * The following method normalizes the data. it will make the range of all the value in
     * input layers from 0 to 1. the value will be divided by 255.
     *
     * @param data       input data set
     * @param resolution the number of value we need to divided.
     */
    private void dataNormalization(List<List<double[]>> data, int resolution) {
        int limit = (int) Math.pow(resolution, 2);
        for (List<double[]> example : data) {
            //normalize the last n2 values.
            double[] one = example.get(0);
            int i = one.length - 1;
            int count = 0;
            while (count < limit) {
                one[i] = one[i] / 255;
                i--;
                count++;
            }
        }
    }

    /**
     * This method will load the data which name was given by the first parameter and
     * assign to the second parameter's list.
     *
     * @param file_path the load file path(hard-coded prefix)
     * @param examples  the list will store all the data.
     */
    private void loadData(String file_path, List<List<double[]>> examples) {
        //hard-code path. assume all the input data was stored at this root.
        try {
            FileInputStream fileInputStream = new FileInputStream(file_path);
            InputStreamReader isr = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String strTmp = "";
            String[] temp;
            String inputString, outputString;
            while ((strTmp = bufferedReader.readLine()) != null) {
                if (!strTmp.startsWith("#")) {
                    List<double[]> oneExample = new ArrayList<>();
                    inputString = strTmp.substring(1, strTmp.length() - 31);
                    outputString = strTmp.substring(strTmp.length() - 28, strTmp.length() - 1);
                    temp = inputString.split(" ");
                    double[] inputs = new double[temp.length];
                    for (int i = 0; i < temp.length; i++) {
                        inputs[i] = Double.parseDouble(temp[i]);
                    }
                    oneExample.add(inputs);
                    temp = outputString.split(" ");
                    double[] outputs = new double[temp.length];
                    for (int i = 0; i < temp.length; i++) {
                        outputs[i] = Double.parseDouble(temp[i]);
                    }
                    oneExample.add(outputs);
                    examples.add(oneExample);
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
