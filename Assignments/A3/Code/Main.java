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

        double ANNSeed = 0.6937930107365077;
        double GPSeed = 0.3871152302052989;

        double rndSeed = Math.random();

        Random seededRandomANN = new Random(Double.doubleToLongBits(ANNSeed));
        ANN ann = new ANN(hotOneEncodedDataMatrix, outcomes, seededRandomANN);

        Random seededRandomGP = new Random(Double.doubleToLongBits(GPSeed));
        GP gp = new GP(stringDataMatrix, seededRandomGP);

        printWekaHeader();

    }
}