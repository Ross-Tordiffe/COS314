package Code;

import java.util.Random;
import java.util.ArrayList;

public class Main extends Helper {
    public static void main(String[] args) {

        // Get data from the file
        readBreastCancerData("breast-cancer.data");
        ArrayList<double[]> hotOneEncodedDataMatrix = getHotDataMatrix();
        ArrayList<String[]> stringDataMatrix = getStringDataMatrix();
        ArrayList<Double> outcomes = getOutcomes();

        // seed the random number generator for the ANN
        double seed = 0.8430774216036232; // for 100 runs, avg 71.84%, max 87.10%, min 51.56% (2 cases below 60%) ANN.

        Random seededRandom = new Random(Double.doubleToLongBits(seed));
        // ANN ann = new ANN(hotOneEncodedDataMatrix, outcomes, seededRandom);
        for (int i = 0; i < 100; i++) {
            GP gp = new GP(stringDataMatrix, seededRandom);
        }

    }
}