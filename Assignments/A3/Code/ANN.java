package Code;

import java.util.Random;
import java.util.ArrayList;

public class ANN {

    // ===== Variables =====
    // =====================
    // Layer Nodes
    private Double[] inputLayerNodes;
    private Double[] hiddenLayerNodes;
    private Double outputLayerNode;

    // Bias Nodes
    private final Double hiddenBias = 1.0;
    private final Double outputBias = 1.0;

    // Weights
    private Double[][] weightsIH;
    private Double[] weightsHO;
    private double[] hiddenBiasWeights;
    private double outputBiasWeight;

    // Tracking variables
    private int correct = 0;
    private int total = 0;
    private int noChangeCount = 0;
    private int epoch = 0;
    private double[] errors;
    private boolean errorChanged = true;
    private boolean doPrinting = true;
    private int[][] confusionMatrix = new int[2][2];

    // Data sets
    ArrayList<double[]> trainRecurrence = new ArrayList<double[]>();
    ArrayList<double[]> trainNoRecurrence = new ArrayList<double[]>();
    ArrayList<double[]> testRecurrence = new ArrayList<double[]>();
    ArrayList<double[]> testNoRecurrence = new ArrayList<double[]>();

    // Random object
    private Random random;

    // Parameters
    private final double learningRate = 0.08;
    private final double trainPercentage = 0.8;
    private final int hiddenLayerSize = 51;
    private final int maxTrainEpochs = 50;
    private final int maxNoChange = 5;
    private final double noChangeTolerance = 0.08;

    // ===== CONSTRUCTOR =====
    // =======================
    public ANN(ArrayList<double[]> dataMatrix, ArrayList<Double> outcomes, Random seededRandom) {

        this.random = seededRandom;

        initializeRandom();
        organiseData(dataMatrix, outcomes);
        printHeader();

        while (epoch < maxTrainEpochs && runTrainEpoch(dataMatrix, outcomes)) {
        } // Run epochs until the weights have sufficiently converged or the maximum
          // number of epochs has been reached

        runTestEpoch();
        printTestingHeader();
    }

    // ===== GETTERS =====
    // ===================
    /**
     * @brief Returns the accuracy of the network using the number of correct
     *        intances and the total number of instances.
     * @return accuracy
     */
    public double getAccuracy() {
        return (double) correct / total;
    }

    public double getFMeasure() {
        double precision = (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[1][0]);
        double recall = (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[0][1]);
        return 2 * ((precision * recall) / (precision + recall));
    }

    public double getBinaryPrecision() {
        return (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[1][0]);
    }

    public double getRecall() {
        return (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[0][1]);
    }

    public double getNegativeFMeasure() {
        double precision = (double) confusionMatrix[1][1] / (confusionMatrix[1][1] + confusionMatrix[0][1]);
        double recall = (double) confusionMatrix[1][1] / (confusionMatrix[1][1] + confusionMatrix[1][0]);
        return 2 * ((precision * recall) / (precision + recall));
    }

    public double getNegativePrecision() {
        return (double) confusionMatrix[1][1] / (confusionMatrix[1][1] + confusionMatrix[0][1]);
    }

    public double getNegativeRecall() {
        return (double) confusionMatrix[1][1] / (confusionMatrix[1][1] + confusionMatrix[1][0]);
    }

    // ===== TESTING AND TRAINING =====
    // ================================
    /**
     * @brief Trains the neural network on a given data set with a target value
     * 
     * @param dataSet
     * @param target
     */
    private void train(double[] dataSet, Double target) {

        for (int i = 0; i < inputLayerNodes.length; i++) {
            inputLayerNodes[i] = dataSet[i];
        }

        feedforward();
        backpropagation(target);
    }

    /**
     * @brief Tests the neural network on a given data set
     * 
     * @param dataSet
     * @param target
     */
    private void test(double[] dataSet, Double target) {

        for (int i = 0; i < inputLayerNodes.length; i++) {
            inputLayerNodes[i] = dataSet[i];
        }

        // Feedforward
        feedforward();

        // Check if the output layer node is correct and update the confusion matrix
        if (outputLayerNode >= 0.5 && target == 1.0 || outputLayerNode < 0.5 && target == 0.0) {
            this.correct++;
            if (outputLayerNode >= 0.5 && target == 1.0) {
                this.confusionMatrix[0][0]++;
            } else {
                this.confusionMatrix[1][1]++;
            }
        } else {
            if (outputLayerNode >= 0.5 && target == 0.0) {
                this.confusionMatrix[0][1]++;
            } else {
                this.confusionMatrix[1][0]++;
            }
        }

        this.total++;
    }

    /**
     * @brief Run an epoch of training on the data set.
     * 
     * @param dataMatrix
     * @param outcomes
     * @param instanceIndexs
     * @return true if the weights have sufficiently changed, false otherwise.
     */
    private boolean runTrainEpoch(ArrayList<double[]> dataMatrix, ArrayList<Double> outcomes) {

        this.epoch++;
        // this.weightChanged = false;
        this.errorChanged = false;
        boolean useRecurrence = false;

        ArrayList<double[]> currentRecurrenceSet = new ArrayList<double[]>();
        ArrayList<double[]> currentNoRecurrenceSet = new ArrayList<double[]>();

        // Fill temporary data sets which will be used to train the network
        for (int i = 0; i < trainRecurrence.size(); i++) {
            currentRecurrenceSet.add(trainRecurrence.get(i));
            currentNoRecurrenceSet.add(trainNoRecurrence.get(i));
        }

        int randomIndex = 0;
        Double target = null;
        double averageError = 0.0;

        // Train on the data set until it is empty
        while (currentRecurrenceSet.size() > 0 && currentNoRecurrenceSet.size() > 0) {
            if (useRecurrence && currentRecurrenceSet.size() > 0) {
                useRecurrence = false;
                target = 1.0;
                randomIndex = random.nextInt(currentRecurrenceSet.size());
                train(currentRecurrenceSet.get(randomIndex), target);
                currentRecurrenceSet.remove(randomIndex);
            } else if (currentNoRecurrenceSet.size() > 0) {
                useRecurrence = true;
                target = 0.0;
                randomIndex = random.nextInt(currentNoRecurrenceSet.size());
                train(currentNoRecurrenceSet.get(randomIndex), target);
                currentNoRecurrenceSet.remove(randomIndex);
            }
            averageError += Math.pow(target - outputLayerNode, 2);
        }

        // If the weights have not changed sufficiently, increment the no change count
        // if (!weightChanged) {
        if (this.errorChanged) {
            noChangeCount++;
            if (doPrinting)
                System.out.println("   " + String.format(" %-19s \u001B[31m%.4f", epoch,
                        averageError) + "\u001B[0m");
        } else {
            noChangeCount = 0;
            if (doPrinting)
                System.out.println("   " + String.format(" %-19s \u001B[32m%.4f", epoch,
                        averageError) + "\u001B[0m");
        }

        // If the weights have not changed for a number of epochs, return false
        if (noChangeCount >= maxNoChange) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @brief Run an epoch of testing on the data set.
     */
    private void runTestEpoch() {
        while (testRecurrence.size() > 0 && testNoRecurrence.size() > 0) {
            if (random.nextDouble() > 0.5 && testRecurrence.size() > 0) {
                test(testRecurrence.remove(random.nextInt(testRecurrence.size())), 1.0);
            } else {
                test(testNoRecurrence.remove(random.nextInt(testNoRecurrence.size())), 0.0);
            }
        }

        while (testNoRecurrence.size() > 0) {
            test(testNoRecurrence.remove(random.nextInt(testNoRecurrence.size())), 0.0);
        }

    }

    // ===== FEEDFORWARD AND BACKPROPAGATION =====
    // ===========================================
    /**
     * @brief Performs the feedforward on the neural network
     */
    private void feedforward() {

        // Calculate the hidden layer
        for (int i = 0; i < hiddenLayerNodes.length; i++) { // For each hidden node
            hiddenLayerNodes[i] = 0.0;
            for (int j = 0; j < inputLayerNodes.length; j++) { // For each input node
                hiddenLayerNodes[i] += inputLayerNodes[j] * weightsIH[j][i];
                // Hidden node = input node directed at it * weight of that connection
            }
            hiddenLayerNodes[i] += hiddenBias * hiddenBiasWeights[i]; // Add the bias node
            hiddenLayerNodes[i] = ReLU(hiddenLayerNodes[i]); // Activation function
        }

        outputLayerNode = 0.0;

        // Calculate the output layer
        for (int j = 0; j < hiddenLayerNodes.length; j++) { // For each hidden node (including bias node)
            outputLayerNode += hiddenLayerNodes[j] * weightsHO[j];
        }

        outputLayerNode += outputBias * outputBiasWeight; // Add the bias node
        outputLayerNode = sigmoid(outputLayerNode); // Activation function
    }

    /**
     * @brief Performs backpropagation on the network
     * 
     * @param target
     */
    private void backpropagation(Double target) {

        // Calculate the error of the output layer
        Double outputError = calcOutputError(outputLayerNode, target);

        // Update the weights of the HO connections
        for (int i = 0; i < weightsHO.length; i++) {
            weightsHO[i] += learningRate * outputError * hiddenLayerNodes[i];
        }

        // update the bias weight
        outputBiasWeight += learningRate * outputError * outputBias;

        // Calculate the error of the hidden layer
        Double hiddenError[] = new Double[hiddenLayerNodes.length];
        for (int i = 0; i < hiddenLayerNodes.length; i++) {
            hiddenError[i] = calcHiddenError(hiddenLayerNodes[i], outputError, weightsHO, i);
        }

        // Update the weights of the IH connections
        for (int i = 0; i < weightsIH.length; i++) {
            for (int j = 0; j < weightsIH[i].length; j++) {
                weightsIH[i][j] += learningRate * hiddenError[j] * inputLayerNodes[i];
            }
        }

        // update the bias weights
        for (int i = 0; i < hiddenBiasWeights.length; i++) {
            hiddenBiasWeights[i] += learningRate * hiddenError[i] * hiddenBias;
            // if (Math.abs(originalWeight - hiddenBiasWeights[i]) > noChangeTolerance) {
            // this.weightChanged = true;
            // }
        }
    }

    // ===== ACTIVATION FUNCTIONS =====
    // ================================
    /**
     * @brief ReLU activation function
     * 
     * @param n
     * @return n or 0
     */
    private double ReLU(double n) {
        // ReLU activation function
        if (n > 0) {
            return n;
        } else {
            return 0;
        }
    }

    /**
     * @brief Derivative of ReLU activation function
     * 
     * @param n
     * @return 1 or 0
     */
    private double ReLuDerivative(double n) {
        // Derivative of ReLU activation function
        if (n > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * @brief Sigmoid activation function
     * 
     * @param n
     * @return 1 / (1 + e^-n)
     */
    private double sigmoid(double n) {
        // Sigmoid activation function
        return 1 / (1 + Math.exp(-n));
    }

    /**
     * @brief Derivative of sigmoid activation function
     * 
     * @param n
     * @return n * (1 - n)
     */
    private double sigmoidDerivative(double n) {
        // Derivative of sigmoid activation function
        return n * (1 - n);
    }

    // ===== ERROR CALCULATIONS =====
    // ==============================
    /**
     * @brief alculate the error information for the output layer using the sigmoid
     *        derivative
     * 
     * @param output
     * @param target
     * @return
     */
    private double calcOutputError(Double output, Double target) {
        // (target - output) * derivative of activation function
        double outputError = (target - output) * sigmoidDerivative(output);
        return outputError;
    }

    /**
     * @brief Calculate the error information for the hidden layer using the ReLU
     * @param hidden
     * @param outputError
     * @param weightsHO
     * @param index
     * @return
     */
    private double calcHiddenError(Double hidden, Double outputError, Double[] weightsHO, int index) {
        // (outputError * weightsHO) * derivative of activation function
        double error = (outputError * weightsHO[index]) * ReLuDerivative(hidden);
        if ((error - this.errors[index]) > noChangeTolerance) {
            this.errorChanged = true;
        }
        this.errors[index] = error;
        return error;
    }

    // ===== DATA ORGANISATION =====
    // =============================
    /**
     * @brief Create the network layers and initialize the weights
     */
    private void initializeRandom() {

        inputLayerNodes = new Double[51];

        // Initalize the hidder layer
        hiddenLayerNodes = new Double[hiddenLayerSize];
        hiddenBiasWeights = new double[hiddenLayerNodes.length];
        this.errors = new double[hiddenLayerNodes.length];

        // Initialize hidden weights
        weightsIH = new Double[inputLayerNodes.length][hiddenLayerNodes.length];
        for (int i = 0; i < weightsIH.length; i++) {
            for (int j = 0; j < weightsIH[i].length; j++) {
                weightsIH[i][j] = randomInitilization();

                if (i == 0) {
                    hiddenBiasWeights[j] = randomInitilization();
                }
            }
            if (i < this.errors.length)
                this.errors[i] = 0.0;
        }

        // Initialize output weights
        weightsHO = new Double[hiddenLayerNodes.length];
        for (int i = 0; i < weightsHO.length; i++) {
            weightsHO[i] = randomInitilization();
        }

        outputBiasWeight = randomInitilization();

    }

    /**
     * @brief Returns a random gaussian number between -0.08 and 0.08. Used to
     *        initalize the weights of the network
     * @return random gaussian number
     */
    private double randomInitilization() {
        double gaussian = random.nextGaussian() * 0.08;
        return gaussian;
    }

    /**
     * @brief Sort the data into recurrence and no recurrence, training and testing
     *        sets. The training set is a percentage of the data set.
     * 
     * @param dataMatrix
     * @param outcomes
     */
    private void organiseData(ArrayList<double[]> dataMatrix, ArrayList<Double> outcomes) {

        // Sort the data into recurrence and no recurrence, training and testing sets
        ArrayList<double[]> no_recurrence = new ArrayList<double[]>();
        ArrayList<double[]> recurrence = new ArrayList<double[]>();

        for (int i = 0; i < dataMatrix.size(); i++) {
            if (outcomes.get(i) == 0.0) {
                no_recurrence.add(dataMatrix.get(i));
            } else {
                recurrence.add(dataMatrix.get(i));
            }
        }

        int initalSize = recurrence.size();
        while (trainRecurrence.size() < initalSize * this.trainPercentage) {
            int randomIndex = random.nextInt(recurrence.size());
            trainRecurrence.add(recurrence.remove(randomIndex));
            int randomIndex2 = random.nextInt(no_recurrence.size());
            trainNoRecurrence.add(no_recurrence.remove(randomIndex2));
        }

        while (recurrence.size() > 0) {
            testRecurrence.add(recurrence.remove(0));
            testNoRecurrence.add(no_recurrence.remove(0));
        }

        while (no_recurrence.size() > 0) {
            testNoRecurrence.add(no_recurrence.remove(0));
        }

    }

    // ===== PRINT FUNCTIONS =====
    // ===========================
    private void printHeader() {
        if (doPrinting) {
            System.out.println("     _    _   _ _   _ ");
            System.out.println("    / \\  | \\ | | \\ | |");
            System.out.println("   / _ \\ |  \\| |  \\| |");
            System.out.println("  / ___ \\| |\\  | |\\  |");
            System.out.println(" /_/   \\_\\_| \\_|_| \\_|");
            System.out.println("\n===== Artificial Neural Network =====");
            System.out.println("=====================================");
            System.out.println("------------ Parameters ------------- ");
            System.out.println("Learning Rate              " + this.learningRate);
            System.out.println("Hidden Layer Size          " + this.hiddenLayerSize);
            System.out
                    .println("Training Percentage        " + String.format("%.1f", (this.trainPercentage * 100))
                            + "% \u001B[2m // Percent of total \"recurrent\" instances used.\u001B[0m");
            System.out.println("Max Train Epochs           " + this.maxTrainEpochs
                    + "\u001B[2m     // ^ This is matched with equal \"non-recurrent\" instances.\u001B[0m");
            System.out.println("Max Epochs Without Change  " + this.maxNoChange);
            System.out.println("No Change Tolerance        " + this.noChangeTolerance);
            System.out.println("=====================================");
            System.out.println("------------- Training --------------");
            System.out
                    .println(String.format("  %-24s", "Datasets")
                            + (trainRecurrence.size() + trainNoRecurrence.size()));
            System.out.println("-------------------------------------");
            System.out.println("  Epoch                  Error       \n");
        }
    }

    private void printTestingHeader() {
        if (doPrinting) {
            System.out.println("=====================================");
            System.out.println("------------- Testing ---------------");

            System.out.println(String.format("%-27s", "Datasets") + total);
            System.out.println(String.format("%-44s", "\u001B[32mCorrect\u001B[0m:\u001B[31mIncorrect\u001B[32m")
                    + correct + "\u001B[0m:\u001B[31m"
                    + (total - correct) + "\u001B[0m");
            System.out.println("=====================================");
            System.out.println("------------- Findings --------------");
            System.out.println(String.format("%-26s", "| True Positive") + confusionMatrix[0][0]
                    + "\u001B[2m     // recurrence events correctly classified \u001B[0m");
            System.out.println(String.format("%-26s", "| True Negative") + confusionMatrix[1][1]
                    + "\u001B[2m    // non-recurrence events correctly classified \u001B[0m");
            System.out.println(String.format("%-26s", "| False Positive") + confusionMatrix[0][1]
                    + "\u001B[2m     // non-recurrence events incorrectly classified \u001B[0m");
            System.out.println(String.format("%-26s", "| False Negative") + confusionMatrix[1][0]
                    + "\u001B[2m      // recurrence events incorrectly classified \u001B[0m");

            System.out.println("-------------------------------------");
            System.out.println(" --- Positive ---");
            System.out.println(
                    String.format("%-26s", "Positive Precision") + String.format("%.2f", getBinaryPrecision() * 100));

            System.out.println(String.format("%-26s", "Positive Recall") + String.format("%.2f", getRecall() * 100));

            System.out
                    .println(String.format("%-26s", "Positive F-Measure") + String.format("%.2f", getFMeasure() * 100));

            System.out.println("\n --- Negative ---");
            System.out.println(
                    String.format("%-26s", "Negative Precision") + String.format("%.2f", getNegativePrecision() * 100));
            System.out.println(
                    String.format("%-26s", "Negative Recall") + String.format("%.2f", getNegativeRecall() * 100));
            System.out.println(
                    String.format("%-26s", "Negative F-Measure") + String.format("%.2f", getNegativeFMeasure() * 100));
            System.out.println("\n --- Accuracy ---");
            System.out.println(String.format("%-26s", "Accuracy") + String.format("%.2f", getAccuracy() * 100)
                    + "%");

            System.out.println("=====================================\n");
        }
    }

}
