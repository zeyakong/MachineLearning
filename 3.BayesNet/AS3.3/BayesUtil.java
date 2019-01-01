import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Zeya Kong
 * On 2017/10/22 16:01.
 */
public class BayesUtil {

    //I store all the Nodes in List called nodes
    private ArrayList<Node> nodes = new ArrayList<>();

    //This is used to store the index of node in the loadTables function.
    //Because loadTable includes more than one line, So it needs the class variable to record.
    private int currentIndex = -1;

    //I use Map to store the cpt.
    private HashMap<String , Double> cpt = new HashMap<>();

    //This is to record the number of line of cpt
    private int numberLine = -1;

    //This is the X, Query variables.
    private Node queryNode;

    //This is the e , first int is index of the node, second is the value. In boolean case, 0 means false,1 means true
    //In other case , <index , value> (it depends on the type of the node)
    //eg <1,3> means index = 1 , the value is 3.
    private HashMap<Integer,Integer> conditionMap = new HashMap<>();


    /**Just the start
     * @param filepath get the file path.
     */
    public void start(String filepath) {
        File file = new File(filepath);
        loadFile(file);
        System.out.println("\nLoading file \""+file.getName()+"\"\n");

        //Because the Scanner might be used more than once.
        //Sometimes I just create many Scanner and close().It might cause error.
        // I read some paper and it said use Scanner as a parameter is better
        Scanner input = new Scanner(System.in);

        while(true){
            startBayesNet(input);
        }
    }

    private void startBayesNet(Scanner scanner) {
        getInput(scanner);

        //Start the enumeration inference algorithm
        enumerationAsk();
    }

    //In that method, we should calculate the Bn ,which includes X,E,Y
    //X we get before and the conditionMap includes the information of E
    //Find parents of all nodes in BN, this is the way to find the Y :HIDDEN VARIABLE
    //We also should judge different type of nodes.
    private void enumerationAsk() {
        //This is the Bn, a bayes net with variables X ,E ,Y
        List<Node> BayesNet = new ArrayList<>();
        //First , get the Bn. Just find all parents of X ,E.
        //Add X,E to BayesNet
        BayesNet.add(queryNode);

        for (int i = 0 ;i<nodes.size();i++) {
            if(conditionMap.containsKey(i) && !BayesNet.contains(nodes.get(i))) BayesNet.add(nodes.get(i));
        }

        //Considering the complex of the nodes in example. I use this way to find the parents.
        //If the size of bn don't increase, it means that we found all of the parents.
        // I think it can find all the parents.
        int flag=1;
        while(flag==1){
            flag = 0;
            for(int i = 0 ;i<BayesNet.size() ; i++){
                if(BayesNet.get(i).getParents()!=null) {
                    List<Node> parents=BayesNet.get(i).getParents();
                    for (int j = 0; j < parents.size(); j++) {
                        if(!BayesNet.contains(parents.get(j))){
                            flag = 1;
                            BayesNet.add(parents.get(j));
                        }
                    }
                }
            }
        }
//        System.out.println("The bayesNet is :"+ BayesNet.toString());

        //Second,find the E
        //I the ConditionMap called c as the E.
        HashMap<Integer,Integer> c = (HashMap<Integer,Integer>)conditionMap.clone();

        //set xi to condition.ex is e extends with X = xi.
        int index=0;
        for (int i = 0; i < nodes.size(); i++) {
            if(nodes.get(i).getName().equals(queryNode.getName()))index = i;
        }

        //just TF value
        if(nodes.get(index).getNumberValue()==2){
            c.put(index,1);

            //DeepClone.
            HashMap<Integer,Integer> c1 = (HashMap<Integer,Integer>)new HashMap<>(c).clone();
            HashMap<Integer,Integer> c2 = (HashMap<Integer,Integer>)conditionMap.clone();

            List<Node> b1 = (List<Node>)new ArrayList<>(BayesNet).clone();
            List<Node> b2 = (List<Node>)new ArrayList<>(BayesNet).clone();
            double x = enumerationAll(b1,c1);
            double n = enumerationAll(b2,c2);
            DecimalFormat df = new DecimalFormat("#.###");
            System.out.println("P(T)= "+df.format(Double.parseDouble(String.format("%.3f",x/n)))+ ", P(F)= "+df.format(Double.parseDouble(String.format("%.3f",1-x/n)))+"\n");

            //clear all of the class variables and start next query.
            conditionMap.clear();
            queryNode = null;

        }else{
            //the Multiple values of node.
            //Because all mutiple value nodes have 5 value
            //So assumption: All multiple value nodes just have 5 value{1,2,3,4,5}
            //The value normalization is never changed .
            HashMap<Integer,Integer> c2 = (HashMap<Integer,Integer>)conditionMap.clone();
            List<Node> b2 = (List<Node>)new ArrayList<>(BayesNet).clone();
            double n = enumerationAll(b2,c2);//normalization

            for (int i = 1; i < 6; i++) {
                c.put(index,i);

                HashMap<Integer,Integer> c1 = (HashMap<Integer,Integer>)new HashMap<>(c).clone();


                List<Node> b1 = (List<Node>)new ArrayList<>(BayesNet).clone();

                double x = enumerationAll(b1,c1);

                DecimalFormat df = new DecimalFormat("#.###");
                if(i!=5) System.out.print("P("+i+")= "+df.format(Double.parseDouble(String.format("%.3f",x/n)))+", ");
                else System.out.println("P("+i+")= "+df.format(Double.parseDouble(String.format("%.3f",x/n)))+"\n");

                //clear all of the class variables and start next query.
                conditionMap.clear();
                queryNode = null;
            }
        }
    }

    /**
     *
     * @param bayesNet the net includes X,E,Y
     * @param c   E with some extends conditions
     * @return  a real number, the result of the calculate
     */
    private double enumerationAll(List<Node> bayesNet,HashMap<Integer,Integer> c){

        if(bayesNet.isEmpty()){
            return 1;
        }
//        System.out.println("Now enter in bn:"+toString(bayesNet)+"\nc: "+c.toString()+"\n");
        Node n = null;
        int nodeIndex = 0;

        //Get the first node
        for (int i = 0 ;i<nodes.size();i++){
            if (bayesNet.contains(nodes.get(i))){
                n = nodes.get(i);
                nodeIndex = i;
                break;
            }
        }

        if(c.containsKey(nodeIndex)){
            if(n.getParents()==null){
                bayesNet.remove(n);
                List<Node> b = (List<Node>)new ArrayList<>(bayesNet).clone();
                if(n.getNumberValue()==2){
                    if(c.get(nodeIndex)==1){
                        return n.getCpt().get("")*enumerationAll(b,c);
                    }else{
                        return (1-n.getCpt().get(""))*enumerationAll(b,c);
                    }
                }else{
                    return n.getCpt().get(c.get(nodeIndex)+"")*enumerationAll(b,c);
                }
            }else{
                List<Node> parents = n.getParents();
                String cptKey = "";
                for (int i = 0; i < parents.size(); i++) {
                    cptKey += c.get(findIndexByName(parents.get(i)));
                }
                bayesNet.remove(n);
                List<Node> b = (List<Node>)new ArrayList<>(bayesNet).clone();
                if(n.getNumberValue()==2){
                    if(c.get(nodeIndex)==1){
                        return n.getCpt().get(cptKey)*enumerationAll(b,c);
                    }else{
                        return (1-n.getCpt().get(cptKey))*enumerationAll(b,c);
                    }
                }else{
                    return n.getCpt().get(cptKey+c.get(nodeIndex))*enumerationAll(b,c);
                }
            }
        }


        else{
            if(n.getParents()==null){
                bayesNet.remove(n);

                if(n.getNumberValue()==2){
                    HashMap<Integer,Integer> c1 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c2 = (HashMap<Integer,Integer>)c.clone();
                    c1.put(findIndexByName(n),1);
                    c2.put(findIndexByName(n),0);

                    List<Node> b1 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b2 = (List<Node>)new ArrayList<>(bayesNet).clone();

                    return n.getCpt().get("")*enumerationAll(b1,c1)+
                            (1-n.getCpt().get(""))*enumerationAll(b2,c2);
                }else{
                    //Must do deepCopy.
                    HashMap<Integer,Integer> c1 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c2 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c3 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c4 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c5 = (HashMap<Integer,Integer>)c.clone();
                    c1.put(findIndexByName(n),1);
                    c2.put(findIndexByName(n),2);
                    c3.put(findIndexByName(n),3);
                    c4.put(findIndexByName(n),4);
                    c5.put(findIndexByName(n),5);

                    List<Node> b1 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b2 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b3 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b4 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b5 = (List<Node>)new ArrayList<>(bayesNet).clone();

                    return  n.getCpt().get(1+"")*enumerationAll(b1,c1)+
                            n.getCpt().get(2+"")*enumerationAll(b2,c2)+
                            n.getCpt().get(3+"")*enumerationAll(b3,c3)+
                            n.getCpt().get(4+"")*enumerationAll(b4,c4)+
                            n.getCpt().get(5+"")*enumerationAll(b5,c5);
                }

            }else{
                List<Node> parents = n.getParents();
                String cptKey = "";
                for (int i = 0; i < parents.size(); i++) {
                    cptKey += c.get(findIndexByName(parents.get(i)));
                }

                bayesNet.remove(n);
                if(n.getNumberValue()==2){
                    HashMap<Integer,Integer> c1 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c2 = (HashMap<Integer,Integer>)c.clone();
                    c1.put(findIndexByName(n),1);
                    c2.put(findIndexByName(n),0);

                    List<Node> b1 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b2 = (List<Node>)new ArrayList<>(bayesNet).clone();
//                System.out.println("c1 : "+c1.toString()+"\nc2 : "+c2.toString());
//                System.out.println("Now do (have parents) :n.getCpt().get(cptKey)= "+n.getCpt().get(cptKey)+"*enumerationAll(bayesNet,c1)+");
                    return n.getCpt().get(cptKey)*enumerationAll(b1,c1)+
                            (1-n.getCpt().get(cptKey))*enumerationAll(b2,c2);
                }else{
                    //Must do deepCopy.
                    HashMap<Integer,Integer> c1 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c2 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c3 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c4 = (HashMap<Integer,Integer>)c.clone();
                    HashMap<Integer,Integer> c5 = (HashMap<Integer,Integer>)c.clone();
                    c1.put(findIndexByName(n),1);
                    c2.put(findIndexByName(n),2);
                    c3.put(findIndexByName(n),3);
                    c4.put(findIndexByName(n),4);
                    c5.put(findIndexByName(n),5);

                    List<Node> b1 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b2 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b3 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b4 = (List<Node>)new ArrayList<>(bayesNet).clone();
                    List<Node> b5 = (List<Node>)new ArrayList<>(bayesNet).clone();

                    return n.getCpt().get(cptKey+1)*enumerationAll(b1,c1)+
                            n.getCpt().get(cptKey+2)*enumerationAll(b2,c2)+
                            n.getCpt().get(cptKey+3)*enumerationAll(b3,c3)+
                            n.getCpt().get(cptKey+4)*enumerationAll(b4,c4)+
                            n.getCpt().get(cptKey+5)*enumerationAll(b5,c5);

                }
            }
        }
    }

    private int findIndexByName(Node n){
        for (int i = 0; i < nodes.size(); i++) {
            if(nodes.get(i).getName().equals(n.getName())){
                return  i ;
            }
        }
        return -1;
    }

    private static <T> List<T> deepCopy(List<T> src) {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(byteOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.writeObject(src);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(byteIn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        @SuppressWarnings("unchecked")
            List<T> dest = null;
        try {
            dest = (List<T>) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return dest;

    }

    private String toString(List<Node> s){
        String str = "";
        for (int i = 0; i < s.size(); i++) {
            str +=s.get(i).getName()+ " ";
        }
        return str;
    }

    /**
     *  @param input get the Scanner because the user may search more than once.
     */
    private void getInput(Scanner input) {
        String str = input.nextLine();
        str.trim();
        if (str.equals("quit")){
            System.exit(0);
        }

        if(str.contains("|")){
            String[] temp = str.split("\\|");

            //Assume there is only one query variable.
            //Delete the space.
            String query = temp[0].trim();

            //Find the query variable node index by name.
            for (int i = 0; i <nodes.size() ; i++) {
                if(nodes.get(i).getName().equals(query)){
                    queryNode = nodes.get(i);
                    break;
                }
            }

            if(temp[1].contains(",")) {
                String[] conditionStr = temp[1].split(",");
                for (int i = 0; i <conditionStr.length ; i++) {
                    setNodeCondition(conditionStr[i].trim());
                }
            }else//just one condition
            {
                setNodeCondition(temp[1].trim());

            }
        }else{
            //Find the query variable node index by name.
            for (int i = 0; i <nodes.size() ; i++) {
                if(nodes.get(i).getName().equals(str.trim())){
                    queryNode = nodes.get(i);
                    break;
                }
            }
        }

    }

    private void setNodeCondition(String string) {
        String[] str = string.split("=");
        for (int i = 0; i < nodes.size(); i++) {
            if(nodes.get(i).getName().equals(str[0].trim())){
                //0means false , 1 means true
                if(str[1].trim().equals("T")){
                    conditionMap.put(i,1);
                }else if (str[1].trim().equals("F")){
                    conditionMap.put(i,0);
                }else{
                    //multiple value of variables
                    conditionMap.put(i,Integer.parseInt(str[1].trim()));
                }
            }
        }
    }

    /**
     * Loading the file and store it in bayes net.
     *
     * @param file  the input file
     */
    private void loadFile(File file) {
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
            int flag = 0;
            //it was used to identify the data type.
            //0 means variable area ,1 means parent area, 2means cpt tables.
            line = br.readLine();
            while (line != null) {
                line.trim();
                if (line.equals("# Parents")) flag = 1;
                if (line.equals("# Tables")) flag = 2;
                if (flag == 0) {
                    loadVariable(line);
                } else if (flag == 1) {
                    loadParents(line);
                } else if (flag == 2) {
                    loadTables(line);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("The file can not be recognized!");
        }
    }

    private void loadTables(String line) {
        if (line.startsWith("#")){
            return;
        }

        for (int i = 0; i < nodes.size(); i++) {
            if(nodes.get(i).getName().equals(line)){
                currentIndex = i;
                if(nodes.get(i).getParents()==null)numberLine = 1;
                else {
                    int result  =1;
                    List<Node> parents = nodes.get(i).getParents();
                    for(int j =0 ;j<parents.size() ;j++){
                        result = result*parents.get(j).getNumberValue();
                    }
                    numberLine = result;
                }
                return;
            }
        }

        if(nodes.get(currentIndex).getNumberValue()==2){
            String[] str = line.split(" ");
//        System.out.println("numberline is "+numberLine+"cpt add"+str[str.length-1]+"for"+currentIndex);
            String cptKey ="";
            for (int i = 0; i < str.length-1; i++) {
                if(str[i].trim().equals("T")){
                    cptKey +="1";
                }else if(str[i].trim().equals("F")){
                    cptKey +="0";
                }
            }
            cpt.put(cptKey,Double.parseDouble(str[str.length-1].trim()));
            numberLine =numberLine-1;
            if(numberLine==0){
                HashMap<String , Double> copy = (HashMap<String , Double>)cpt.clone();
                nodes.get(currentIndex).setCpt(copy);
                cpt.clear();
            }
        }else{
            if(nodes.get(currentIndex).getParents()==null){
                String[] str = line.split(" ");
                double sum =0;
                for (int i = 0; i < str.length; i++) {
                    cpt.put(i+1+"",Double.parseDouble(str[i].trim()));
                    sum =sum +Double.parseDouble(str[i].trim());
                }
                cpt.put(str.length+1+"",1-sum);
                HashMap<String , Double> copy = (HashMap<String , Double>)cpt.clone();
                nodes.get(currentIndex).setCpt(copy);
                cpt.clear();
            }else{
                String[] str = line.split(" ");
                String cptKey ="";
                if(str[0].trim().equals("T")){
                    cptKey +="1";
                }else if(str[0].trim().equals("F")){
                    cptKey +="0";
                }
                cptKey +=str[1]+str[2];
                double sum = 0;
                for(int i = 1 ;i<5;i++){
                    cpt.put(cptKey+i,Double.parseDouble(str[i+2].trim()));
                    sum =sum +Double.parseDouble(str[i+2].trim());
                }
                cpt.put(cptKey+5,1-sum);
                numberLine =numberLine-1;
                if(numberLine==0){
                    HashMap<String , Double> copy = (HashMap<String , Double>)cpt.clone();
                    nodes.get(currentIndex).setCpt(copy);
                    cpt.clear();
                }
            }
        }
    }

    private void loadParents(String line) {
        //just ignore the tag.
        if (line.startsWith("#")){
            return;
        }

        String[] str = line.split(" ");
        int nodeIndex = 0;
        ArrayList<Node> parents = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            //Find the current node by name.
            if(nodes.get(i).getName().equals(str[0])){
                nodeIndex = i;
            }
        }
        for (int i = 1; i < str.length; i++) {
            for (int j = 0; j < nodes.size(); j++) {
                //Find the parents node by name.
                if(nodes.get(j).getName().equals(str[i])){
//                    System.out.println("the "+nodes.get(nodeIndex).getName()+" have a parent:" +nodes.get(j).getName());
                    parents.add(nodes.get(j));
                }
            }
        }
        nodes.get(nodeIndex).setParents(parents);
    }

    private void loadVariable(String line) {
        line.trim();
        if (line.startsWith("#")){
            return;
        }
        String[] str = line.split(" ");
        Node node = new Node();
        node.setName(str[0].trim());
        node.setNumberValue(str.length-1);
        nodes.add(node);
    }
}
