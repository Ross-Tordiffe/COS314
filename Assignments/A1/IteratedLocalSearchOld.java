import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class IteratedLocalSearchOld extends Helper {

    private ArrayList<ArrayList<Integer>> instance;
    private Integer cap;
    private Double instanceFitness;
    private Integer binCount = 0;
    private AtomicLong runtime = new AtomicLong(0);

    // Constructor
    public IteratedLocalSearchOld(ArrayList<ArrayList<Integer>> instance,
            Integer cap, Integer iterations, Integer swaps, Integer reshuffles) {

        this.instance = instance;
        this.cap = cap;

        instanceFitness = Fitness(instance);

        // start timer
        long startTime = System.currentTimeMillis();

        // INITIAL SOLUTION
        bestFit();

        // PETURBATION + LOCAL SEARCH
        for (int j = 0; j < iterations; j++) {

            for (int i = 0; i < swaps; i++) {
                Swap(); // Local Search done after each swap
            }

            // CONSTRUCT A NEW SOLUTION
            bestFit();
            for (int i = 0; i < reshuffles; i++) {
                reshuffleSmallest();
            }
        }

        runtime = new AtomicLong((System.currentTimeMillis() - startTime));
        binCount = this.instance.size();

    }

    /**
     * Returns the runtime of the algorithm
     */
    public long getRuntime() {
        return runtime.get();
    }

    /**
     * Returns bin count of the instance
     */
    public Integer getBinCount() {
        return binCount;
    }

    /**
     * Best Fit Algorithm
     */
    public void bestFit() {

        ArrayList<ArrayList<Integer>> bestList = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < instance.size(); i++) {
            for (int j = 0; j < instance.get(i).size(); j++) {
                Integer value = instance.get(i).get(j);
                Integer bestIndex = 0;
                if (bestList.size() == 0) {
                    ArrayList<Integer> newList = new ArrayList<Integer>();
                    newList.add(value);
                    bestList.add(newList);
                    continue;
                }
                Integer bestSum = sumBin(bestList.get(0)) + value;
                for (int k = 1; k < bestList.size(); k++) {
                    Integer sum = sumBin(bestList.get(k)) + value;
                    if (sum < bestSum) {
                        bestSum = sum;
                        bestIndex = k;
                    }
                }
                if (bestSum <= cap) {
                    bestList.get(bestIndex).add(value);
                } else {
                    ArrayList<Integer> newList = new ArrayList<Integer>();
                    newList.add(value);
                    bestList.add(newList);
                }
            }
        }

        if (Fitness(bestList) < Fitness(instance)) {
            instance = bestList;
        }

    }

    /**
     * Finds the best bin to insert a value into the instance
     * 
     * @param value
     */
    public void bestInsert(Integer value) {

        Integer bestIndex = 0;
        Integer bestSum = sumBin(instance.get(0)) + value;
        for (int i = 1; i < instance.size(); i++) {
            Integer sum = sumBin(instance.get(i)) + value;
            if (sum < bestSum) {
                bestSum = sum;
                bestIndex = i;
            }
        }
        if (bestSum <= cap) {
            instance.get(bestIndex).add(value);
        } else {
            ArrayList<Integer> newList = new ArrayList<Integer>();
            newList.add(value);
            instance.add(newList);
        }

    }

    /*
     * Finds the bin with the smallest sum and removes it, then reshuffles the
     * values back into the instance
     */
    public void reshuffleSmallest() {

        Integer smallestIndex = 0;
        Integer smallestSum = sumBin(instance.get(0));
        for (int i = 1; i < instance.size(); i++) {
            Integer sum = sumBin(instance.get(i));
            if (sum < smallestSum) {
                smallestSum = sum;
                smallestIndex = i;
            }
        }

        ArrayList<Integer> smallestBin = instance.get(smallestIndex);
        // Swap the smallest bin with the last bin
        instance.set(smallestIndex, instance.get(instance.size() - 1));
        instance.set(instance.size() - 1, smallestBin);
        // Remove the smallest bin
        instance.remove(instance.size() - 1);

        for (int i = 0; i < smallestBin.size(); i++) {
            bestInsert(smallestBin.get(i));
        }
    }

    /**
     * Finds a random bin and removes it, then reshuffles the values back into the
     */
    public void reshuffleRandom() {

        Integer randomIndex = (int) (Math.random() * instance.size());

        ArrayList<Integer> randomBin = instance.get(randomIndex);
        // Swap with the last bin
        instance.set(randomIndex, instance.get(instance.size() - 1));
        instance.set(instance.size() - 1, randomBin);
        // Remove the random bin
        instance.remove(instance.size() - 1);

        for (int i = 0; i < randomBin.size(); i++) {
            bestInsert(randomBin.get(i));
        }

    }

    /**
     * Swap two random items in the instance
     * 
     * @param instance
     */
    public void Swap() {

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

            Double newFitness = Fitness(instance);

            if (newFitness <= instanceFitness) {
                instanceFitness = newFitness;
            } else { // LOCAL SEARCH
                item1.set(valueIndex1, value1);
                item2.set(valueIndex2, value2);
            }
        }

    }

}
