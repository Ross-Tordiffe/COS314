
import java.util.HashMap;
// Genetic Algorithm For the Knapsack Problem
public class GA {
    
    int populationSize;
    int tournamentSize = 3;
    Knapsack knapsack;
    Boolean[][] knapsackPopulation;
    double crossoverRate;
    double mutationRate;

    Boolean[] bestKnapsack;
    double bestFitness;

    Boolean[][] parents;
    Boolean[][] children;
    
    public GA(Knapsack initalKnapsack, double crossoverRate, double mutationRate, double populationMultiplier, int numGenerations) {
        
        // Create the initial population
        knapsack = initalKnapsack;
        populationSize = (int) (populationMultiplier * initalKnapsack.getItems().size());

        knapsackPopulation = new Boolean[populationSize][initalKnapsack.getItems().size()];

        for(int i = 0; i < populationSize; i++) {
            knapsackPopulation[i] = knapsack.generateRandomChromosome();
        }

        // Run the genetic algorithm
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;

        for(int i = 0; i < numGenerations; i++) {
            run();
        }

        setBestKnapsack();

    }

    public void run() {

        // Select two knapsacks from the population
        tournamentSelection();

        // Perform crossover on the two knapsacks
        onePointCrossover();

        // Perform mutation on the two knapsacks
        bitFlipMutation();

        // Replace the two worst knapsacks in the population with the two children
        replaceWorst();
        
    }

    /**
     * @brief Selects a knapsack from the population using tournament selection
     */
    public void tournamentSelection() {
        
        parents = new Boolean[2][knapsack.getItems().size()];

        int[] tournamentSpace = new int[tournamentSize*2];

        // Select two random knapsacks from the population
        for(int i = 0; i < tournamentSize*2; i++) {
            int index = (int) (Math.random() * populationSize);
            while(isInTournament(tournamentSpace, index)) {
                index = (int) (Math.random() * populationSize);
            }
            tournamentSpace[i] = index;
        }

        // Find the best two knapsacks in the tournament space
        int bestIndex = 0;
        int secondBestIndex = 0;
        double bestFitness = 0;
        
        for(int i = 0; i < tournamentSize*2; i++) {
            double fitness = getSumFitness(knapsackPopulation[tournamentSpace[i]]);
            if(fitness > bestFitness) {
                secondBestIndex = bestIndex;
                bestFitness = fitness;
                bestIndex = tournamentSpace[i];
            }
        }

        parents[0] = knapsackPopulation[bestIndex];
        parents[1] = knapsackPopulation[secondBestIndex];       

    }

    /**
     * @brief Performs one point crossover on two knapsacks
     */
    public void onePointCrossover() {

        children = new Boolean[2][knapsack.getItems().size()];

        // Determine if crossover will occur
        if(Math.random() < crossoverRate) {
            // Determine the crossover point
            int crossoverPoint = (int) (Math.random() * knapsack.getItems().size());

            // Perform crossover
            for(int i = 0; i < crossoverPoint; i++) {
                children[0][i] = parents[0][i];
                children[1][i] = parents[1][i];
            }
            for(int i = crossoverPoint; i < knapsack.getItems().size(); i++) {
                children[0][i] = parents[1][i];
                children[1][i] = parents[0][i];
            }
        } else {
            children[0] = parents[0];
            children[1] = parents[1];
        }
        
    }

    /**
     * @brief Performs bit flip mutation on a knapsack
     */
    public void bitFlipMutation() {

        // Determine if mutation will occur
        if(Math.random() < mutationRate) {
            // Determine the mutation point
            int mutationPoint = (int) (Math.random() * knapsack.getItems().size());

            // Perform mutation
            children[0][mutationPoint] = !children[0][mutationPoint];
        }

        // Determine if mutation will occur
        if(Math.random() < mutationRate) {
            // Determine the mutation point
            int mutationPoint = (int) (Math.random() * knapsack.getItems().size());

            // Perform mutation
            children[1][mutationPoint] = !children[1][mutationPoint];
        }
        
    }
    
    /**
     * @brief Replaces the worst two knapsacks in the population with the two children
     */
    public void replaceWorst() {

        int worstIndex = 0;
        int secondWorstIndex = 0;
        double worstFitness = 0;
        
        for(int i = 0; i < populationSize; i++) {
            double fitness = getSumFitness(knapsackPopulation[i]);
            if(fitness < worstFitness) {
                secondWorstIndex = worstIndex;
                worstFitness = fitness;
                worstIndex = i;
            }
        }

        knapsackPopulation[worstIndex] = children[0];
        knapsackPopulation[secondWorstIndex] = children[1];
        
    }

    // === Helper Functions ===

    /**
     * @brief Checks if index of chromosone is in tournament list
     * 
     * @param tournamentSpace
     */
    public boolean isInTournament(int[] tournamentSpace, int index) {
        for(int i = 0; i < tournamentSpace.length; i++) {
            if(tournamentSpace[i] == index) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief Find the best knapsack in the population and set it as the best knapsack, also set the best fitness
     * 
     */
    public void setBestKnapsack() {
        int bestIndex = 0;
        double bestFitness = 0;
        
        for(int i = 0; i < populationSize; i++) {
            double fitness = getSumFitness(knapsackPopulation[i]);
            if(fitness > bestFitness) {
                bestFitness = fitness;
                bestIndex = i;
            }
        }

        bestKnapsack = knapsackPopulation[bestIndex];
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
     * @brief Determines the fitness of a knapsack using a ratio of value against weight. 
     * If the knapsack is over capacity, the fitness is 0. A higher fitness is better.
     * 
     * @param chromosome
     * @return fitness
     */
    public double getRatioFitness(Boolean[] chromosome) {
        double fitness = 0;
        double weight = knapsack.getWeight(chromosome);
        double value = knapsack.getValue(chromosome);
        if(weight <= knapsack.getCapacity()) {
            fitness = value / weight;
        }
        return fitness;
    }

    /**
     * @brief Determines the fitness of a knapsack by summing the value of the items in the knapsack. 
     * If the knapsack is over capacity, the fitness is 0. A higher fitness is better.
     * 
     * @param chromosome
     * @return fitness
     */
    public double getSumFitness(Boolean[] chromosome) {
        double fitness = 0;
        double weight = knapsack.getWeight(chromosome);
        double value = knapsack.getValue(chromosome);
        if(weight <= knapsack.getCapacity()) {
            fitness = value;
        }
        return fitness;
    }

}
