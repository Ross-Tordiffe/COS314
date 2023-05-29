public class Main {
    public static void main(String[] args) {
        // Create a new instance of ANN
        ANN ann = new ANN();
        // Call the train method
        for (int i = 0; i < 100; i++)
            ann.train();
    }
}