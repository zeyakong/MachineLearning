import java.util.Random;

/**
 * Created by Zeya Kong
         * On 2017/11/26 21:36.
         */
public class Start {
    public static void main(String[] args) {
        LearnUtil learnUtil = new LearnUtil(10,10);
        learnUtil.readFile("iceWorld.txt");
        learnUtil.initializeState();
        learnUtil.initializeReward();
        System.out.println("\n----------QLearning----------\n");
        learnUtil.doQLearning();
        System.out.println("\n----------SARSA----------\n");
        learnUtil.doSARSA();

    }
}
