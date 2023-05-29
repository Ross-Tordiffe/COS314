public class ANN {

    // Artificial Neural Network with 3 layers and 2 nodes at each layer excluding 1
    // bias node.
    // Uses backpropagation to train the network.

    private double[] inputLayer = new double[3]; // 2 input nodes and 1 bias node.
    private double[] hiddenLayer = new double[3]; // 2 hidden nodes and 1 bias node
    private double[] outputLayer = new double[2];

    private double[][] weightsIH = new double[3][2];
    private double[][] weightsHO = new double[3][2];

    private double learningRate = 0.8;

    private double[] target = new double[2];

    public ANN() {

        // Initialize the nodes
        inputLayer[0] = 1.0; // 0 is Bias node
        inputLayer[1] = 0.35;
        inputLayer[2] = 0.4;

        hiddenLayer[0] = 1.0; // 0 is Bias node
        hiddenLayer[1] = 0.0; // Not calculated yet
        hiddenLayer[2] = 0.0; // Not calculated yet

        outputLayer[0] = 0.0; // Not calculated yet
        outputLayer[1] = 0.0; // Not calculated yet

        // Initialize the weights
        weightsIH[0][0] = 0.4; // Bias to H1
        weightsIH[0][1] = 0.7; // Bias to H2
        weightsIH[1][0] = 0.2; // I1 to H1
        weightsIH[1][1] = 0.3; // I1 to H2
        weightsIH[2][0] = -0.3; // I2 to H1
        weightsIH[2][1] = 0.8; // I2 to H2

        weightsHO[0][0] = 0.1; // Bias to O1
        weightsHO[0][1] = 0.3; // Bias to O2

        weightsHO[1][0] = 0.7; // H1 to O1
        weightsHO[1][1] = 0.25; // H1 to O2
        weightsHO[2][0] = 0.25; // H2 to O1
        weightsHO[2][1] = 0.6; // H2 to O2

        // Initialize the target
        target[0] = 0.7;
        target[1] = 0.5;

    }

    public void train() {

        printNetwork();

        // Feedforward
        feedforward();

        // Backpropagation
        backpropagation();

        printNetwork();

    }

    public void feedforward() {

        // Calculate the hidden layer
        for (int i = 1; i < hiddenLayer.length; i++) { // For each hidden node (excluding bias node)
            for (int j = 0; j < inputLayer.length; j++) { // For each input node (including bias node)
                hiddenLayer[i] += inputLayer[j] * weightsIH[j][i - 1];
                // Hidden node = input node directed at it * weight of that connection
            }
            hiddenLayer[i] = activation(hiddenLayer[i]); // Activation function
            System.out.println("Hidden Layer " + i + ": " + hiddenLayer[i]);
        }

        // Calculate the output layer
        for (int i = 0; i < outputLayer.length; i++) { // For each output node
            for (int j = 0; j < hiddenLayer.length; j++) { // For each hidden node (including bias node)
                outputLayer[i] += hiddenLayer[j] * weightsHO[j][i];
            }
            outputLayer[i] = activation(outputLayer[i]);
            System.out.println("Output Layer " + i + ": " + outputLayer[i]);
        }
    }

    public void backpropagation() {

        // Calculate the error of the output layer
        double outputError[] = new double[outputLayer.length];
        for (int i = 0; i < outputLayer.length; i++) {
            outputError[i] = calcErrorOutput(outputLayer[i], target[i]);
            System.out.println("Output Error " + i + ": " + outputError[i]);
        }

        // Update the weights of the HO connections
        for (int i = 0; i < weightsHO.length; i++) {
            for (int j = 0; j < weightsHO[i].length; j++) {
                weightsHO[i][j] += learningRate * outputError[j] * hiddenLayer[i]; // originalWeight + learningRate *
                                                                                   // error * input
            }
            System.out.println("HO Weight " + i + ": " + weightsHO[i][0] + " " + weightsHO[i][1]);
        }

        // Calculate the error of the hidden layer
        double hiddenError[] = new double[hiddenLayer.length];
        for (int i = 1; i < hiddenLayer.length; i++) {
            hiddenError[i] = calcErrorHidden(hiddenLayer[i], outputError, weightsHO, i);
            System.out.println("Hidden Error " + i + ": " + hiddenError[i]);
        }

        // Update the weights of the IH connections
        for (int i = 0; i < weightsIH.length; i++) {
            for (int j = 0; j < weightsIH[i].length; j++) {
                weightsIH[i][j] += learningRate * hiddenError[j] * inputLayer[i]; // originalWeight + learningRate *
                                                                                  // error * input
            }
            System.out.println("IH Weight " + i + ": " + weightsIH[i][0] + " " + weightsIH[i][1]);
        }

    }

    public double calcErrorOutput(double output, double target) {
        // (target - output) * derivative of activation function == (target - output) *
        // output * (1 - output)
        return output * (1 - output) * (target - output);
    }

    public double calcErrorHidden(double hidden, double[] outputError, double[][] weightsHO, int index) {
        // (outputError * weightsHO) * derivative of activation function == (outputError
        // * weightsHO) * hidden * (1 - hidden)
        double error = 0;
        for (int i = 0; i < outputError.length; i++) {
            error += outputError[i] * weightsHO[index][i];
        }
        return hidden * (1 - hidden) * error;
    }

    public double activation(double n) {
        // 1 / 1 (1+e^-x) // aka sigmoid
        return 1 / (1 + Math.exp(-n));
    }

    public void printNetwork() {

        System.out.println("Input Layer");
        for (int i = 0; i < inputLayer.length; i++) {
            System.out.println("i" + i + ": " + String.format("%.5f", inputLayer[i]));
        }

        System.out.println("Hidden Layer");
        for (int i = 1; i < hiddenLayer.length; i++) {
            System.out.println("h" + i + ": " + String.format("%.5f", hiddenLayer[i]));
        }

        System.out.println("Output Layer");
        for (int i = 0; i < outputLayer.length; i++) {
            System.out.println("o" + i + ": " + String.format("%.5f", outputLayer[i]) + " -> "
                    + String.format("%.5f", (target[i] - outputLayer[i])) + " error");
        }

        System.out.println("Weights IH");
        for (int i = 0; i < weightsIH.length; i++) {
            for (int j = 0; j < weightsIH[i].length; j++) {
                System.out.println("w" + i + "" + j + ": " + String.format("%.5f", weightsIH[i][j]));
            }
        }

        System.out.println("Weights HO");
        for (int i = 0; i < weightsHO.length; i++) {
            for (int j = 0; j < weightsHO[i].length; j++) {
                System.out.println("w" + i + "" + j + ": " + String.format("%.5f", weightsHO[i][j]));
            }
        }

    }

}
