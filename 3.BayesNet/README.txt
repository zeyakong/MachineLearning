Some Conclussion:
I am sorry for my delaying because I found some bugs when I submited and tested my code.
I spent whole day and night finding where the bugs are and debuging the code. Now my code can get the same result to your samples by using the Enumerative Inference Algorithm.

The bug is very simple but I can not find it in short time because my data structure is very strange and the different funcitons has high coupling in my first version. The bug is the List Copy. If I just copyed the List use clone() method or anthor simple method, the previous List will be changed when I change the last one list. Because the jvm just use the pointer(index) to new list. Sometimes ,the different data type in list has different results when I do copying function. Same bugs happened in the HashMap. If my key is not simple type. I should implement the comparable() interface. Even I did this, the bug can not be fixed. So I redesigned the data struture and finished all of this...20 hours


This directory contains the following materials:

[1] A folder of <AS3.3>, which contains src java code and the data. The entry is BayesNet.java

