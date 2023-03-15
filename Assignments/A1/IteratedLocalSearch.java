import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class IteratedLocalSearch extends Helper {

    private HashMap<String, ArrayList<ArrayList<Integer>>> files;
    private Integer cap;

    private ArrayList<Double> instanceFitness = new ArrayList<Double>();

    // Constructor
    public IteratedLocalSearch(HashMap<String, ArrayList<ArrayList<Integer>>> files,
            Integer cap) {

        this.files = files;
        this.cap = cap;

        // For each instance in the file run 10 swaps and 10 swaps from lowest until
        // there are 40 or less bin or the swaps are done
        int index = 0;
        // for (String key : files.keySet()) {
        // ArrayList<ArrayList<Integer>> instance = files.get(key);
        // Integer count = 0;
        // instanceFitness.add(Fitness(instance));

        // for (int i = 0; i < 10; i++) {
        // while (count < 50 && instance.size() > 40) {
        // Swap(instance, index);
        // count++;
        // }

        // instance = unpackRepack(instance);
        // }

        index++;
        // }

    }

    /**
     * Swap two random items in the instance
     * 
     * @param instance
     */
    public void Swap(ArrayList<ArrayList<Integer>> instance, Integer instanceIndex) {

        Integer index1 = (int) (Math.random() * instance.size());
        Integer index2 = (int) (Math.random() * instance.size());

        while (index1 == index2) {
            index2 = (int) (Math.random() * instance.size());
        }

        ArrayList<Integer> item1 = instance.get(index1);
        ArrayList<Integer> item2 = instance.get(index2);

        Integer valueIndex1 = (int) (Math.random() * item1.size());
        Integer valueIndex2 = (int) (Math.random() * item2.size());

        Integer value1 = item1.get(valueIndex1);
        Integer value2 = item2.get(valueIndex2);

        Integer sum1 = sumBin(item1) - value1 + value2;
        Integer sum2 = sumBin(item2) - value2 + value1;

        if (sum1 <= cap && sum2 <= cap) {
            item1.set(valueIndex1, value2);
            item2.set(valueIndex2, value1);

            if (Fitness(instance) < instanceFitness.get(instanceIndex)
                    || Fitness(instance) == instanceFitness.get(instanceIndex)) {
                instanceFitness.set(instanceIndex, Fitness(instance));
            } else {
                item1.set(valueIndex1, value1);
                item2.set(valueIndex2, value2);
            }
        }

    }

    public ArrayList<ArrayList<Integer>> unpackRepack(ArrayList<ArrayList<Integer>> instance) {

        ArrayList<Integer> unpacked = new ArrayList<Integer>();
        for (int i = 0; i < instance.size(); i++) {
            unpacked.addAll(instance.get(i));
            // instance.get(i).clear();
        }

        return bestFitDecreasing(unpacked, cap);

    }

    public Double Fitness(ArrayList<ArrayList<Integer>> instance) {
        Double fitness = 0.0;
        // Calculate the fitness of the instance
        for (int i = 0; i < instance.size(); i++) {
            fitness += Math.pow(sumBin(instance.get(i)), 2);
        }
        return (1 - fitness / instance.size());
    }

}
