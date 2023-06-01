package Code;

import java.util.Random;
import java.util.ArrayList;

public class GP {

    // ===== Variables =====
    // =====================
    // Population and Data
    private ArrayList<DecisionTree> population;
    private ArrayList<String[]> dataMatrix;
    private ArrayList<String[]> trainingSet = new ArrayList<String[]>();
    private ArrayList<String[]> testSet = new ArrayList<String[]>();
    private DecisionTree bestTree = null;
    private double bestFitness = 0.0;

    // Random object
    private final Random seededRandom;

    // Parameters
    private final int populationSize = 100;
    private final int maxGenerations = 50;
    private final int maxNoChange = 20;
    private final int maxDepth = 4;
    private final int minDepth = 2;
    private final int maxCrossDepth = 2;
    private final int maxMutDepth = 3;
    private final double crossoverRate = 0.8;
    private final double halfAndHalfRate = 0.8;
    private final double trainingSetPercentage = 0.8;
    private final double tournamentSize = 0.1;

    // Tracking variables
    private int noChange = 0;
    private boolean doPrinting = true;
    private int generation = 0;

    // ===== Constructor =====
    // =======================
    public GP(ArrayList<String[]> dataMatrix, Random seededRandom) {

        this.seededRandom = seededRandom;
        this.dataMatrix = dataMatrix;

        // Generate the initial population
        this.population = generatePopulation();

        // Split the data into training and test sets
        organiseData();
        printHeader();
        // Run the GP
        for (int i = 0; i < maxGenerations && noChange < maxNoChange; i++) {
            double oldFitness = bestFitness;
            trainGP();
            if (bestFitness > oldFitness) {
                noChange = 0;
            } else {
                noChange++;
            }
            generation++;
        }

        // Test the best tree
        printTestingHeader();
        testGP();
        printResults();
    }

    // ===== Training and Testing ====
    // ===============================
    private void trainGP() {

        double currentFitness = evaluatePopulation(population, trainingSet);

        ArrayList<DecisionTree> newPopulation = new ArrayList<DecisionTree>();

        while (newPopulation.size() < populationSize) {

            // Select two parents
            DecisionTree parent1 = tournamentSelection(population);
            DecisionTree parent2 = tournamentSelection(population);

            if (seededRandom.nextDouble() < crossoverRate) {
                // Crossover
                DecisionTree[] children = crossover(parent1, parent2);
                newPopulation.add(children[0]);
                newPopulation.add(children[1]);
            } else {
                // Mutate
                DecisionTree[] children = mutate(parent1, parent2);
                newPopulation.add(children[0]);
                newPopulation.add(children[1]);
            }

        }

        // Generational replacement
        double newFitness = evaluatePopulation(newPopulation, trainingSet);
        if (newFitness > currentFitness) {
            population = newPopulation;
            System.out.println("    " + String.format(" %-19s \u001B[32m%.1f", generation,
                    (newFitness * 100)) + "%\u001B[0m");
        } else {
            System.out.println("    " + String.format(" %-19s \u001B[31m%.1f", generation,
                    (newFitness * 100)) + "%\u001B[0m");
        }

    }

    private void testGP() {
        double fitness = bestTree.evaluate(testSet) * 100.0;
    }

    // ===== Selection =====
    // =====================

    private DecisionTree tournamentSelection(ArrayList<DecisionTree> population) {

        // Create a tournament
        ArrayList<DecisionTree> tournament = new ArrayList<DecisionTree>();

        // Add random individuals to the tournament
        while (tournament.size() < tournamentSize) {
            tournament.add(population.get(seededRandom.nextInt(population.size())));
        }

        // Find the best individual in the tournament
        DecisionTree best = tournament.get(0);
        for (DecisionTree tree : tournament) {
            if (tree.getFitness() > best.getFitness()) {
                best = tree;
            }
        }

        return best;
    }

    // ===== Genetic Operations =====
    // ==============================

    private DecisionTree[] crossover(DecisionTree parent1, DecisionTree parent2) {

        // Create the new children
        DecisionTree child1 = parent1.copy();
        DecisionTree child2 = parent2.copy();

        // Swap the nodes
        child1.crossover(child2, maxCrossDepth);

        // Add the children to the new population
        DecisionTree[] children = { child1, child2 };
        return children;
    }

    private DecisionTree[] mutate(DecisionTree parent1, DecisionTree parent2) {

        // Create the new children
        DecisionTree child1 = parent1.copy();
        DecisionTree child2 = parent2.copy();

        if (seededRandom.nextBoolean()) {
            child1.growMutate(maxMutDepth);
            child2.growMutate(maxMutDepth);
        } else {
            child1.shrinkMutate(maxMutDepth);
            child2.shrinkMutate(maxMutDepth);
        }

        // Add the children to the new population
        DecisionTree[] children = { child1, child2 };
        return children;
    }

    // ===== Population Generation =====
    // =================================
    private ArrayList<DecisionTree> generatePopulation() {
        ArrayList<DecisionTree> population = new ArrayList<DecisionTree>();
        int growSize = (int) (populationSize * halfAndHalfRate);
        int fullSize = populationSize - growSize;

        // Generate the grow trees
        for (int i = 0; i < growSize; i++) {
            population.add(
                    new DecisionTree(seededRandom.nextInt(maxDepth - minDepth + 1) + minDepth, seededRandom, false));
        }

        // Generate the full trees
        for (int i = 0; i < fullSize; i++) {
            population.add(
                    new DecisionTree(seededRandom.nextInt(maxDepth - minDepth + 1) + minDepth, seededRandom, true));
        }

        return population;
    }

    // ===== Evaluation Methods =====
    // ==============================
    private double evaluatePopulation(ArrayList<DecisionTree> population, ArrayList<String[]> trainSet) {
        double totalFitness = 0;
        for (DecisionTree tree : population) {
            tree.evaluate(trainSet);
            totalFitness += tree.getFitness();
            if (bestTree == null || tree.getFitness() > bestFitness) {
                bestTree = tree;
                bestFitness = tree.getFitness();
            }
        }
        return totalFitness / population.size();
    }

    // ===== DATA ORGANISATION =====
    // =============================
    private void organiseData() {

        // Split the data into recurrence-events and no-recurrence-events
        ArrayList<String[]> recurrenceEvents = new ArrayList<String[]>();
        ArrayList<String[]> noRecurrenceEvents = new ArrayList<String[]>();

        for (String[] row : this.dataMatrix) {
            if (row[0].equals("recurrence-events")) {
                recurrenceEvents.add(row);
            } else {
                noRecurrenceEvents.add(row);
            }
        }

        // Split the data into training and test sets
        int trainingSetSize = (int) (recurrenceEvents.size() * trainingSetPercentage);

        // Create the training set
        for (int i = 0; i < trainingSetSize; i++) {
            trainingSet.add(recurrenceEvents.remove(seededRandom.nextInt(recurrenceEvents.size())));
        }

        // Create the test set
        while (recurrenceEvents.size() > 0) {
            testSet.add(recurrenceEvents.remove(seededRandom.nextInt(recurrenceEvents.size())));
        }

        int noRecurrenceTrainingSetSize = (int) (noRecurrenceEvents.size() * trainingSetPercentage);
        for (int i = 0; i < noRecurrenceTrainingSetSize; i++) {
            trainingSet.add(noRecurrenceEvents.remove(seededRandom.nextInt(noRecurrenceEvents.size())));
        }

        while (noRecurrenceEvents.size() > 0) {
            testSet.add(noRecurrenceEvents.remove(seededRandom.nextInt(noRecurrenceEvents.size())));
        }
    }

    // ===== PRINT FUNCTIONS =====
    // ===========================

    private void printHeader() {
        if (doPrinting) {
            // ascii art of a tree
            System.out.println("   ____  ____ ");
            System.out.println("  / ___||  _ \\");
            System.out.println(" | |  _|| |_) |");
            System.out.println(" | |_| ||  __/ ");
            System.out.println("  \\____||_|   ");
            System.out.println("\n=== Genetic Programming Algorith ===");
            System.out.println("=====================================");
            System.out.println("------------ Parameters ------------- ");
            System.out.println("Population Size              " + populationSize);
            System.out.println("Max Generations              " + maxGenerations);
            System.out.println("Max No Change                " + maxNoChange);
            System.out.println("Max Depth                    " + maxDepth);
            System.out.println("Min Depth                    " + minDepth);
            System.out.println("Max Crossover Depth          " + maxCrossDepth);
            System.out.println("Max Mutation Depth           " + maxMutDepth);
            System.out.println("Crossover Rate               " + crossoverRate);
            System.out.println("Half and Half Rate           " + halfAndHalfRate);
            System.out.println(
                    "Training Set Percentage      " + String.format("%.2f", trainingSetPercentage * 100) + "%");
            System.out.println("Tournament Size              " + tournamentSize);
            System.out.println("=====================================");
            System.out.println("------------- Training --------------");
            System.out.println(String.format("  %-24s", "Datasets") + trainingSet.size());
            System.out.println("-------------------------------------");
            System.out.println("  Evolution         Average Accuracy\n");
        }
    }

    private void printTestingHeader() {
        if (doPrinting) {
            System.out.println("=====================================");
            System.out.println("------------- Testing ---------------");
            System.out.println(String.format("  %-24s", "Datasets") + testSet.size());
            System.out.println("------------ Best Tree --------------");
            this.bestTree.printTree();
        }
    }

    private void printResults() {
        if (doPrinting) {
            System.out.println("=====================================");
            System.out.println("------------- Findings --------------");
            System.out.println(String.format("%-26s", "| True Positive") + this.bestTree.getConfusionMatrix()[0][0]
                    + "\u001B[2m     // recurrence events correctly classified \u001B[0m");
            System.out.println(String.format("%-26s", "| True Negative") + this.bestTree.getConfusionMatrix()[1][1]
                    + "\u001B[2m    // non-recurrence events correctly classified \u001B[0m");
            System.out.println(String.format("%-26s", "| False Positive") + this.bestTree.getConfusionMatrix()[0][1]
                    + "\u001B[2m     // non-recurrence events incorrectly classified \u001B[0m");
            System.out.println(String.format("%-26s", "| False Negative") + this.bestTree.getConfusionMatrix()[1][0]
                    + "\u001B[2m      // recurrence events incorrectly classified \u001B[0m");

            System.out.println("-------------------------------------");
            System.out.println(" --- Positive ---");
            System.out.println(
                    String.format("%-26s", "Positive Precision")
                            + String.format("%.2f", this.bestTree.getBinaryPrecision() * 100));

            System.out.println(
                    String.format("%-26s", "Positive Recall") + String.format("%.2f", this.bestTree.getRecall() * 100));

            System.out
                    .println(String.format("%-26s", "Positive F-Measure")
                            + String.format("%.2f", this.bestTree.getFMeasure() * 100));

            System.out.println("\n --- Negative ---");
            System.out.println(
                    String.format("%-26s", "Negative Precision")
                            + String.format("%.2f", this.bestTree.getNegativePrecision() * 100));
            System.out.println(
                    String.format("%-26s", "Negative Recall")
                            + String.format("%.2f", this.bestTree.getNegativeRecall() * 100));
            System.out.println(
                    String.format("%-26s", "Negative F-Measure")
                            + String.format("%.2f", this.bestTree.getNegativeFMeasure() * 100));
            System.out.println("\n --- Accuracy ---");
            System.out.println(String.format("%-26s", "Accuracy")
                    + String.format("%.2f", this.bestTree.getFitness() * 100) + "%");
            System.out.println("=====================================\n");
        }
    }
}
