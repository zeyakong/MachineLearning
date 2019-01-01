import java.util.List;

public class Word {

    private String flag;
    private int startX;
    private int startY;

    private int endX;
    private int endY;

    private List<String> domain;
    private int constrainSize;
//    private Map<>

    public int getConstrainSize() {
        return constrainSize;
    }

    public void setConstrainSize(int constrainSize) {
        this.constrainSize = constrainSize;
    }

    public List<String> getDomain() {
        return domain;
    }

    public void setDomain(List<String> domain) {
        this.domain = domain;
    }
    public int getDomainSize(){
        return domain.size();
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getLength(){
        if(startX==endX)return endY-startY+1;
        else return endX-startX+1;
    }

    /**
     *
     * @return 1means across ; 2means down
     */
    public int getDirection(){
        if(startX==endX) return 1;
        else return 2;
    }
}
