import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class Tabu extends Helper {

    private ArrayList<ArrayList<ArrayList<Integer>>> tabuList = new ArrayList<ArrayList<ArrayList<Integer>>>();
    private ArrayList<ArrayList<Integer>> instance;
    private Integer cap;
    private Integer TABU_SIZE = 100;
    private Double instanceFitness;

    private Integer binCount = 0;
    private AtomicLong runtime = new AtomicLong(0);

    // Constructor
    public Tabu(ArrayList<ArrayList<Integer>> instance,
            Integer cap, Integer iterations, Integer swaps, Integer reshuffles) {

        this.instance = instance;
        this.cap = cap;
        instanceFitness = Fitness(instance);

        // start timer
        long startTime = System.currentTimeMillis();

        // INITIAL SOLUTION
        bestFit(); // Items are arranged in decreasing order of size, and then packed into bins
                   // using first fit decreasing

        // PETURBATION + LOCAL SEARCH
        for (int j = 0; j < iterations; j++) {
            for (int i = 0; i < swaps; i++) {
                Swap(); // Has a check within it to see if the new solution is tabu. Will not swap
                        // if it is tabu
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
     * Uses first fit decreasing to sort the instance
     */
    public void greedySort() {

        // Sort the items from largest to smallest
        ArrayList<Integer> sortedItems = new ArrayList<Integer>();
        for (ArrayList<Integer> bin : this.instance) {
            for (Integer item : bin) {
                sortedItems.add(item);
            }
        }
        sortedItems.sort((a, b) -> b - a);

        // Sort the items into bins using first fit decreasing
        this.instance = firstFitDecreasing(sortedItems, cap);

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

        // deep copy the instance
        ArrayList<ArrayList<Integer>> previousInstance = new ArrayList<ArrayList<Integer>>();

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

            if (inTabuList(instance)) {
                item1.set(valueIndex1, value1);
                item2.set(valueIndex2, value2);
                return;
            }

            Double newFitness = Fitness(instance);

            if (newFitness <= instanceFitness) {
                instanceFitness = newFitness;
                addTabu(instance);
            } else {
                item1.set(valueIndex1, value1);
                item2.set(valueIndex2, value2);
            }

        }

    }

    /**
     * Returns a deep copy of the instance
     */
    public ArrayList<ArrayList<Integer>> copyInstance() {
        ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < instance.size(); i++) {
            ArrayList<Integer> bin = new ArrayList<Integer>();
            for (int j = 0; j < instance.get(i).size(); j++) {
                bin.add(instance.get(i).get(j));
            }
            copy.add(bin);
        }
        return copy;

    }

    /**
     * Returns true if the instance is has equal values to an instance in the tabu
     * 
     * @param instance
     * @return
     */
    public boolean inTabuList(ArrayList<ArrayList<Integer>> instance) {
        for (int i = 0; i < tabuList.size(); i++) {
            if (tabuList.get(i).size() == instance.size()) {
                boolean equal = true;
                for (int j = 0; j < instance.size(); j++) {
                    if (tabuList.get(i).get(j).size() != instance.get(j).size()) {
                        equal = false;
                        break;
                    }
                    for (int k = 0; k < instance.get(j).size(); k++) {
                        if (tabuList.get(i).get(j).get(k) != instance.get(j).get(k)) {
                            equal = false;
                            break;
                        }
                    }
                }
                if (equal) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds a new instance to the tabu list
     * 
     * @return
     */
    public void addTabu(ArrayList<ArrayList<Integer>> instance) {
        tabuList.add(copyInstance());
        if (tabuList.size() > TABU_SIZE) {
            tabuList.remove(0);
        }
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

}
