################################
This zip file contains
[1] this README.txt file
[2] the source folder /src
in src:
    1. two .java file which are the network program. The entrance of the program is NeuralNetworkBuilder.java. in order to run this program in command line, you must compile it and try
        $ java NeuralNetwork
    And it will run and ask you to give some inputs to generate the network same as your samples.(I didn't verify the input format. So, if the inputs are unreasonable, the program might be crashed.)\
    2. tranSet_data folder: contains all the train data set.
    3. testSet_data folder: contains all the test data set.
        All the path/name of the train/test data-set are hard-coded in the code, which means changing the file name and file path might cause some errors in this program.
    4. Network1.nnt: I stored this in the desk and you can load it to test this functionality. the encoding of this file is text UTF-8. So, if the load method can't work in your machine, the reason might be the encoding error. I didn't test it but it can generate the network has the same structure to the previous one I stored in my own machine.

#################################
Actually, the program can generate the network successfully but the accuracy for the testData set is very different from your sample results. In most of the cases, the network I generated will output the same accuracy for all the testData set even the trainData set.
So, I debugged the whole processing and I found that whatever the input is, the output is same. After I normalized all the trainSet from your guidance, the outputs of the network are still stable, it means this network will give all of the inputs the same classification.
I tried to make the network more complex or change the learning rate/range of random weights and it still can't fix this error. After I submitted this in Friday, I still worked for this problem. I searched some information from website and found some issues called vanishing gradient or dead neuron. I believed the algorithm is reasonable and I think the problem is the coding problem. I reviewed my code line by line, step by step but I still can't find the error. I also tried other activation function such as ReLU and tanh but I failed for some reason.