Hi Professor.

This zip file includes 2 things.

[1] This readme.txt instruction.

[2] src folder
    -inputData folder: includes all the input data, which contains training data and test data of 2 to 5 dimension.
    -KDTree.java: the KDTree structure implemented by Java.
    -KD_Builder.java: the entrance of this app. Because this app was written by Java and it is a command line program, So you need to use command line to run this program such as:
       ~java KD_Builder 4d.txt 5
       The number of parameters must be two because of the requirements. One is training file name , another is min size of set called S. Otherwise the program will be crashed.
       Because the load file method is hard-coded, the path of the input files must stores at /hw2-Kong/src/inputData/. Otherwise the program will be crashed.
       When you run this program with correct parameters and all the files stored at the correct path. It will run as your sample.
       If the parameter S you input is less than 1, the program will be stackoverflow because the build tree method will never be stopped.

I tested all the cases from your samples and I got the similar output except a few differences. I think the difference is from the median value calculation(I explain it clearly in this method's code comments). I tried to modify the median calculation to make the output same to your samples but I failed. After that, I compared the distance of each test data and I think my output is also reasonable. So, I hope my program is completed.

Have A Good Day!

Student,
Zeya Kong.
