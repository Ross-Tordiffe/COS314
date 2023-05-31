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
    private final int maxDepth = 6;
    private final int minDepth = 2;
    private final int maxCrossDepth = 3;
    private final int maxMutDepth = 2;
    private final double crossoverRate = 0.8;
    private final double halfAndHalfRate = 0.5;
    private final double trainingSetPercentage = 0.8;
    private final double tournamentSize = 0.2;

    // ===== Constructor =====
    // =======================
    public GP(ArrayList<String[]> dataMatrix, Random seededRandom) {

        double seed = Math.random();
        System.out.print(seed + " ");
        this.seededRandom = new Random(Double.doubleToLongBits(seed));
        this.dataMatrix = dataMatrix;

        // Generate the initial population
        this.population = generatePopulation();

        // Split the data into training and test sets
        organiseData();

        // Run the GP
        for (int i = 0; i < maxGenerations; i++)
            trainGP();

        // Test the best tree
        testGP();
    }

    // ===== Training and Testing ====
    // =====================
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
        if (evaluatePopulation(newPopulation, trainingSet) > currentFitness) {
            population = newPopulation;
        }

    }

    private void testGP() {
        double fitness = bestTree.evaluate(testSet) * 100.0;
        if (fitness > 75)
            System.out.println(String.format("%.2f", fitness) + "%");
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

    // Mutation

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
            trainingSet.add(noRecurrenceEvents.remove(seededRandom.nextInt(noRecurrenceEvents.size())));
        }

        // Create the test set
        while (recurrenceEvents.size() > 0) {
            testSet.add(recurrenceEvents.remove(seededRandom.nextInt(recurrenceEvents.size())));
            testSet.add(noRecurrenceEvents.remove(seededRandom.nextInt(noRecurrenceEvents.size())));
        }
    }
}
