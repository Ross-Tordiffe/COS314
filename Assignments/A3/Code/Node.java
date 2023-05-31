package Code;

public abstract class Node implements Cloneable {

    private String attribute;
    protected int index = 0;
    protected int depth = 0;
    private Node parent = null;

    public Node(String attribute) {
        this.attribute = attribute;
    }

    public boolean isEndNode() {
        return false;
    }

    public String getAttribute() {
        return attribute;
    }

    public int getIndex() {
        return index;
    }

    public int getDepth() {
        return depth;
    }

    public Node getParent() {
        return parent;
    }

    public void setDepth(int newDepth) {
        this.depth = newDepth;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node copy() {
        return null;
    }

    public void print() {
        System.out.println("Node: " + attribute);
    }

}
