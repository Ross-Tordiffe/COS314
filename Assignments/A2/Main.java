
import java.util.HashMap;

public class Main extends Helper {
    public static void main(String[] args) {

        HashMap<String, Double> optimums = new HashMap<String, Double>();
        optimums.put("f1_l-d_kp_10_269", 295.0);
        optimums.put("f2_l-d_kp_20_878", 1024.0);
        optimums.put("f3_l-d_kp_4_20", 35.0);
        optimums.put("f4_l-d_kp_4_11", 23.0);
        optimums.put("f5_l-d_kp_15_375", 481.0694);
        optimums.put("f6_l-d_kp_10_60", 52.0);
        optimums.put("f7_l-d_kp_7_50", 107.0);
        optimums.put("f8_l-d_kp_23_10000", 9767.0);
        optimums.put("f9_l-d_kp_5_80", 130.0);
        optimums.put("f10_l-d_kp_20_879", 1025.0);

        HashMap<String, Knapsack> knapsacks = new HashMap<String, Knapsack>();
        // Folder is called "Knapsack Instances watch out for the space"
        knapsacks = readKnapsackData("Knapsack Instances");

        // Run through the genetic algorithm for each knapsack (in order)
        for (String key : knapsacks.keySet()) {

            Knapsack knapsack = knapsacks.get(key);
            GA ga = new GA(knapsack);

            if (ga.getBestFitness() == optimums.get(key)) {
                System.out.println("\033[32moptimal:\033[0m " + key + " Found on iter: " + ga.getBestIteration());
            } else {
                System.out.println("\033[31mnot optimal:\033[0m " + key + " O(" + optimums.get(key) + ")" + " F("
                        + ga.getBestFitness() + ") | out by \033[31m" + (-1 * (ga.getBestFitness() - optimums.get(key)))
                        + "\033[0m " + " Found on iter: " + ga.getBestIteration());
            }
        }
    }
}
