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
        System.out.print(
                "|");
        for (int i = 0; i < depth; i++) {
            System.out.print("\u001B[3" + (i + 3) + "m" + "│" + "\u001B[0m");
        }
        System.out.println((depth + 1) + "-".repeat(depth * 4)
                + (outcome ? "\u001B[1m(1) Recurrence\u001B[0m" : "\u001B[2m(0) No Recurrence\u001B[0m"));
    }

}
