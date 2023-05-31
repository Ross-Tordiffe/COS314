package Code;

public class EndNode extends Node {

    private boolean outcome;

    public EndNode(String attribute, boolean outcome, int depth) {
        super(attribute);
        this.outcome = outcome;
        this.depth = depth;
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
        System.out.println(depth + "-".repeat(depth * 4) + (outcome ? "(1) Recurrence" : "(0) No Recurrence"));
    }

}
