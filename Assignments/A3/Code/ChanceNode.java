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

    public void replaceChild(int index, Node child) {
        this.children.set(index, child);
        child.setParent(this);
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
        System.out.print(
                "|");
        for (int i = 0; i < depth; i++) {
            System.out.print("\u001B[3" + (i + 3) + "m" + "│" + "\u001B[0m");
        }
        System.out.println(
                (depth + 1) + "=".repeat((depth * 4) + 1) + "[" + this.children.size() + "]" + this.getAttribute());
        for (Node child : children) {
            child.print();
        }
    }

}
