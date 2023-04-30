import java.util.ArrayList;

public class ACO extends Helper {

    Knapsack knapsack;
    ArrayList<Boolean[]> ants;
    Boolean[] bestKnapsack;
    double bestFitness;
    int noImprovement = 0;
    double averageFitness = 0;
    long timeTaken = 0;

    // ACO parameters (constants)
    final int NUM_ANTS = 3;
    final int MAX_ITERATIONS = 100;
    final double ALPHA = 1.0;
    final double BETA = 5.0;
    final double EVAPORATION_RATE = 0.5;
    final double UPDATE_STRENTH = 0.5;
    final double EXPLORATION_PROBABILITY = 0.5;

    // ACO parameters (variables)
    public ACO(Knapsack initalKnapsack) {

        knapsack = initalKnapsack;
        createInitialPopulation();

        run();

    }

    /**
     * @brief Run the ACO algorithm
     */
    public void run() {

        // output the ants
        for (int i = 0; i < ants.size(); i++) {
            System.out.println("Ant " + i + ": ");
            printSolution(ants.get(i));
        }

    }

    /**
     * @brief Creates an initial population of knapsacks (ants)
     */
    private void createInitialPopulation() {

        // Add random items to an inital solution until it is full capacity
        ArrayList<Integer> itemList = new ArrayList<Integer>();
        for (int i = 0; i < itemList.size(); i++) {
            itemList.add(i);
        }

        Boolean[] initalSolution = new Boolean[knapsack.getItems().size()];

        while (knapsack.getWeight(initalSolution) < knapsack.getCapacity()) {
            int randomItem = (int) (Math.random() * itemList.size());
            initalSolution[randomItem] = true;
            itemList.remove(randomItem);
        }

        // Create the ants with the inital solution (deep copy)
        ants = new ArrayList<Boolean[]>();
        for (int i = 0; i < NUM_ANTS; i++) {
            ants.add(initalSolution);
        }

    }

    // === Helper functions ===

}
