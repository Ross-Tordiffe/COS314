package Code;

import java.util.ArrayList;

public class ChanceNode extends Node {

    private ArrayList<Node> children;
    private String[] values;

    // ===== Constructor =====
    // =======================
    public ChanceNode(String attribute, int index, ArrayList<Node> children, String[] values, int depth) {
        super(attribute);
        this.children = children;
        this.values = values;
        this.index = index;
        this.depth = depth;
        for (Node child : children) {
            child.setParent(this);
        }
    }

    public ChanceNode(Node nodeToReplace) {
        super(nodeToReplace);
        this.values = ((ChanceNode) nodeToReplace).getValues();
        this.children = ((ChanceNode) nodeToReplace).getChildren();
        for (Node child : children) {
            child.setParent(this);
        }
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    @Override
    public boolean isEndNode() {
        return false;
    }

    public String[] getValues() {
        return values;
    }

    public Node getChild(String value) {
        int index = 0;
        for (String v : this.values) {
            if (v.equals(value)) {
                return this.children.get(index);
            }
            index++;
        }
        return null;
    }

    @Override
    public Node copy() {
        ArrayList<Node> newChildren = new ArrayList<Node>();
        for (Node child : children) {
            newChildren.add(child.copy());
        }
        return new ChanceNode(this.getAttribute(), index, newChildren, values, depth);
    }

    @Override
    public void print() {
        System.out.println(
                "=".repeat((depth * 2) + 1) + "[" + this.children.size() + "]" + this.getAttribute() + " - > " + this);
        for (Node child : children) {
            child.print();
        }
    }

}
