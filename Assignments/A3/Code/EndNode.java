package Code;

public class EndNode extends Node {

    private boolean outcome;

    public EndNode(String attribute, boolean outcome, int depth) {
        super(attribute);
        this.outcome = outcome;
        this.depth = depth;
    }

    public EndNode(Node nodeToReplace) {
        super(nodeToReplace);
        this.outcome = ((EndNode) nodeToReplace).getOutcome();
    }

    public boolean getOutcome() {
        return outcome;
    }

    @Override
    public boolean isEndNode() {
        return true;
    }

    @Override
    public Node copy() {
        return new EndNode(this.getAttribute(), this.getOutcome(), this.getDepth());
    }

    @Override
    public void print() {
        System.out.println("-".repeat(depth * 2) + (outcome ? "(1) Recurrence" : "(0) No Recurrence"));
    }

}
