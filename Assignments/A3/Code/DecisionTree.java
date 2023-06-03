package Code;

import java.util.ArrayList;
import java.util.Random;

public class DecisionTree extends Helper {

    // ===== Variables =====
    // =====================

    // Tree
    private final Node root;
    private final ArrayList<AttributeInformation> attributeList;
    private double fitness = 0.0;
    private ArrayList<Node> allNodes = new ArrayList<Node>();

    // Random object
    private final Random seededRandom;

    // Parameters
    private final int maxDepth;
    private final boolean full;
    private final double terminalProbability = 0.1;

    // Tracking variables
    private int[][] confusionMatrix = new int[2][2];
    private int correct = 0;

    // ===== CONSTRUCTORS =====
    // =======================
    // Default constructor
    public DecisionTree(int maxDepth, Random seededRandom, boolean full) {
        this.maxDepth = maxDepth;
        this.seededRandom = seededRandom;
        this.full = full;
        this.attributeList = getAttributeList();
        this.root = buildTree(new ArrayList<AttributeInformation>(attributeList), 0);
        addToAllNodesList(root);
    }

    // Constructor for cloning
    public DecisionTree(Node root, int maxDepth, Random seededRandom, boolean full) {
        this.maxDepth = maxDepth;
        this.seededRandom = seededRandom;
        this.full = full;
        this.attributeList = getAttributeList();
        this.root = root;
        addToAllNodesList(root);
    }

    // Subtree constructor
    public DecisionTree(int mutationDepth, Random seededRandom, boolean full,
            ArrayList<AttributeInformation> attributeList, int startDepth) {
        this.maxDepth = startDepth + mutationDepth;
        this.seededRandom = seededRandom;
        this.full = full;
        this.attributeList = attributeList;
        this.root = buildTree(new ArrayList<AttributeInformation>(attributeList), startDepth);
        // Not necessary to add to allNodes of a subtree
    }

    // ===== Getters =====
    // ===================

    public Node getRoot() {
        return root;
    }

    public double getFitness() {
        return fitness;
    }

    public int getCorrect() {
        return correct;
    }

    public boolean isFull() {
        return full;
    }

    public int[][] getConfusionMatrix() {
        return confusionMatrix;
    }

    public double getBinaryPrecision() {
        return (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[1][0]);
    }

    public double getRecall() {
        return (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[0][1]);
    }

    public double getFMeasure() {
        double precision = getBinaryPrecision();
        double recall = getRecall();
        return 2 * ((precision * recall) / (precision + recall));
    }

    public Node getRandomNode() {

        // Get a node and its depth
        int randomIndex = seededRandom.nextInt(allNodes.size() - 1) + 1; // Don't want to return the root
        int depth = allNodes.get(randomIndex).getDepth(); // Get the depth of the node

        // Traverse the tree randomly until the random depth is reached
        // copy reference to root node
        depth = 1;
        Node node = root;
        while (depth > 0) {
            if (node instanceof ChanceNode) {
                ChanceNode chanceNode = (ChanceNode) node;
                node = chanceNode.getChildren().get(seededRandom.nextInt(chanceNode.getChildren().size()));
            } else {
                break;
            }
            depth--;
        }
        return node;
    }

    // ===== Clone Method =====
    // ========================
    public DecisionTree copy() {
        return new DecisionTree(root.copy(), maxDepth, seededRandom, full);
    }

    // ===== Tree Build Methods =====
    // ==============================
    private Node buildTree(ArrayList<AttributeInformation> availableAttributes, int depth) {

        // If max depth is reached OR it is a grow tree, the depth is greater than 0 and
        // a random number is less than the terminal probability
        boolean terminal = (depth >= maxDepth)
                || (!this.full) && (depth > 0) && (seededRandom.nextDouble() < terminalProbability);

        if (terminal) {
            return new EndNode("Class", seededRandom.nextBoolean(), depth); // Create a terminal node
        } else if (availableAttributes.size() > 0) {
            return createChanceNode(availableAttributes, depth); // Create a chance node
        } else {
            System.out.println("Error: No attributes left to choose from. Check maxDepth < number of attributes"); // This
                                                                                                                   // should
                                                                                                                   // never
                                                                                                                   // happen
            return null;
        }
    }

    private ChanceNode createChanceNode(ArrayList<AttributeInformation> availableAttributes, int depth) {
        // Choose a random attribute and remove it from the list
        int attributeIndex = seededRandom.nextInt(availableAttributes.size());
        AttributeInformation attribute = availableAttributes.get(attributeIndex);
        availableAttributes.remove(attributeIndex);

        // Add a child for each value of the attribute.
        // Give each child the same copy of the available attributes
        ArrayList<Node> children = new ArrayList<Node>();
        for (String value : attribute.getValues()) {
            children.add(buildTree(new ArrayList<AttributeInformation>(availableAttributes), depth + 1));
        }
        // Create the node with the children that were just created.
        return new ChanceNode(attribute.getName(), attribute.getIndex(), children, attribute.getValues(), depth);
    }

    // ===== Genetic Operations =====
    // ==============================

    public void crossover(DecisionTree otherTree, int crossDepth) {
        Node thisNode = getRandomNode();
        Node otherNode = otherTree.getRandomNode();

        ChanceNode thisParent = (ChanceNode) thisNode.getParent();
        ChanceNode otherParent = (ChanceNode) otherNode.getParent();

        int thisIndex = thisParent.getChildren().indexOf(thisNode);
        int otherIndex = otherParent.getChildren().indexOf(otherNode);

        // Swap the nodes
        Node temp = thisNode;
        thisNode = otherNode;
        otherNode = temp;

        // Update the allNodes lists for both trees
        removeFromAllNodesList(thisNode);
        addToAllNodesList(otherNode);
        otherTree.removeFromAllNodesList(otherNode);
        otherTree.addToAllNodesList(thisNode);

        int thisDepth = thisNode.getDepth(), otherDepth = otherNode.getDepth();
        thisParent.replaceChild(thisIndex, checkPrune(thisNode, otherDepth,
                crossDepth));
        otherParent.replaceChild(otherIndex, checkPrune(otherNode, thisDepth,
                crossDepth));
    }

    public void growMutate(int mutationDepth) {
        Node node = getRandomNode();
        ChanceNode parent = (ChanceNode) node.getParent();

        int startDepth = node.getDepth();

        DecisionTree subtree = new DecisionTree(mutationDepth, seededRandom, false, attributeList, node.getDepth());
        parent.replaceChild(parent.getChildren().indexOf(node),
                checkPrune(subtree.getRoot(), startDepth, mutationDepth));
    }

    public void shrinkMutate(int mutationDepth) {
        Node node = getRandomNode();
        ChanceNode parent = (ChanceNode) node.getParent();
        parent.replaceChild(parent.getChildren().indexOf(node), new EndNode("Class", seededRandom.nextBoolean(),
                node.getDepth()));
    }

    /**
     * Check if a node needs to be pruned recursivley.
     * 
     * @param node
     */
    private Node checkPrune(Node node, int depthToUpdate, int crossDepth) {
        node.setDepth(depthToUpdate);
        if (node instanceof ChanceNode) {
            if (crossDepth == 0 || node.getDepth() >= maxDepth) {
                return new EndNode("Class", seededRandom.nextBoolean(), depthToUpdate);
            } else {
                ChanceNode chanceNode = (ChanceNode) node;
                for (int i = 0; i < chanceNode.getChildren().size(); i++) {
                    chanceNode.getChildren().set(i, checkPrune(chanceNode.getChildren().get(i), depthToUpdate + 1,
                            crossDepth - 1));
                }
                return chanceNode;
            }
        }
        return node;
    }

    // ===== Classifier & Evaluation Methods =====
    // ===========================================
    public double evaluate(ArrayList<String[]> data) {
        correct = 0;
        confusionMatrix = new int[2][2];
        for (String[] row : data) {
            if (classify(row)) {
                correct++;
            }
        }
        this.fitness = (double) correct / data.size();
        return this.fitness;
    }

    public boolean classify(String[] data) {
        return classify(data, root);
    }

    private boolean classify(String[] data, Node node) {
        if (node.isEndNode()) {
            if (((EndNode) node).getOutcome() == true && data[0].equals("recurrence-events")) {
                confusionMatrix[0][0]++;
                return true;
            } else if (((EndNode) node).getOutcome() == true && data[0].equals("no-recurrence-events")) {
                confusionMatrix[0][1]++;
                return false;
            } else if (((EndNode) node).getOutcome() == false && data[0].equals("recurrence-events")) {
                confusionMatrix[1][0]++;
                return false;
            } else if (((EndNode) node).getOutcome() == false && data[0].equals("no-recurrence-events")) {
                confusionMatrix[1][1]++;
                return true;
            }
        } else {
            // Get the index of the attribute
            int index = ((ChanceNode) node).getIndex();
            int valueIndex = 0;
            for (String value : ((ChanceNode) node).getValues()) {
                if (value.equals(data[index])) {
                    return classify(data, ((ChanceNode) node).getChildren().get(valueIndex));
                }
                valueIndex++;
            }
        }
        return false;
    }

    // ===== Helper Methods =====
    // ==========================
    /**
     * @brief Creates a list of the attribute names and their possible values
     * 
     * @return An array of AttributeInformation objects containing the attribute
     *         names and values
     */
    private ArrayList<AttributeInformation> getAttributeList() {
        ArrayList<String[]> attributeValues = getAttributeInformation();
        ArrayList<String> attributeNames = getAttributeNames();

        ArrayList<AttributeInformation> attributeList = new ArrayList<AttributeInformation>();

        for (int i = 1; i < attributeNames.size(); i++) {
            String attribute = attributeNames.get(i);
            String[] values = attributeValues.get(i);
            int index = i;
            attributeList.add(new AttributeInformation(attribute, index, values));
        }

        return attributeList;
    }

    private void addToAllNodesList(Node node) {
        allNodes.add(node);
        if (node instanceof ChanceNode) {
            for (Node child : ((ChanceNode) node).getChildren()) {
                addToAllNodesList(child);
            }
        }
    }

    private void removeFromAllNodesList(Node node) {
        allNodes.remove(node);
        if (node instanceof ChanceNode) {
            for (Node child : ((ChanceNode) node).getChildren()) {
                removeFromAllNodesList(child);
            }
        }
    }

    public void printTree() {
        this.root.print();
    }

    // ===== Helper Classes =====
    // ==========================

    /**
     * @brief A class to store the attribute name and values
     */
    private final class AttributeInformation {

        private final String name;
        private final int index;
        private final String[] values;

        public AttributeInformation(String name, int index, String[] values) {
            this.name = name;
            this.values = values;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }

        public String[] getValues() {
            return values;
        }

    }

}
