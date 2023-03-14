import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Helper {
    public static void main(String[] args) {

        String[] fileNames = {
                "Falkenauer/Falkenauer_T", "Falkenauer/Falkenauer_U",
                "Hard28",
                "Scholl/Scholl_1", "Scholl/Scholl_2", "Scholl/Scholl_3",
                "Schwerin/Schwerin_1", "Schwerin/Schwerin_2",
                "Waescher"
        };

        // Use Falkenauer_T to test
        HashMap<String, ArrayList<Integer>> valuesFalkenauerT = readFiles(fileNames[0]);
        Integer capFalkenauerT = getCap(valuesFalkenauerT);
        // printValues(valuesFalkenauerT);

        HashMap<String, ArrayList<ArrayList<Integer>>> instanceFalkenauerT = organiseInstances(valuesFalkenauerT,
                capFalkenauerT);
        printInstance(instanceFalkenauerT);

        // IteratedLocalSearch ils = new IteratedLocalSearch(instanceFalkenauerT,
        // capFalkenauerT);

    }
}
