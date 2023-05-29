import java.util.Random;
import java.util.ArrayList;

public class Main extends Helper {
    public static void main(String[] args) {

        // Get data from the file
        readBreastCancerData("breast-cancer.data");
        ArrayList<double[]> dataMatrix = getDataMatrix();
        ArrayList<Double> outcomes = getOutcomes();

        // seed the random number generator for the ANN
        double seed = 0.6853354025016832; // 86.79% for ANN
        Random seededRandom = new Random(Double.doubleToLongBits(seed));

        ANN ann = new ANN(dataMatrix, outcomes, seededRandom);
        System.out.println(String.format("%.2f", ann.getAccuracy()) + "%");

    }
}