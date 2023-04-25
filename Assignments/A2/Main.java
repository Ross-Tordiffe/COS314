import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class Main extends Helper {
    public static void main(String[] args) {
        
        HashMap<String, Knapsack> knapsacks = new HashMap<String, Knapsack>();
        // Folder is called "Knapsack Instances watch out for the space"
        knapsacks = readKnapsackData("Knapsack Instances");

        // Run through the genetic algorithm for each knapsack
        for(String key : knapsacks.keySet()) {
            Knapsack knapsack = knapsacks.get(key);
            GA ga = new GA(knapsack, 0.8, 0.1, 1.5, 100);
            System.out.print("Best configuration for " + key + " is: [");
            for(int i = 0; i < ga.getBestKnapsack().length; i++) {
                if(ga.getBestKnapsack()[i]) {
                    System.out.print(knapsack.getItems().get(i).getWeight() + "," + knapsack.getItems().get(i).getValue());
                    if(i != ga.getBestKnapsack().length - 1) {
                        System.out.print(",");
                    }
                }
            }
            System.out.println("]");
            System.out.println("Best fitness for " + key + " is: " + ga.getBestFitness());
        }
    }
}

