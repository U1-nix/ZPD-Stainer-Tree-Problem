import nelderMead.NelderMead;
import trees.Debug;
import trees.Tools;
import trees.model.TreeApex;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ZPD {
    public static void main(String[] args) {
        List<TreeApex> apexes = new ArrayList<>(initializeTreeApexes());

        System.out.println("Starting values");
        Debug.showTreeApexes(apexes);

        //Prim.createMinimumSpanningTree(apexes);
        List<Double> newCoords = new ArrayList<>(NelderMead.minimise(prepareCoordinates(apexes), apexes));
        System.out.println("new coords: " + newCoords + "\n");

        System.out.println("Exit values");
        Debug.showTreeApexes(apexes);
        System.out.println("\n" + "Tree length: " + Tools.calculateTreeLength(apexes));

    }

    public static List<TreeApex> initializeTreeApexes() {
        List<TreeApex> apexes = new ArrayList<>();
        int idCounter = initializeMainTreeApexes(apexes);
        initializeAdditionalTreeApexes(apexes, idCounter);
        return apexes;
    }

    private static int initializeMainTreeApexes(List<TreeApex> apexes) {
        Scanner scanner = new Scanner(System.in);
        int idCounter = 0;
        int numberOfMainApexes = scanner.nextInt();
        for (int i = 0; i < numberOfMainApexes; i++) {
            TreeApex treeApex = new TreeApex();
            treeApex.setId(i);
            // -1 means no next tree apex
            if (i == 0) {
                // -2 means first tree apex
                treeApex.setPreviousApexId(-2);
            } else {
                treeApex.setPreviousApexId(-1);
            }
            treeApex.setConnectedFurther(false);
            treeApex.setDistanceToParent(Double.MAX_VALUE);
            List<Double> coordinates = new ArrayList<>();
            // userInput - coordinate in String format
            String userInput = scanner.next();
            double coordinate = Double.parseDouble(userInput);
            coordinates.add(coordinate);
            userInput = scanner.next();
            coordinate = Double.parseDouble(userInput);
            coordinates.add(coordinate);
            treeApex.setCoordinates(coordinates);
            treeApex.setAdditional(false);
            apexes.add(treeApex);
            idCounter++;
        }
        return idCounter;
    }

    private static void initializeAdditionalTreeApexes(List<TreeApex> apexes, int idCounter) {
//        Scanner scanner = new Scanner(System.in);
//        int numberOfAdditionalApexes = scanner.nextInt();
//        for (int i = 0; i < numberOfAdditionalApexes; i++) {
//            TreeApex treeApex = new TreeApex();
//            treeApex.setId(idCounter);
//            treeApex.setPreviousApexId(-1);
//            treeApex.setConnectedFurther(false);
//            treeApex.setDistanceToParent(Double.MAX_VALUE);
//            List<Double> coordinates = new ArrayList<>();
//
//            double rangeMin = -5.0;
//            double rangeMax = 5.0;
//            Random r = new Random();
//            double randomValueX = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
//            double randomValueY = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
//
//            coordinates.add(i+randomValueX);
//            coordinates.add(i+randomValueY);
//            treeApex.setCoordinates(coordinates);
//            treeApex.setAdditional(true);
//            apexes.add(treeApex);
//            idCounter++;
//        }

        TreeApex treeApex1 = new TreeApex();
        List<Double> coordinates1 = new ArrayList<>();
        coordinates1.add(3000.5);
        coordinates1.add(600.5);
        treeApex1.setCoordinates(coordinates1);
        treeApex1.setId(4);
        treeApex1.setPreviousApexId(-1);
        treeApex1.setConnectedFurther(false);
        treeApex1.setDistanceToParent(Double.MAX_VALUE);
        treeApex1.setAdditional(true);
        apexes.add(treeApex1);

        TreeApex treeApex2 = new TreeApex();
        List<Double> coordinates2 = new ArrayList<>();
        coordinates2.add(1000.0);
        coordinates2.add(200.5);
        treeApex2.setCoordinates(coordinates2);
        treeApex2.setId(5);
        treeApex2.setPreviousApexId(-1);
        treeApex2.setConnectedFurther(false);
        treeApex2.setDistanceToParent(Double.MAX_VALUE);
        treeApex2.setAdditional(true);
        apexes.add(treeApex2);
    }

    public static List<Double> prepareCoordinates(List<TreeApex> apexes) {
        List<Double> coordinates = new ArrayList<>();
        for (TreeApex e : apexes) {
            if (e.isAdditional()) {
                coordinates.add(e.getCoordinates().get(0));
                coordinates.add(e.getCoordinates().get(1));
            }
        }
        return coordinates;
    }
}
