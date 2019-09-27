import trees.Prim;
import trees.model.TreeApex;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ZPD {
    public static void main(String[] args) {
        List<TreeApex> apexes = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < 5; i++) {
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
            apexes.add(treeApex);
        }

        System.out.println("Starting values");
        for (TreeApex e : apexes) {
            System.out.println("ID: " + e.getId());
            System.out.println("Coordinates: " + e.getCoordinates());
            System.out.println("Previous apex ID: " + e.getPreviousApexId());
            System.out.println("Distance to parent: " + e.getDistanceToParent());
            System.out.println();
        }

        apexes = Prim.createMinimumSpanningTree(apexes, apexes.size());


        System.out.println("Exit values");
        for (TreeApex e : apexes) {
            System.out.println("ID: " + e.getId());
            System.out.println("Coordinates: " + e.getCoordinates());
            System.out.println("Previous apex ID: " + e.getPreviousApexId());
            System.out.println("Distance to parent: " + e.getDistanceToParent());
            System.out.println();
        }
    }
}
