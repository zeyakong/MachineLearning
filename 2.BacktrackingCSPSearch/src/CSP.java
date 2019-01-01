
import java.io.*;
import java.util.*;

public class CSP {
    private List<String> dictionary =new ArrayList<String>();
    private int[][] puzzle;
    private char[][] draw;
    private int[][] constraints;
    private List<Word> words = new ArrayList<Word>();
    private int trackNumber = 0;
    private Set<Word> assignSet = new HashSet<Word>();


    //Load dictionary
    public void loadDic(File file)
    {
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("The file path can not be found!");
            System.exit(0);
        }
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        try {
//            System.out.print("Reading city data...");
            line = br.readLine();
            while (line != null) {
                dictionary.add(line);
                line = br.readLine();
            }
//            System.out.println("Done.\n");
        } catch (IOException e) {
            System.out.println("The file can not be recognized!");
            return;
        } finally {
            // Close all the stream resources
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    System.out.println("Stream close error!");
                }
            if (isr != null)
                try {
                    isr.close();
                } catch (IOException e) {
                    System.out.println("Stream close error!");
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Stream close error!");
                }
        }
    }

    /**
     * use int[][]array to store the puzzle.
     * 0 means _
     * -1 means X
     * Integer more than 1 means position
     * @param file
     */
    public void loadPuzzle(File file){
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("The file path can not be found!");
            System.exit(0);
        }
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        try {
            line = br.readLine();
            //get size of puzzle
            String[] str= line.split(" ");
            int across=0;
            int down=0;
            for(int i = 0 ;i<str.length;i++){
                if(!str[0].equals("")){
                    across = Integer.parseInt(str[0]);
                    down = Integer.parseInt(str[1]);
                }else{
                    across = Integer.parseInt(str[1]);
                    down = Integer.parseInt(str[2]);
                }
            }
            puzzle = new int[across][down];
            /*
            * now we finish initials the puzzle
            * */

            //get value.
            String[] valuetemp;
            int indexAcross = 0;
            int indexDown= 0;
            line = br.readLine();
            while (line !=null) {

                valuetemp = line.split(" ");
                for(int i = 0 ;i<valuetemp.length;i++){
                    if(valuetemp[i].equals("")){
                        continue;
                    }
                    else if(isNumeric(valuetemp[i])){
                        puzzle[indexAcross][indexDown] = Integer.parseInt(valuetemp[i]);
                        indexDown++;
                    }else if(valuetemp[i].equals("_")){
                        puzzle[indexAcross][indexDown] = 0;
                        indexDown++;
                    }else if(valuetemp[i].equals("X")){
                        puzzle[indexAcross][indexDown] = -1;
                        indexDown++;
                    }
                }
//                System.out.println("finish this line="+indexAcross);
                indexAcross++;
                indexDown=0;
                line = br.readLine();
            }

        } catch (IOException e) {
            System.out.println("The file can not be recognized!");
            return;
        } finally {
            // Close all the stream resources
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    System.out.println("Stream close error!");
                }
            if (isr != null)
                try {
                    isr.close();
                } catch (IOException e) {
                    System.out.println("Stream close error!");
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("Stream close error!");
                }
        }
    }


    public static boolean isNumeric(String str) {
        if (str != null && !"".equals(str.trim())) {
            return str.matches("^[0-9]*$");
        }
        return false;
    }


    public void printDic() {
        for(Iterator i = dictionary.iterator(); i.hasNext();) {//比较常规的for写法
            System.out.println(i.next());
        }
    }

    public void printPuzzle(){
        for(int i =0;i<puzzle.length;i++){
            for(int j = 0 ;j<puzzle[i].length;j++){
                System.out.printf("%3s",puzzle[i][j]);
            }
            System.out.println();
        }
    }

    public void printDraw(){
        for(int i =0;i<draw.length;i++){
            for(int j = 0 ;j<draw[i].length;j++){
                System.out.printf("%c",draw[i][j]);
            }
            System.out.println();
        }
    }


    public void initWords(){
        for(int i =0;i<puzzle.length;i++){
            for(int j = 0 ;j<puzzle[i].length;j++){
                if(puzzle[i][j]>0){
                    //means across start
                    if(j-1<0 || puzzle[i][j-1]<0){
                        //find the destination
                        for(int k = j+1;k<puzzle[i].length;k++){
                            if(puzzle[i][k]<0)
                            {
                                if(k==j+1) break;
                                Word word = new Word();
                                word.setStartX(i);
                                word.setStartY(j);
                                word.setEndX(i);
                                word.setEndY(k);
                                word.setFlag(puzzle[i][j]+"-across");
                                words.add(word);
                                break;
                            }
                            if(k==puzzle[i].length-1){
                                Word word = new Word();
                                word.setStartX(i);
                                word.setStartY(j);
                                word.setEndX(i);
                                word.setEndY(k);
                                word.setFlag(puzzle[i][j]+"-across");
                                words.add(word);
                            }
                        }
                    }
                }
                if(puzzle[i][j]>0){
                    //means down start
                    if(i-1<0 || puzzle[i-1][j]<0){
                        //find the destination
                        for(int k = i+1;k<puzzle.length;k++){
                            if(puzzle[k][j]<0)
                            {
                                if(k==i+1)break;
                                Word word = new Word();
                                word.setStartX(i);
                                word.setStartY(j);
                                word.setEndX(k);
                                word.setEndY(j);
                                word.setFlag(puzzle[i][j]+"-down");
                                words.add(word);
                                break;
                            }
                            if(k==puzzle.length-1){
                                Word word = new Word();
                                word.setStartX(i);
                                word.setStartY(j);
                                word.setEndX(k);
                                word.setEndY(j);
                                word.setFlag(puzzle[i][j]+"-down");
                                words.add(word);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("\n"+words.size()+" words");
    }

    public void initDomain(){
        System.out.println("\nInitial assignment and domain sizes:");
        for(int i = 0 ;i<words.size();i++){
            System.out.println(words.get(i).getFlag()+" = NO_VALUE ("+getDomainSize(words.get(i))+" values possible)");
        }
    }

    public void initDraw(){
        draw = new char[puzzle.length][puzzle[0].length];
        for(int i =0;i<draw.length;i++){
            for(int j = 0 ;j<draw[i].length;j++){
                draw[i][j]=' ';
            }
        }
    }


    public void initConstrains(){
        constraints = new int[puzzle.length][puzzle[0].length];
        int result=0;
        for(int i =0 ;i<puzzle.length ;i++){
            for(int j=0;j<puzzle[i].length;j++){
                if(puzzle[i][j]>=0){
                   if((getTop(i,j)||getBottom(i ,j))&&
                           (getLeft(i , j)||getRight(i , j))) {
                       constraints[i][j] = 1;
                       result++;
                   }
                }
            }
        }
        System.out.println(result+" constraints");
    }

    private boolean getTop(int i , int j){
        if(i-1<0)return  false;
        else if(puzzle[i-1][j]>=0) return true;
        return false;
    }

    private boolean getBottom(int i , int j){
        if(i+1>puzzle.length-1)return  false;
        else if(puzzle[i+1][j]>=0) return true;
        return false;
    }

    private boolean getLeft(int i , int j){
        if(j-1<0)return false;
        else if(puzzle[i][j-1]>=0)return true;
        return false;
    }

    private boolean getRight(int i , int j){
        if(j+1>puzzle[i].length-1)return false;
        else if(puzzle[i][j+1]>=0) return true;
        return false;
    }

    public int getDomainSize(Word word){
        int result = 0;
        List<String> domain = new ArrayList<>();
        for(Iterator i = dictionary.iterator();i.hasNext();){
            String str = (String) i.next();
            if(str.length()==word.getLength()){
                domain.add(str);
                result++;
            }
        }
        word.setDomain(domain);
        return result;
    }

    public void printConstrains(){
        for(int i = 0 ; i<constraints.length; i++){
            for (int j = 0 ; j<constraints[i].length;j++){
                System.out.print(constraints[i][j]+" ");
            }
            System.out.println();
        }
    }

    public void backTrackingSearch(){
        Set<Word> s= new HashSet<>();
        if(backTrackOpt1(0)){
            System.out.println("\nSUCCESS! Solution found after "+trackNumber+" recursive calls to search.\n");
            printDraw();
        }else{
            System.out.println("Track Number :"+trackNumber);
            System.out.println("cant find it!");
        }
    }

    /** optimize the backTrack.
     *  1. user order to find word
     *  2. user order to match the dictionary instead of scanning of the array.
     *  set means the choose set of word .
     */
    public boolean backTrackOpt1(int n){
        if(n==words.size()){
            return true;
        }
        //select the minimum domain size and the maximum constrains
        int index = selectVariable(assignSet);
        for(int i = 0 ;i<words.get(index).getDomain().size(); i++){
            if( isFit (words.get(index).getDomain().get(i),n)){
                char[][] temp = getPerDraw();
                putWordToDraw(words.get(index).getDomain().get(i),n);
                assignSet.add(words.get(index));
                if(backTrackOpt1(n+1))
                {
                    return true;
                }
                reduction(temp);
                assignSet.remove(words.get(index));
            }
        }
        trackNumber++;
        return false;
    }

    //in order to reduction
    private char[][] getPerDraw() {
        char[][] temp1 = new char[draw.length][draw[0].length];
        for(int i = 0 ;i<draw.length;i++){
            for(int j = 0 ;j<draw[i].length; j++){
                temp1[i][j]=draw[i][j];
            }
        }
        return  temp1;
    }

    //just reduce the words in draw
    private void reduction(char[][] temp) {
        for(int i = 0 ;i<temp.length;i++){
            for(int j = 0 ;j<temp[i].length; j++){
                draw[i][j]=temp[i][j];
            }
        }
    }


    private void putWordToDraw(String s, int n) {
        int x = words.get(n).getStartX();
        int y = words.get(n).getStartY();
        for(int i = 0 ;i<s.length(); i++) {
            //across
            if (words.get(n).getDirection() == 1) {
                draw[x][y] = s.charAt(i);
                y++;
            } else {
                draw[x][y] = s.charAt(i);
                x++;
            }
        }
    }

    public boolean isFit(String str,int n){
        if(str.length()!=words.get(n).getLength())return false;//not possible
        int x = words.get(n).getStartX();
        int y = words.get(n).getStartY();
        for(int i = 0 ;i<str.length(); i++){
            //across
            if(words.get(n).getDirection()==1){
//                if(y==str.length()-1) return true;
                if(draw[x][y]==' '){
                    y++;
                    continue;
                }else{
                    if(draw[x][y]==str.charAt(i)){
                        y++;
                        continue;
                    }else{
                        return false;
                    }
                }
            }else if(words.get(n).getDirection()==2){
//                if(y==str.length()-1) return true;
                if(draw[x][y]==' '){
                    x++;
                    continue;
                }
                else if(draw[x][y]==str.charAt(i)){
                    x++;
                    continue;
                }else{
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param assigned set
     * @return index of word
     */
    public int selectVariable(Set assigned){
        int orderIndex = 0;
        //find min domain size
        int minDomainSize = 100;
        for(int i = 0 ;i<words.size(); i++){
            if(words.get(i).getDomainSize()<minDomainSize &&
                    !assigned.contains(i))minDomainSize = words.get(i).getDomainSize();
        }

        List<Integer> index1 = new ArrayList<>();
        //find all variables' index in minimum domain area.
        for(int i = 0 ;i<words.size(); i++){
            if(words.get(i).getDomainSize()==minDomainSize &&!assigned.contains(i)){
                index1.add(i);
            }
        }

        //find the maximum constrain size in index.
        int result =0;
        for(int i = 0 ; i<index1.size()  ; i++){
            int indexConstrain = words.get(i).getConstrainSize();
            if(result <indexConstrain){
                result = indexConstrain;
                orderIndex = i;
            }
        }
        return  orderIndex;
    }

    public void  initWordConstraint(){
        for(int i = 0 ;i<words.size();i++){
            words.get(i).setConstrainSize(findConstrainByIndex(i));
        }
    }

    private int findConstrainByIndex(int i){
        int result = 0;
        //across
        if(words.get(i).getDirection()==1){
            for(int j = words.get(i).getStartY() ;j<words.get(i).getEndY()+1; j++){
                if(constraints[words.get(i).getStartX()][j]==1)result++;
            }
        }else if (words.get(i).getDirection()==2){
            for(int k = words.get(i).getStartX() ;k<words.get(i).getEndX()+1; k++){
                if(constraints[k][words.get(i).getStartY()]==1)result++;
            }
        }
        return result;
    }


    public void start(String dicPath,String puzzlePath){
        loadDic(new File(dicPath));
        loadPuzzle(new File(puzzlePath));

        initWords();
        initConstrains();
        initDomain();
        initDraw();
        initWordConstraint();

        backTrackingSearch();


//        System.out.println(isFit("ANOTHER",0));
//        char[][] temp = getPerDraw();
//        System.out.println("the temp draw:");
//        for(int i = 0 ;i<temp.length;i++){
//            for(int j = 0 ;j<temp[i].length; j++){
//                System.out.printf("%3c",temp[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println("\nnow put string into draw ");
//        putWordToDraw("ANOTHER",0);
//        System.out.println("Print Draw");
//        printDraw();
//        System.out.println("Now Reduction");
//        reduction(temp);
//        System.out.println("Print draw again");
//        printDraw();

    }

//    public void initConstraints(){
//        System.out.println(words.get(2).getStartY());
//        for(int i = 0;i<words.size()-1;i++){
//            for(int j = i+1;j<words.size() ;j++){
//                //the sum can find two vertical lines,
//                if(words.get(i).getDirection()+words.get(j).getDirection()==3){
//                    if( isCrosse(words.get(i).getStartX(),words.get(i).getStartY(), words.get(i).getEndX(), words.get(i).getEndY(),
//                            words.get(j).getStartX(),  words.get(j).getStartY(), words.get(j).getEndX() ,  words.get(j).getEndY()) ){
//                        if(words.get(i).getDirection()==1){
//                            System.out.println("now we found word["+i+"] cross word["+j+"] position is"+words.get(i).getStartX()+","+words.get(j).getStartY());
//                            constrain[words.get(i).getStartX()][words.get(j).getEndY()] = 1;
//                        }else{
//                            System.out.println("now we found word["+i+"] cross word["+j+"] position is"+words.get(j).getStartX()+","+words.get(i).getStartY());
//                            constrain[words.get(j).getStartX()][words.get(i).getEndY()] = 1;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    public void testWord(){
//        System.out.println("word number : "+words.size());
//        for(int i = 0 ; i<words.size(); i++) {
//            System.out.println("The "+i+" is from ("+words.get(i).getStartX()+","+words.get(i).getStartY()+") to ("+words.get(i).getEndX()+","+words.get(i).getEndY()+") and length is"+words.get(i).getLength());
//        }
//    }
//
//
//    /**
//     * we know the two lines start and end position, how to know whether they cross?
//     * I use math knowledge to find the answer.
//     * @param sx1 line 1 start x
//     * @param sy1 line 1 start y
//     * @param ex1 line 1 end x
//     * @param ey1 line 1 end y
//     * @param sx2 line 2 start x
//     * @param sy2 .......
//     * @param ex2
//     * @param ey2
//     * @return
//     */
//    private boolean isCrosse(int sx1, int sy1 , int ex1, int ey1, int sx2,int sy2,int ex2, int ey2){
//        /*assumptions:across line to find down line.
//        fact:1.Parallel does not intersect
//             2. two lines are vertical
//        */
//        if(sx1<=ex2 && ey2>=sy1 && sy2<=ey1 && ex1>=sx2)
//            return true;
//        return false;
//    }
//
//    public int getConstrainsNum(){
//        int result=0;
//        for(int i=0;i<constrain.length;i++){
//            for(int j = 0 ;j<constrain[i].length; j++){
//                System.out.print(constrain[i][j]);
//                if(constrain[i][j]==1)
//                result++;
//            }
//            System.out.println();
//        }
//        return result;
//    }
}
