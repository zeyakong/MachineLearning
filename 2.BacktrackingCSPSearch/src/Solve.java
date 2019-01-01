import java.io.File;

public class Solve {
    public static void main(String[] args) {
        CSP csp = new CSP();
//        String puzzlePath = "E:\\WorkFolder\\WorkSpace\\IDEA\\JavaSE\\AS2\\Examples\\inputData\\xword02.txt";
//        String dicPath = "E:\\WorkFolder\\WorkSpace\\IDEA\\JavaSE\\AS2\\Examples\\inputData\\dictionary_medium.txt";
        String puzzlePath = args[0];
        String dicPath = args[1];
        csp.start(dicPath,puzzlePath);
    }
}
