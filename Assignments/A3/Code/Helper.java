package Code;

import java.io.*;
import java.util.ArrayList;

public class Helper {

    private static double[] averages;
    private static ArrayList<double[]> hotDataMatrix = new ArrayList<double[]>();
    private static ArrayList<double[]> dataMatrix = new ArrayList<double[]>();
    private static ArrayList<String[]> stringDataMatrix = new ArrayList<String[]>();
    private static ArrayList<Double> outcomes = new ArrayList<Double>();
    private static ArrayList<String[]> attributeInformation = getAttributeInformation();
    private final static int NUM_ATTRIBUTES = 10;
    private final static int TOTAL_ATTRIBUTES = 51;

    /**
     * 
     * @param folderName
     * @return a normalized data array
     */
    public static void readBreastCancerData(String filename) {

        ArrayList<double[]> data = new ArrayList<double[]>();
        ArrayList<String[]> lineMatrix = new ArrayList<String[]>();
        // File is in sibling directory called Data
        File file = new File("Data\\" + filename);
        BufferedReader br = null;
        String line = "";
        averages = new double[NUM_ATTRIBUTES];

        try {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                String[] lineData = line.split(",");
                lineMatrix.add(lineData);
                double[] instance = handleStringData(lineData);
                data.add(instance);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO Exception");
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println("IO Exception");
                }
            }
        }

        for (int i = 0; i < averages.length; i++) {
            averages[i] = Math.round(averages[i] /= data.size());
        }

        // fill hot data matrix
        for (int i = 0; i < data.size(); i++) { // For each instance
            if (data.get(i)[0] == 0) { // Assign the outcome
                outcomes.add(0.0);
            } else {
                outcomes.add(1.0);
            }
            hotDataMatrix.add(fillOneHot(data.get(i)));
        }

        dataMatrix = data;

        // fill string data matrix and data matrix average values
        for (int i = 0; i < stringDataMatrix.size(); i++) {
            for (int j = 0; j < stringDataMatrix.get(i).length; j++) {
                if (stringDataMatrix.get(i)[j].equals("?")) {
                    stringDataMatrix.get(i)[j] = attributeInformation.get(j)[(int) averages[j]];
                    dataMatrix.get(i)[j] = averages[j];
                }
            }
        }
    }

    /**
     * @brief returns a numerically encoded array from a string of tabular data
     * 
     * @param line
     */
    private static double[] handleStringData(String[] line) {

        double[] instance = new double[10];
        String[] stringValues = new String[10];
        for (int i = 0; i < attributeInformation.size(); i++) {
            for (int j = 0; j < attributeInformation.get(i).length; j++) {
                if (line[i].equals("?")) {
                    instance[i] = -1;
                    continue;
                }
                if (line[i].equals(attributeInformation.get(i)[j])) {
                    instance[i] = (double) j;
                    averages[i] += j;
                    continue;
                }
            }
            stringValues[i] = line[i];
        }

        stringDataMatrix.add(stringValues);

        return instance;
    }

    private static double[] fillOneHot(double[] instance) {

        double[] oneHot = new double[TOTAL_ATTRIBUTES];
        ArrayList<String[]> attributeInformation = getAttributeInformation();
        int index = 0;

        for (int i = 1; i < attributeInformation.size(); i++) {
            for (int j = 0; j < attributeInformation.get(i).length; j++) {
                if (instance[i] == j || (instance[i] == -1 && averages[i] == j)) {
                    oneHot[index] = 1;
                    if ((instance[i] == -1 && averages[i] == j)) {
                    }
                } else {
                    oneHot[index] = 0;
                }
                index++;
            }
        }

        // printDataInstance(oneHot);

        return oneHot;
    }

    public static void printDataMatrix(double[][] dataMatrix, String[][] lineMatrix) {

        for (int i = 0; i < dataMatrix.length; i++) {
            System.out.print(i + ": [");
            for (int j = 0; j < lineMatrix[i].length; j++) {
                System.out.print(lineMatrix[i][j]);
                if (j != lineMatrix[i].length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
            System.out.print("    [");
            for (int j = 0; j < dataMatrix[i].length; j++) {
                System.out.print(dataMatrix[i][j]);
                if (j != dataMatrix[i].length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }

    public static void printDataString(String[] data) {
        System.out.print("[");
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i]);
            if (i != data.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void printDataInstance(double[] data) {
        System.out.print("[");
        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i]);
            if (i != data.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * @brief returns a list of all possible values for each attribute
     * @return
     */
    public static ArrayList<String[]> getAttributeInformation() {

        ArrayList<String[]> attributeInformation = new ArrayList<String[]>();
        attributeInformation.add(new String[] { "no-recurrence-events", "recurrence-events" });
        attributeInformation.add(new String[] { "10-19", "20-29", "30-39", "40-49", "50-59", "60-69", "70-79",
                "80-89", "90-99" });
        attributeInformation.add(new String[] { "lt40", "ge40", "premeno" });
        attributeInformation.add(new String[] { "0-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34",
                "35-39", "40-44", "45-49", "50-54", "55-59" });
        attributeInformation.add(new String[] { "0-2", "3-5", "6-8", "9-11", "12-14", "15-17", "18-20",
                "21-23", "24-26", "27-29", "30-32", "33-35", "36-39" });
        attributeInformation.add(new String[] { "yes", "no" });
        attributeInformation.add(new String[] { "1", "2", "3" });
        attributeInformation.add(new String[] { "left", "right" });
        attributeInformation.add(new String[] { "left_up", "left_low", "right_up", "right_low", "central" });
        attributeInformation.add(new String[] { "yes", "no" });

        return attributeInformation;
    }

    /**
     * @brief returns a list of names for each attribute
     * @return
     */
    public static ArrayList<String> getAttributeNames() {

        ArrayList<String> attributeNames = new ArrayList<String>();
        attributeNames.add("Class");
        attributeNames.add("Age");
        attributeNames.add("Menopause");
        attributeNames.add("Tumor Size");
        attributeNames.add("Inv Nodes");
        attributeNames.add("Node Caps");
        attributeNames.add("Deg Malig");
        attributeNames.add("Breast");
        attributeNames.add("Breast Quad");
        attributeNames.add("Irradiat");

        return attributeNames;
    }

    /**
     * @brief returns the outcomes matrix one hot encoded
     * @return hotOutcomes
     */
    public static ArrayList<Double> getOutcomes() {
        return outcomes;
    }

    /**
     * @brief returns the data matrix
     * @return dataMatrix
     */
    public static ArrayList<double[]> getDataMatrix() {
        return dataMatrix;
    }

    /**
     * @brief returns the data matrix one hot encoded
     * @return hotDataMatrix
     */
    public static ArrayList<double[]> getHotDataMatrix() {
        return hotDataMatrix;
    }

    /**
     * @brief returns the data matrix unencoded
     * @return unencodedDataMatrix
     */
    public static ArrayList<String[]> getStringDataMatrix() {
        return stringDataMatrix;
    }

    // ===== Print Methods =====
    // =========================

    public static void printWekaHeader() {
        System.out.println(" __        _______ _  __    _    ");
        System.out.println(" \\ \\      / / ____| |/ /   / \\");
        System.out.println("  \\ \\ /\\ / /|  _| | ' /   / _ \\ ");
        System.out.println("   \\ V  V / | |___| . \\  / ___ \\ ");
        System.out.println("    \\_/\\_/  |_____|_|\\_\\/_/   \\_\\");
        System.out.println("\n== C4.5 Descision Tree using WEKA ==");
        System.out.println("=====================================");
        System.out.println("------------- Findings --------------");
        System.out.println(String.format("%-26s", "| True Positive") + 20
                + "\u001B[2m     // recurrence events correctly classified \u001B[0m");
        System.out.println(String.format("%-26s", "| True Negative") + 199
                + "\u001B[2m    // non-recurrence events correctly classified \u001B[0m");
        System.out.println(String.format("%-26s", "| False Positive") + 19
                + "\u001B[2m     // non-recurrence events incorrectly classified \u001B[0m");
        System.out.println(String.format("%-26s", "| False Negative") + 48
                + "\u001B[2m      // recurrence events incorrectly classified \u001B[0m");

        System.out.println("");
        System.out.println(
                String.format("%-26s", "Precision") + String.format("%.2f", 0.513 * 100));

        System.out.println(String.format("%-26s", "Recall") + String.format("%.2f", 0.294 * 100));

        System.out
                .println(String.format("%-26s", "F-Measure") + String.format("%.2f", 0.374 * 100));
        System.out.println("");
        System.out.println(String.format("%-26s", "Accuracy") + String.format("%.2f", 0.765734 * 100)
                + "%");

        System.out.println("=====================================\n");
    }

}
