This zip file contains four files/folders.

[1] this README file which contains the analysis of the two different learning algorithms.

[2] a src folder which contains the java source code and the pipe_world txt file.

[3] The graph shows the variety of rewards per 100 episode between Q learning and feature-Q learning.

I used Java to implement the assignment. The entrance of the program is the StartLearn.java.
In order to run this program, first you must compile the java code by using:
    /src $javac QLearning.java
then
    /src $java QLearning
It will show all the information same as your explanation in the class.

Because I wrote the same Q Learning last year in your AI course. When I went though my previous code, I found that the code has many unnecessary complexity data structure. So this year I redesigned all the code and followed the Software Engineering principles to try to make a elegant code.
The idea is from your code when you shown your code mistakes about the randomness bug. I noticed that using the position array to represent a state is much easier than using an object.
So, I use a three dimensional array to store the Q value instead of a HashMap to represent the many-to-one relationship. The code comments show the details.

The graph shows that the feature-based Q learns faster and the curve is stable compare to the basic Q learning.
From the trend of the curve, the total rewards of Q learning goes worse at first time because of the E-greedy policy.
After many time training, because of the decrease of the randomness, the agent will following the experience.
The policy matrix of the feature-based Q leaning is vertical symmetry. because for two positions that are vertically symmetrical, they have same feature1 and feature2 values. this is the reason why feature-based learning can learn fast. Using features to represent the state is more efficient in this specific world.
