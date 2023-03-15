import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.*;

public class Helper {

    // ====================================
    // =--------- Helper Methods ---------=
    // ====================================

    /**
     * Reads in a file and returns an ArrayList of Integers
     * 
     * @param dirName
     * @return
     */
    public static HashMap<String, ArrayList<Integer>> readFiles(String dirName) {

        File dir = new File("1BPP_Instances/" + dirName);
        File[] files = dir.listFiles();

        HashMap<String, ArrayList<Integer>> fileContents = new HashMap<>();

        Integer cap = null;
        int count = 0;
        ArrayList<Integer> currentFile = null;

        for (File file : files) {
            // Don't include the 1st and 2nd lines of the file
            count++;
            if (file.isFile()) {
                String fileName = file.getName();
                fileContents.put(fileName, new ArrayList<Integer>());
                currentFile = fileContents.get(fileName);
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    br.readLine(); // Skip the first line
                    if (cap == null) { // Save the capacity of the bin
                        cap = Integer.parseInt(br.readLine());
                    } else {
                        br.readLine(); // Or skip the second line if we already have the capacity
                    }
                    while ((line = br.readLine()) != null) {
                        currentFile.add(Integer.parseInt(line));
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (count == 1)
                break;
        }

        // Add the capacity of the bin to the back of the ArrayList
        fileContents.put("cap", new ArrayList<Integer>());
        fileContents.get("cap").add(cap);

        return fileContents;
    }

    /**
     * Prints the contents of a values ArrayList
     * 
     * @param values
     */
    public static void printValues(ArrayList<Integer> values) {
        System.out.println("Bin [");
        for (int i = 0; i < values.size(); i++) {
            System.out.print(values.get(i));
            if (i != values.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * Prints the contents of a files HashMap in a column format
     * 
     * @param fileInstance
     */
    public static void printInstance(HashMap<String, ArrayList<ArrayList<Integer>>> fileInstance) {

        int total = 0, fileTotal = 0, subTotal;

        for (String filename : fileInstance.keySet()) {
            fileTotal = 0;
            System.out.print(filename + " <| ");
            ArrayList<ArrayList<Integer>> bins = fileInstance.get(filename);
            for (int i = 0; i < bins.size(); i++) {
                subTotal = 0;
                System.out.print("Bin " + (i + 1) + " [");
                for (int j = 0; j < bins.get(i).size(); j++) {
                    System.out.print(bins.get(i).get(j));
                    subTotal += bins.get(i).get(j);
                    if (j != bins.get(i).size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("]->" + (1000 - subTotal));
                fileTotal += subTotal;
            }
            System.out.println("|> File Total: " + fileTotal);
        }
        System.out.println("Total: " + total);
    }

    /**
     * Prints the contents of a files HashMap in a loose format
     * 
     * @param fileInstance
     */
    public static void printInstanceLoose(HashMap<String, ArrayList<ArrayList<Integer>>> fileInstance) {

        int total = 0, fileTotal = 0, subTotal;

        for (String filename : fileInstance.keySet()) {
            fileTotal = 0;
            System.out.println(filename + " <| ");
            ArrayList<ArrayList<Integer>> bins = fileInstance.get(filename);
            for (int i = 0; i < bins.size(); i++) {
                subTotal = 0;
                System.out.print("[");
                for (int j = 0; j < bins.get(i).size(); j++) {
                    System.out.print(bins.get(i).get(j));
                    subTotal += bins.get(i).get(j);
                    if (j != bins.get(i).size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.print("]");
                if (i != bins.size() - 1)
                    System.out.print(", ");
                fileTotal += subTotal;
            }
            System.out.println("|> File Total: " + fileTotal);
        }
        System.out.println("Total: " + total);
    }

    /**
     * Removes the cap from the fileContents hashmap and returns it
     * 
     * @param values
     */
    public static Integer getCap(HashMap<String, ArrayList<Integer>> fileContents) {
        Integer cap = fileContents.get("cap").get(0);
        fileContents.remove("cap");
        return cap;
    }

    public static Integer sumBin(ArrayList<Integer> bin) {
        Integer sum = 0;
        for (int i = 0; i < bin.size(); i++) {
            sum += bin.get(i);
        }
        return sum;
    }

    // ===================================== //
    // =--------- Sorting Methods ---------= //
    // ===================================== //

    /**
     * Sorts the values ArrayList into capped bins using First Fit Decreasing
     * 
     * @param values
     * @param cap
     * @return ArrayList<ArrayList<Integer>> bins
     */
    public static ArrayList<ArrayList<Integer>> firstFitDecreasing(ArrayList<Integer> values, Integer cap) {

        ArrayList<ArrayList<Integer>> bins = new ArrayList<>();
        // Add values to the current bin. If it is full, create a new bin and continue
        // until all values have been added
        for (int i = 0; i < values.size(); i++) {
            Integer value = values.get(i);
            // If the current bin is full, create a new bin
            if (bins.size() == 0 || sumBin(bins.get(bins.size() - 1)) + value > cap) {
                ArrayList<Integer> bin = new ArrayList<>();
                bin.add(value);
                bins.add(bin);
            } else {
                // Add the value to the current bin
                bins.get(bins.size() - 1).add(value);
            }
        }

        return bins;

    }

    /**
     * Organises the fileContents into a HashMap of file names and their respective
     * bins
     * 
     * @param fileContents
     * @param cap
     * @return
     */
    public static HashMap<String, ArrayList<ArrayList<Integer>>> organiseInstances(
            HashMap<String, ArrayList<Integer>> fileContents, Integer cap) {

        HashMap<String, ArrayList<ArrayList<Integer>>> instances = new HashMap<>();

        for (String fileName : fileContents.keySet()) {
            ArrayList<Integer> values = fileContents.get(fileName);
            values.remove(values.size() - 1);
            instances.put(fileName, firstFitDecreasing(values, cap));
        }

        return instances;
    }

}
