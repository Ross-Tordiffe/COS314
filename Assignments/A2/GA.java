
import java.util.HashMap;
import java.util.ArrayList;

// Genetic Algorithm For the Knapsack Problem
public class GA {

    Knapsack knapsack;
    ArrayList<Boolean[]> knapsackPopulation;
    ArrayList<Boolean[]> nextGenerationPopulation;
    Boolean[] bestKnapsack;
    double bestFitness;
    int noImprovement = 0;
    double averageFitness = 0;

    ArrayList<Boolean[]> winners;

    int bestIteration = 0;

    // GA Parameters (constants)
    final int POPULATION_MULTIPLIER = 20;
    final double CROSSOVER_RATE = 0.6;
    final double MUTATION_RATE = 0.4;
    final int MAX_GENERATIONS = 150;
    final int STOPPING_ITERATIONS = 75;
    final int PENALTY_FACTOR = 15;
    final double TOURNAMENT_PORTION = 0.3;

    // GA Parameters (variables)
    int populationSize;
    int tournamentSize;

    public GA(Knapsack initalKnapsack) {

        // Create the initial population
        knapsack = initalKnapsack;

        populationSize = (int) (initalKnapsack.getItems().size() * POPULATION_MULTIPLIER);
        tournamentSize = (int) (populationSize * TOURNAMENT_PORTION);
        if (tournamentSize < 2) {
            tournamentSize = 2;
        } else if (tournamentSize % 2 == 1) {
            tournamentSize++;
        }

        knapsackPopulation = new ArrayList<Boolean[]>();

        for (int i = 0; i < populationSize; i++) {
            Boolean[] chromosome = new Boolean[knapsack.getItems().size()];
            for (int j = 0; j < knapsack.getItems().size(); j++) {
                chromosome[j] = Math.random() < 0.5;
            }
            knapsackPopulation.add(chromosome);
        }

        for (int i = 0; i < MAX_GENERATIONS; i++) {

            if (noImprovement >= STOPPING_ITERATIONS) {
                System.out.println("No improvement for " + STOPPING_ITERATIONS + " iterations, stopping");
                break;
            }

            run();

            double currentAverageFitness = getAverageFitness();
            if (currentAverageFitness > averageFitness) {
                averageFitness = currentAverageFitness;
                bestIteration += noImprovement;
                noImprovement = 0;
            } else {
                noImprovement++;
            }

        }

        setBestKnapsack();

    }

    public void run() {

        // Select two knapsacks from the population
        // System.out.println("Tournament Phase");
        tournamentSelection();

        // Perform crossover on the two knapsacks
        // System.out.println("Crossover Phase");
        onePointCrossover();

        // Perform mutation on the two knapsacks
        // System.out.println("Mutation Phase");
        bitFlipMutation();

        // Fill the rest of the new population with random knapsacks
        // System.out.println("Random Phase");
        fillPopulation();

        // Replace the old population with the new population
        knapsackPopulation = nextGenerationPopulation;

    }

    /**
     * @brief Selects a knapsack from the population using tournament selection
     */
    public void tournamentSelection() {

        // Select random knapsacks from the population to compete in the tournament run
        // the tournament. Add the winner to the winners list and remove them and the
        // other competitors from the population, then repeat until the population is
        // empty
        winners = new ArrayList<Boolean[]>();

        while (knapsackPopulation.size() > 0 && winners.size() < populationSize) {

            // Select random knapsacks from the population to compete in the tournament
            ArrayList<Boolean[]> competitors = new ArrayList<Boolean[]>();
            for (int i = 0; i < tournamentSize; i++) {
                int randomIndex = (int) (Math.random() * knapsackPopulation.size());
                competitors.add(knapsackPopulation.get(randomIndex));
            }

            // print all competitors
            // System.out.println("Competitors: ");
            // for (int i = 0; i < competitors.size(); i++) {
            // printChromosome(competitors.get(i));
            // }
            // System.out.println("Population: ");
            // for (int i = 0; i < knapsackPopulation.size(); i++) {
            // printChromosome(knapsackPopulation.get(i));
            // }

            // Run the tournament
            Boolean[] winner = new Boolean[knapsack.getItems().size()];
            double winnerFitness = 0;
            for (int i = 0; i < competitors.size(); i++) {
                double competitorFitness = getPenaltyFitness(competitors.get(i));
                if (competitorFitness > winnerFitness) {
                    winner = competitors.get(i);
                    winnerFitness = competitorFitness;
                }
            }

            // Add the winner to the winners list and remove them and the other competitors
            // from the population
            winners.add(winner);
            for (int i = 0; i < competitors.size(); i++) {
                if (competitors.get(i) != winner) {
                    knapsackPopulation.add(competitors.get(i));
                }
            }

        }

    }

    /**
     * @brief Performs one point crossover on two knapsacks
     */
    public void onePointCrossover() {

        // For each pair of knapsacks in the winners list, determine if crossover will
        // occur. If it will, determine the crossover point and perform crossover

        nextGenerationPopulation = new ArrayList<Boolean[]>();

        for (int i = 0; i < winners.size(); i += 2) {

            Boolean[] parent1 = winners.get(i);
            Boolean[] parent2 = winners.get(i + 1);

            // Determine if crossover will occur
            if (Math.random() < CROSSOVER_RATE) {

                // Determine the crossover point
                int crossoverPoint = (int) (Math.random() * knapsack.getItems().size());

                // Perform crossover
                Boolean[] child1 = new Boolean[knapsack.getItems().size()];
                Boolean[] child2 = new Boolean[knapsack.getItems().size()];

                for (int j = 0; j < crossoverPoint; j++) {
                    child1[j] = parent1[j];
                    child2[j] = parent2[j];
                }

                for (int j = crossoverPoint; j < knapsack.getItems().size(); j++) {
                    child1[j] = parent2[j];
                    child2[j] = parent1[j];
                }

                nextGenerationPopulation.add(child1);
                nextGenerationPopulation.add(child2);

            } else {

                nextGenerationPopulation.add(parent1);
                nextGenerationPopulation.add(parent2);

            }

        }

    }

    /**
     * @brief Performs bit flip mutation on a chromosome
     */
    public void bitFlipMutation() {

        for (int i = 0; i < nextGenerationPopulation.size(); i++) {

            Boolean[] chromosome = nextGenerationPopulation.get(i);

            // Determine if mutation will occur
            if (Math.random() < MUTATION_RATE) {

                // Determine the mutation point
                int mutationPoint = (int) (Math.random() * knapsack.getItems().size());

                // Perform mutation
                chromosome[mutationPoint] = !chromosome[mutationPoint];

            }

        }

    }

    /**
     * @brief fills the rest of the new population with random knapsacks
     */
    public void fillPopulation() {

        while (nextGenerationPopulation.size() < populationSize) {

            Boolean[] chromosome = new Boolean[knapsack.getItems().size()];
            for (int j = 0; j < knapsack.getItems().size(); j++) {
                chromosome[j] = Math.random() < 0.5;
            }
            nextGenerationPopulation.add(chromosome);

        }

        knapsackPopulation = nextGenerationPopulation;

    }

    // === Helper Functions ===

    /**
     * @brief Checks if index of chromosone is in tournament list
     * 
     * @param tournamentSpace
     */
    public boolean isInTournament(int[] tournamentSpace, int index) {
        for (int i = 0; i < tournamentSpace.length; i++) {
            if (tournamentSpace[i] == index) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief Find the best knapsack in the population and set it as the best
     *        knapsack, also set the best fitness
     * 
     */
    public void setBestKnapsack() {
        int bestIndex = 0;
        double bestFitness = 0;

        for (int i = 0; i < populationSize; i++) {
            double fitness = getSumFitness(knapsackPopulation.get(i));
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndex = i;
            }
        }

        bestKnapsack = knapsackPopulation.get(bestIndex);
        this.bestFitness = bestFitness;

    }

    /**
     * @brief Gets the best knapsack and returns it
     * 
     * @return bestKnapsack
     */
    public Boolean[] getBestKnapsack() {
        return bestKnapsack;
    }

    /**
     * @brief Gets the best fitness and returns it
     * 
     * @return bestFitness
     */
    public double getBestFitness() {
        return bestFitness;
    }

    /**
     * @brief Gets the best iteration and returns it
     * 
     * @return bestIteration
     */
    public int getBestIteration() {
        return bestIteration;
    }

    /**
     * @brief Determines the fitness of a knapsack using a penalty if the weight
     *        exceeds the capacity
     * 
     * @param chromosome
     * @return fitness
     */
    public double getPenaltyFitness(Boolean[] chromosome) {
        double fitness = 0;
        double weight = knapsack.getWeight(chromosome);
        double penalty = Math.max(0, weight - knapsack.getCapacity()) * PENALTY_FACTOR;
        // if (weight <= knapsack.getCapacity()) {
        fitness = knapsack.getValue(chromosome) - penalty;
        // }
        return fitness;
    }

    /**
     * @brief Determines the fitness of a knapsack by summing the value of the items
     *        in the knapsack.
     *        If the knapsack is over capacity, the fitness is 0. A higher fitness
     *        is better.
     * 
     * @param chromosome
     * @return fitness
     */
    public double getSumFitness(Boolean[] chromosome) {
        double fitness = 0;
        double weight = knapsack.getWeight(chromosome);
        double value = knapsack.getValue(chromosome);
        if (weight <= knapsack.getCapacity()) {
            fitness = value;
        }

        if (fitness % 1 > 0.0001) {
            fitness = Math.round(fitness * 10000.0) / 10000.0;
        }

        return fitness;
    }

    /**
     * @brief Determines the average fitness of the population
     * 
     * @return averageFitness
     */
    public double getAverageFitness() {
        double averageFitness = 0;
        for (int i = 0; i < populationSize; i++) {
            averageFitness += getPenaltyFitness(knapsackPopulation.get(i));
        }
        averageFitness /= populationSize;
        return averageFitness;
    }

    /**
     * @brief print a chromosome
     * 
     * @param chromosome
     */
    public void printChromosome(Boolean[] chromosome) {
        System.out.print("[");
        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i]) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
            if (i != chromosome.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

}
