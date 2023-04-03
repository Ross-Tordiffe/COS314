import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.*;

public class Main extends Helper {
    public static void main(String[] args) {

        String currentTime = new java.util.Date().toString();

        Integer iterations = 40;
        Integer swaps = 1000;
        Integer reshuffles = 1;

        long startTime = System.currentTimeMillis();

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream("results.txt", true));
            writer.println("\n\nTime of Run: " + currentTime);
            writer.println("Algorithm: Iterated Local Search");
            writer.println(String.format("Iterations: %d, Swaps: %d, Reshuffles: %d", iterations,
                    swaps, reshuffles));
            writer.println(
                    "Filename                 | Optimal Bin Count | Near Optimal Bin Count | Sub Optimal Bin Count | Sum of Bin Counts | Average Runtime (milliseconds)");
            writer.println(
                    "-------------------------|-------------------|------------------------|-----------------------|-------------------|-------------------------------");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        HashMap<String, Double[]> files = new HashMap<String, Double[]>();
        // Value is an array of [optimalBinCount, nearOptimalBinCount, sumOfBinCounts,
        // averageRuntime]
        files.put("Falkenauer/Falkenauer_T", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Falkenauer/Falkenauer_U", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Hard28", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Scholl/Scholl_1", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Scholl/Scholl_2", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Scholl/Scholl_3", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Schwerin/Schwerin_1", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Schwerin/Schwerin_2", new Double[] { 0.0, 0.0, 0.0, 0.0 });
        files.put("Waescher", new Double[] { 0.0, 0.0, 0.0, 0.0 });

        HashMap<String, Integer> optima = readOptima("optima.txt");

        // For ILS
        for (String filename : files.keySet()) {
            HashMap<String, ArrayList<Integer>> values = readFiles(filename);
            HashMap<String, Integer> caps = getCaps(values);

            HashMap<String, ArrayList<ArrayList<Integer>>> instances = organiseInstances(values);

            long avgRuntime = 0;

            for (String instanceName : instances.keySet()) {
                int opt = optima.get(instanceName.substring(0, instanceName.length() - 4));
                ArrayList<ArrayList<Integer>> instance = instances.get(instanceName);
                Integer cap = caps.get(instanceName);
                IteratedLocalSearch ils = new IteratedLocalSearch(instance, instanceName, cap, iterations, swaps,
                        reshuffles);
                if (ils.getBinCount() == opt) {
                    files.get(filename)[0] += 1;
                } else if (ils.getBinCount() <= opt + 1) {
                    files.get(filename)[1] += 1;
                }

                files.get(filename)[2] += 1;

                avgRuntime += ils.getRuntime();
            }

            System.out.println("Runtime:" + avgRuntime / files.get(filename)[2]);
            files.get(filename)[3] = (double) avgRuntime / files.get(filename)[2];

        }

        for (String filename : files.keySet()) {
            Double[] values = files.get(filename);
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

        long endTime = System.currentTimeMillis();
        try {
            PrintWriter writer = new PrintWriter(
                    new FileOutputStream(new File("results.txt"), true));
            writer.println("Total Runtime: " + (endTime - startTime) / 1000 + " seconds");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // For Tabu Search

        // // set the values in files back to 0
        // for (String filename : files.keySet()) {
        // files.put(filename, new Double[] { 0.0, 0.0, 0.0, 0.0 });
        // }

        // startTime = System.currentTimeMillis();

        // try {
        // PrintWriter writer = new PrintWriter(new FileOutputStream("results.txt",
        // true));
        // writer.println("Algorithm: Tabu Search");
        // writer.println(
        // "Filename | Optimal Bin Count | Near Optimal Bin Count | Sum of Bin Counts |
        // Average Runtime (milliseconds)");
        // writer.println(
        // "-------------------------|-------------------|------------------------|-------------------|-------------------------------");
        // writer.close();
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }

        // for (String filename : files.keySet()) {
        // HashMap<String, ArrayList<Integer>> values = readFiles(filename);

        // Integer cap = getCap(values);

        // HashMap<String, ArrayList<ArrayList<Integer>>> instances =
        // organiseInstances(values, cap);
        // long avgRuntime = 0;

        // for (String instanceName : instances.keySet()) {
        // int opt = optima.get(instanceName.substring(0, instanceName.length() - 4));
        // ArrayList<ArrayList<Integer>> tabuInstance = instances.get(instanceName);
        // Tabu ts = new Tabu(tabuInstance, instanceName, cap, iterations, swaps,
        // reshuffles);
        // if (ts.getBinCount() == opt) {
        // files.get(filename)[0] += 1;
        // } else if (ts.getBinCount() <= opt + 1) {
        // files.get(filename)[1] += 1;
        // }

        // files.get(filename)[2] += 1;

        // avgRuntime += ts.getRuntime();
        // }

        // System.out.println("Runtime:" + avgRuntime / files.get(filename)[2]);
        // files.get(filename)[3] = (double) avgRuntime / files.get(filename)[2];

        // }

        // for (String filename : files.keySet()) {
        // Double[] values = files.get(filename);
        // try {
        // PrintWriter writer = new PrintWriter(new FileOutputStream(new
        // File("results.txt"),
        // true /* append = true */));
        // writer.println(String.format("%-25s| %-18d| %-23d| %-18d| %-16.2f", filename,
        // values[0].intValue(),
        // values[1].intValue(), values[2].intValue(), values[3]));

        // writer.close();
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }

        // }

        // endTime = System.currentTimeMillis();
        // try {
        // PrintWriter writer = new PrintWriter(
        // new FileOutputStream(new File("results.txt"), true));
        // writer.println("Total Runtime: " + (endTime - startTime) / 1000 + "
        // seconds");
        // writer.close();
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }

    }
}
