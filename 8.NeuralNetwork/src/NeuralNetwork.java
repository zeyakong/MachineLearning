import java.io.*;
import java.util.List;
import java.util.Random;

/**
 * This class represents a neural network which contains six state variables.
 * the learningRate is the alpha in the algorithm.(The assignment requires 1 as its static
 * value, but I try to modify this value when I try the ReLU function.)
 * the two dimensional array called neurons stores all the values of the output of all
 * the nodes activated by a function g(ln).(In this assignment, only use the sigmoid function.
 * I also tried the ReLU but the results are same.)
 * neurons[i][j]: i indicates the layer index. j means the position of the node in this layer.
 * For example: neurons[0][3] means the third input node in the input layer.
 * Same as delta and bias array, one stores the delta value for the back propagation another
 * stores the bias weight for this current node.
 * The three dimensional array called weights stores all the values of weights for all the
 * connection.
 * For example: weights[i][j][k]: weight for layer i, from node j to node k.
 * weights[1][2][3]: the weights of the third node(index is 2) in the layer 1(not the input layer.
 * input layer is layer 0)to next layer's fourth node(index is 3).
 * The outputError vector records the error value for each output node.
 */
public class NeuralNetwork {
    private static final double learningRate = 1;
    private double[][] neurons;
    private double[][][] weights;
    private double[][] bias;
    private double[][] delta;
    private double[] outputError;

    /**
     * The following constructor generates a new neural network.
     * The input List named layers is the list of layers(including the input/
     * hidden/output layers ). list.get(i) means the number of
     * neuron of layer i(i starts with 0).
     *
     * @param layers the list contains the number of neural for each layer.
     */
    public NeuralNetwork(List<Integer> layers) {
        //init all the state variables.
        this.neurons = new double[layers.size()][];
        this.weights = new double[layers.size()-1][][];
        this.delta = new double[layers.size()][];
        this.bias = new double[layers.size()][];
        this.outputError = new double[layers.get(layers.size() - 1)];


        //init the size of weights according to the input layer info
        for (int i = 0; i < layers.size(); i++) {
            neurons[i] = new double[layers.get(i)];
            delta[i] = new double[layers.get(i)];
            //no bias weight for the input layer
            if (i != 0) bias[i] = new double[layers.get(i)];
            if (i + 1 < layers.size()) {
                //for each layer i: we link each neuron to the neural of the layer i+1.
                weights[i] = new double[layers.get(i)][layers.get(i + 1)];
            }
        }

        //assign a random number to all weights from (0,1) double
        Random r = new Random();
        for (int layer = 0; layer < weights.length - 1; layer++) {
            for (int from = 0; from < weights[layer].length; from++) {
                for (int to = 0; to < weights[layer][from].length; to++) {
                    //give a random double(0,1)
                    weights[layer][from][to] = r.nextDouble();
                }
            }
        }
        //assign the bias weights from (0,1) double
        for (int layer = 1; layer < bias.length; layer++) {
            for (int i = 0; i < bias[layer].length; i++) {
                bias[layer][i] = r.nextDouble();
            }
        }
    }

    /**
     * The following method is the back propagation implementation for this neural network.
     * The rough processing is :
     * 1. from top to down(input -->  output): calculate the value for each node.( g(ln) ).
     * 2. from down to top (output layer  ---> input layer) calculate the delta for each node.
     * 3. according to the delta value: update the weights (including the bias weights )
     * 4. loop 1 - 3 for 1000 times or find the break condition(MaxErr < 0.01)
     * The algorithm follows the handbook (Russell & Norvig, 18.7)
     *
     * @param inputLayer  the array of the input layer values (double value)
     * @param outputLayer the output vector represents as an double array.
     *                    Tell the network the result of the output and train this network
     *                    to update all the weights.
     */
    public void backPropagation(double[] inputLayer, double[] outputLayer) {
        for (int c = 0; c < 1000; c++) {
            //propagate the inputs forward to compute the outputs
            double[] output = predict(inputLayer);
            //propagate deltas backward from output layer to input layer
            //i: the output layer index.
            int i = neurons.length - 1;
            //for each node j in the output layer i do
            for (int j = 0; j < delta[i].length; j++) {
                outputError[j] = outputLayer[j] - neurons[i][j];
                //the derivative of sigmoid(x) is sigmoid(x) * [1 - sigmoid(x)]
                delta[i][j] = neurons[i][j] * (1 - neurons[i][j]) * outputError[j];
                //delta[i][j] = derivativeReLU(neurons[i][j]) * outputError[j];
            }
            //double total = 0.0;
            //record the max error value.
            double maxError = 0.0;
            for (int e = 0; e < outputError.length; e++) {
                if (Math.abs(outputError[e]) > maxError) maxError = Math.abs(outputError[e]);
                //total += Math.pow(outputError[e], 2.0) / 2.0;
            }
            //we can finish the loop because the max of the error is very low.
            if (maxError < 0.01) {
                //System.out.println("After " + c + " times. Finished!" + Arrays.toString(output));
                return;
            }
            //System.out.println("loop"+c+", Total:"+total+", outputError:" + Arrays.toString(outputError));

            //for l = L-1 to 1 do
            for (int l = i - 1; l >= 0; l--) {
                //for each node k in layer l do
                for (int k = 0; k < delta[l].length; k++) {
                    double sum = 0;
                    for (int m = 0; m < delta[l + 1].length; m++) {
                        //using sigmoid
                        sum += neurons[l][k] * (1 - neurons[l][k]) * weights[l][k][m] * delta[l + 1][m];
                        //sum += derivativeReLU(neurons[l][k])* weights[l][k][m] * delta[l + 1][m];
                        weights[l][k][m] = weights[l][k][m] + learningRate * neurons[l][k] * delta[l + 1][m];
                        bias[l + 1][m] = bias[l + 1][m] + learningRate * delta[l + 1][m];
                    }
                    delta[l][k] = sum;
                }
            }
        }
    }

    /**
     * The following method calculates the output vector for the given input vector for
     * the current neural network weights.
     * The size of input vector must be same to the structure given by a user
     * when the user create the network. Same as the output vector.
     * All the vectors are represented as arrays which make the calculation easier.
     *
     * @param inputLayer the input vector. represents as an array.
     * @return the output vector. array format.
     */
    public double[] predict(double[] inputLayer) {
        //the input layer
        for (int i = 0; i < neurons[0].length; i++) {
            neurons[0][i] = inputLayer[i];
        }
        //the hidden layer and output layer. from 1 to layer length
        for (int layer = 1; layer < neurons.length; layer++) {
            //find the position of the neuron.
            for (int i = 0; i < neurons[layer].length; i++) {
                double sum = 0;
                //find all neural of last layer
                // because all neurons are connected between two adjacent layers
                for (int j = 0; j < neurons[layer - 1].length; j++) {
                    //this layer. the weight from j to i times value(ln).
                    sum += weights[layer - 1][j][i] * neurons[layer - 1][j];
                }
                //add bias
                sum += bias[layer][i];
                //active the neuron.

//                neurons[layer][i] = ReLu(sum);
                neurons[layer][i] = sigmoid(sum);
//                System.out.println(sum);
//                System.out.println(neurons[layer][i]);
            }
        }
        //the output is the last layer
        return neurons[neurons.length - 1];
    }

    /**
     * The following method returns the sigmoid activation value for the giving input.
     *
     * @param in input double value.
     * @return the value of the sigmoid function output.
     */
    private double sigmoid(double in) {
        return 1 / (1 + Math.exp(-in));
    }

    /**
     * The following method give the output of the ReLU function.
     *
     * @param in double value input
     * @return the value of the ReLU(in)
     */
    private double ReLu(double in) {
        if (in < 0) return 0;
        return in;
    }

    /**
     * The following method calculates the derivative of the ReLU function for the given input
     * double in.
     *
     * @param in
     * @return ReLU'(in) - derivative value
     */
    private double derivativeReLU(double in) {
        if (in <= 0) return 0;
        return 1;
    }

    /**
     * The following method writes all the NeuralNetwork information into local disk.
     * The encoding of the file is text but the file extension is assigned by the user.
     * The String parameter is the fileName with the file extension. There
     * is no limit of the file extension.
     *
     * @param fileName the filename with the file extension.
     */
    public void saveToFile(String fileName) {
        try {
            File file = new File(fileName);
            FileWriter out = new FileWriter(file);
            //save the size of each layer.
            for(int i=0 ;i<neurons.length;i++){
                out.write(neurons[i].length+ "\t");
            }
            out.write("\n");
            //save weights and bias.
            for (int i = 0; i < weights.length; i++) {
                for (int j = 0; j < weights[i].length; j++) {
                    for (int k = 0; k < weights[i][j].length; k++) {
                        out.write(weights[i][j][k] + "\t");
                    }
                }
            }
            out.write("\n");
            for (int j = 1; j < bias.length; j++) {
                for (int k = 0; k < bias[j].length; k++) {
                    out.write(bias[j][k] + "\t");
                }
            }
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * The following constructor is used to create a neural network by given weights and bias.
     * the given weights array indicates the number of layers and the size of each layer.
     *
     * @param weights the three dimensional double array indicates all the value of all
     *                connections.
     * @param bias    bias weights for each node.(two dimension)
     */
    public NeuralNetwork(double[][][] weights, double[][] bias) {
        this.weights = weights;
        this.bias =bias;
        this.neurons = new double[bias.length][];
        this.delta = new double[bias.length][];
        this.outputError = new double[bias[bias.length-1].length];
        for (int i = 0; i < weights.length; i++) {
            neurons[i] = new double[weights[i].length];
            delta[i] = new double[weights[i].length];
        }
        neurons[neurons.length-1] = new double[outputError.length];
        delta[delta.length-1] = new double[outputError.length];
    }
}
