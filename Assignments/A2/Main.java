
import java.util.HashMap;

public class Main extends Helper {
    public static void main(String[] args) {

        int RUN_COUNT = 10;

        boolean runGA = false;
        boolean runASO = true;

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
        optimums.put("knapPI_1_100_1000_1", 9147.0);

        HashMap<String, Knapsack> knapsacks = new HashMap<String, Knapsack>();
        // Folder is called "Knapsack Instances watch out for the space"
        knapsacks = readKnapsackData("Knapsack Instances");

        boolean first = true;

        // Run through the genetic algorithm for each knapsack
        if (runGA) {
            for (String key : knapsacks.keySet()) {

                Knapsack knapsack = knapsacks.get(key);

                int averageIterations = 0;
                int hits = 0;
                int averageOutBy = 0;
                int averageTime = 0;

                for (int i = 0; i < RUN_COUNT; i++) {
                    GA ga = new GA(knapsack);

                    if (first) {
                        first = false;
                        ga.printParameters();
                    }

                    averageIterations += ga.getBestIteration();
                    if (ga.getBestFitness() == optimums.get(key)) {
                        hits++;
                    } else {
                        averageOutBy += (-1 * (ga.getBestFitness() - optimums.get(key)));
                    }
                    averageTime += ga.getTimeElapsed();

                }

                averageIterations /= RUN_COUNT;
                averageTime /= RUN_COUNT;

                if (hits < RUN_COUNT) {
                    averageOutBy /= (RUN_COUNT - hits);
                }

                if (hits >= Math.floor(RUN_COUNT / 2)) {
                    System.out.println("\033[32mMajority Optimal:\033[0m Avg Time:" + averageTime);
                } else {
                    System.out.println("\033[31mMajority Not Optimal:\033[0m Avg Time:" + averageTime);
                }

                System.out.println(key + " - Optimal: " + optimums.get(key) + " | Average Iterations: "
                        + (averageIterations / RUN_COUNT) + " | Found Optimal: " + hits + "/" + RUN_COUNT
                        + " | Average Out By: " + averageOutBy);

            }
        }

        // run through ACO for each knapsack
        if (runASO) {
            for (String key : knapsacks.keySet()) {

                if (!key.equals("f4_l-d_kp_4_11")) {
                    continue;
                }

                Knapsack knapsack = knapsacks.get(key);

                // int averageIterations = 0;
                // int hits = 0;
                // int averageOutBy = 0;
                // int averageTime = 0;

                for (int i = 0; i < RUN_COUNT; i++) {
                    ACO aco = new ACO(knapsack);

                    // if (first) {
                    // first = false;
                    // aco.printParameters();
                    // }

                    // averageIterations += aco.getBestIteration();
                    // if (aco.getBestFitness() == optimums.get(key)) {
                    // hits++;
                    // } else {
                    // averageOutBy += (-1 * (aco.getBestFitness() - optimums.get(key)));
                    // }
                    // averageTime += aco.getTimeElapsed();

                }

                // averageIterations /= RUN_COUNT;
                // averageTime /= RUN_COUNT;

                // if (hits < RUN_COUNT) {
                // averageOutBy /= (RUN_COUNT - hits);
                // }

                // if (hits >= Math.floor(RUN_COUNT / 2)) {
                // System.out.println("\033[32mMajority Optimal:\033[0m Avg Time:" +
                // averageTime);
                // } else {
                // System.out.println("\033[31mMajority Not Optimal:\033[0m Avg Time:" +
                // averageTime);
                // }

                // System.out.println(key + " - Optimal: " + optimums.get(key) + " | Average
                // Iterations: "
                // + (averageIterations / RUN_COUNT) + " | Found Optimal: " + hits + "/" +
                // RUN_COUNT
                // + " | Average Out By: " + averageOutBy);

            }
        }
    }
}

// if (ga.getBestFitness() == optimums.get(key)) {
// System.out.println("\033[32moptimal:\033[0m " + key + " Found on iter: " +
// ga.getBestIteration());
// } else {
// System.out.println("\033[31mnot optimal:\033[0m " + key + " O(" +
// optimums.get(key) + ")" + " F("
// + ga.getBestFitness() + ") | out by \033[31m"
// + (-1 * (ga.getBestFitness() - optimums.get(key)))
// + "\033[0m " + " Found on iter: " + ga.getBestIteration());
// }