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

        for(int j = 0; j < 50; j++) {
            for (int i = 0; i < 50; i++) {
                Swap();
            }
            bestFit();
        }

        HashMap<String, ArrayList<ArrayList<Integer>>> results = new HashMap<String, ArrayList<ArrayList<Integer>>>();
        results.put(this.instanceName, this.instance);
        printInstance(results);

    }

    /**
     * 
     */
    public void bestFit() {

        System.out.println("Previous: " + Fitness(instance));
        
        ArrayList<ArrayList<Integer>> bestList = new ArrayList<ArrayList<Integer>>();

        for (int i = 0; i < instance.size(); i++) {
            for(int j = 0; j < instance.get(i).size(); j++) {
                Integer value = instance.get(i).get(j);
                Integer bestIndex = 0;
                if(bestList.size() == 0) {
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

        if(Fitness(bestList) < Fitness(instance)) {
            instance = bestList;
        }

        // HashMap<String, ArrayList<ArrayList<Integer>>> results = new HashMap<String, ArrayList<ArrayList<Integer>>>();
        // results.put(instanceName, instance);
        // printInstance(results);
        
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
