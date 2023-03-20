import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

public class IteratedLocalSearch extends Helper {

    private ArrayList<ArrayList<Integer>> instance;
    private String instanceName;
    private Integer cap;
    private Double instanceFitness;

    // Constructor
    public IteratedLocalSearch(ArrayList<ArrayList<Integer>> instance, String instanceName,
            Integer cap) {

        this.instance = instance;
        this.instanceName = instanceName;
        this.cap = cap;

        instanceFitness = Fitness(instance);

        bestFit();

        for (int j = 0; j < 1; j++) {
            // for (int i = 0; i < 50; i++) {
            //     Swap();
            // }
            // bestFit();
            // for (int i = 0; i < 10; i++) {
            // reshuffleSmallest();
            // }
        }

        HashMap<String, ArrayList<ArrayList<Integer>>> results = new HashMap<String, ArrayList<ArrayList<Integer>>>();
        results.put(this.instanceName, this.instance);
        printInstance(results);

    }

    /**
     * Best Fit Algorithm
     */
    public void bestFit() {

        System.out.println("Previous: " + Fitness(instance));

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

        System.out.println("Best Fit: " + Fitness(bestList));

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
            } else {
                item1.set(valueIndex1, value1);
                item2.set(valueIndex2, value2);
            }
        }

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
