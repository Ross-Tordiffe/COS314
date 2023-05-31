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
        double ANNSeed = 0.8430774216036232; // for 100 runs, avg 71.84%, max 87.10%, min 51.56% (2 cases below 60%)
                                             // ANN.
        double GPSeed = 0.3871152302052989; // for 100 runs, avg 72.01%, max 87.10%, min 51.56% (2 cases below 60%)
                                            // GP.

        Random seededRandomANN = new Random(Double.doubleToLongBits(ANNSeed));
        ANN ann = new ANN(hotOneEncodedDataMatrix, outcomes, seededRandomANN);

        Random seededRandomGP = new Random(Double.doubleToLongBits(GPSeed));
        GP gp = new GP(stringDataMatrix, seededRandomGP);

    }
}