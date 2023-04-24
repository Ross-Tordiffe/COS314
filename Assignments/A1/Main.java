import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class Main extends Helper {
    public static void main(String[] args) {

        String currentTime = new java.util.Date().toString();

        long ILSTime = 0;
        long tabuTime = 0;

        Integer RUNS = 3;
        Boolean RUN_ILS = true;
        Boolean RUN_TABU = true;

        Integer ITERATION_MULTIPLIER = 10;
        Integer TABU_LIST_SIZE = 10;
        Integer NEIGHBOURHOOD_SIZE = 5;

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File("results.txt"), true));
            writer.println("\n\n========== RUNS " + RUNS + " ==========");
            writer.println("ILS-" + RUN_ILS + " TABU-" + RUN_TABU);
            writer.println("ITERATION_MULTIPLIER:" + ITERATION_MULTIPLIER + " TABU_LIST_SIZE:"
                    + TABU_LIST_SIZE + " NEIGHBOURHOOD_SIZE:" + NEIGHBOURHOOD_SIZE);
            writer.println("==============================");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, Double[]> ILSFiles = null;
        if (RUN_ILS) {
            ILSFiles = new HashMap<String, Double[]>();

            ILSFiles.put("Falkenauer/Falkenauer_T", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Falkenauer/Falkenauer_U", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Hard28", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Scholl/Scholl_1", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Scholl/Scholl_2", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Scholl/Scholl_3", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Schwerin/Schwerin_1", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Schwerin/Schwerin_2", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            ILSFiles.put("Waescher", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        }

        HashMap<String, Double[]> tabuFiles = null;
        if (RUN_TABU) {
            tabuFiles = new HashMap<String, Double[]>();

            tabuFiles.put("Falkenauer/Falkenauer_T", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Falkenauer/Falkenauer_U", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Hard28", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Scholl/Scholl_1", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Scholl/Scholl_2", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Scholl/Scholl_3", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Schwerin/Schwerin_1", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Schwerin/Schwerin_2", new Double[] { 0.0, 0.0, 0.0, 0.0 });
            tabuFiles.put("Waescher", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        }

        for (int i = 0; i < RUNS; i++) {

            int ILSFileCount = 0;
            int tabuFileCount = 0;

            HashMap<String, Integer> optima = readOptima("optima.txt");

            // For ILS
            if (RUN_ILS) {

                long startTime = System.currentTimeMillis();

                for (String filename : ILSFiles.keySet()) {

                    HashMap<String, ArrayList<Integer>> values = readFiles(filename);
                    HashMap<String, Integer> fileIterations = getCaps(values);
                    HashMap<String, Integer> caps = getCaps(values);
                    Integer optimalBins = 0;
                    Integer nearOptimalBins = 0;
                    Integer totalBins = 0;

                    HashMap<String, ArrayList<ArrayList<Integer>>> instances = organiseInstances(values);
                    long avgRuntime = 0;

                    for (String instanceName : instances.keySet()) {
                        int opt = optima.get(instanceName.substring(0, instanceName.length() - 4));
                        ArrayList<ArrayList<Integer>> instance = instances.get(instanceName);
                        Integer iterations = fileIterations.get(instanceName);
                        Integer cap = caps.get(instanceName);
                        IteratedLocalSearch ils = new IteratedLocalSearch(instance, cap, (iterations
                                * ITERATION_MULTIPLIER));

                        if (ils.getBinCount() <= opt) {
                            optimalBins += 1;
                        } else if (ils.getBinCount() == opt + 1) {
                            nearOptimalBins += 1;
                        }
                        totalBins += 1;
                        avgRuntime += ils.getRuntime();
                    }

                    avgRuntime = avgRuntime / (long) totalBins;

                    // Detrmine progress
                    System.out.print("\033[H\033[2J");
                    double progress = ((double) ++ILSFileCount / (double) ILSFiles.size()) * 100;
                    if (RUN_TABU)
                        progress = (progress + ((double) tabuFileCount / (double) tabuFiles.size()) * 100) / 2;
                    progress = (progress / RUNS) + (i * (100 / RUNS));
                    System.out.println("Progress: " + Math.round(progress * 100.0) / 100.0 + "%");

                    ILSFiles.get(filename)[0] += optimalBins;
                    ILSFiles.get(filename)[1] += nearOptimalBins;
                    ILSFiles.get(filename)[2] += totalBins;
                    ILSFiles.get(filename)[3] += (double) avgRuntime;

                }

                ILSTime += System.currentTimeMillis() - startTime;

            }

            // For Tabu Search
            if (RUN_TABU) {

                long startTime = System.currentTimeMillis();

                for (String filename : tabuFiles.keySet()) {

                    HashMap<String, ArrayList<Integer>> values = readFiles(filename);
                    HashMap<String, Integer> fileIterations = getCaps(values);
                    HashMap<String, Integer> caps = getCaps(values);
                    Integer optimalBins = 0;
                    Integer nearOptimalBins = 0;
                    Integer totalBins = 0;

                    HashMap<String, ArrayList<ArrayList<Integer>>> instances = organiseInstances(values);
                    long avgRuntime = 0;

                    for (String instanceName : instances.keySet()) {
                        int opt = optima.get(instanceName.substring(0, instanceName.length() - 4));
                        ArrayList<ArrayList<Integer>> instance = instances.get(instanceName);
                        Integer iterations = fileIterations.get(instanceName);
                        Integer cap = caps.get(instanceName);
                        Tabu ts = new Tabu(instance, cap, (iterations * ITERATION_MULTIPLIER),
                                TABU_LIST_SIZE,
                                NEIGHBOURHOOD_SIZE);

                        if (ts.getBinCount() <= opt) {
                            optimalBins += 1;
                        } else if (ts.getBinCount() == opt + 1) {
                            nearOptimalBins += 1;
                        }

                        totalBins += 1;
                        avgRuntime += ts.getRuntime();
                    }

                    avgRuntime = avgRuntime / (long) totalBins;

                    // Detrmine progress
                    System.out.print("\033[H\033[2J");
                    double progress = ((double) ++tabuFileCount / (double) tabuFiles.size()) * 100;
                    if (RUN_ILS)
                        progress = (progress + ((double) ILSFileCount / (double) ILSFiles.size()) * 100) / 2;
                    progress = (progress / RUNS) + (i * (100 / RUNS));
                    System.out.println("Progress: " + Math.round(progress * 100.0) / 100.0 + "%");

                    tabuFiles.get(filename)[0] += optimalBins;
                    tabuFiles.get(filename)[1] += nearOptimalBins;
                    tabuFiles.get(filename)[2] += totalBins;
                    tabuFiles.get(filename)[3] += (double) avgRuntime;

                }

                tabuTime += System.currentTimeMillis() - startTime;

            }

        }

        // Writes the results to a file
        if (RUN_ILS) {

            try {
                PrintWriter writer = new PrintWriter(new FileOutputStream("results.txt",
                        true));
                writer.println("\nAlgorithm: Iterative Local Search");
                writer.println(
                        "Filename                 | Optimal Bin Count | Near Optimal Bin Count | Sub Optimal Bin Count | Sum of Bin Counts | Average Runtime (milliseconds)");
                writer.println(
                        "-------------------------|-------------------|------------------------|-----------------------|-------------------|-------------------------------");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            for (String filename : ILSFiles.keySet()) {

                ILSFiles.get(filename)[0] = ILSFiles.get(filename)[0] / RUNS;
                ILSFiles.get(filename)[1] = ILSFiles.get(filename)[1] / RUNS;
                ILSFiles.get(filename)[2] = ILSFiles.get(filename)[2] / RUNS;
                ILSFiles.get(filename)[3] = ILSFiles.get(filename)[3] / RUNS;

                Double[] values = ILSFiles.get(filename);

                try {
                    PrintWriter writer = new PrintWriter(new FileOutputStream(new File("results.txt"),
                            true /* append = true */));
                    writer.println(String.format("%-25s| %-18.0f| %-23.0f| %-22.0f| %-18.0f| %-30.0f",
                            filename, values[0], values[1], values[2] - values[0] - values[1], values[2],
                            values[3]));
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            try {
                PrintWriter writer = new PrintWriter(
                        new FileOutputStream(new File("results.txt"), true));
                writer.println("Total Runtime: " + (ILSTime) / 1000 + " seconds");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (RUN_TABU) {

            try {
                PrintWriter writer = new PrintWriter(new FileOutputStream("results.txt",
                        true));
                writer.println("\nAlgorithm: Tabu Search");
                writer.println(
                        "Filename                 | Optimal Bin Count | Near Optimal Bin Count | Sub Optimal Bin Count | Sum of Bin Counts | Average Runtime (milliseconds)");
                writer.println(
                        "-------------------------|-------------------|------------------------|-----------------------|-------------------|-------------------------------");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            for (String filename : tabuFiles.keySet()) {

                tabuFiles.get(filename)[0] = tabuFiles.get(filename)[0] / RUNS;
                tabuFiles.get(filename)[1] = tabuFiles.get(filename)[1] / RUNS;
                tabuFiles.get(filename)[2] = tabuFiles.get(filename)[2] / RUNS;
                tabuFiles.get(filename)[3] = tabuFiles.get(filename)[3] / RUNS;

                Double[] values = tabuFiles.get(filename);

                try {
                    PrintWriter writer = new PrintWriter(new FileOutputStream(new File("results.txt"),
                            true /* append = true */));
                    writer.println(String.format("%-25s| %-18.0f| %-23.0f| %-22.0f| %-18.0f| %-30.0f",
                            filename, values[0], values[1], values[2] - values[0] - values[1], values[2],
                            values[3]));
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            try {
                PrintWriter writer = new PrintWriter(
                        new FileOutputStream(new File("results.txt"), true));
                writer.println("Total Runtime: " + (tabuTime) / 1000 + " seconds");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

}
