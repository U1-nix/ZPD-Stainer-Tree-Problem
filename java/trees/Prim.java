package trees;

import trees.model.TreeApex;

import java.util.ArrayList;
import java.util.List;

public class Prim {

    public static List<TreeApex> createMinimumSpanningTree(List<TreeApex> apexes, int numberOfApexes) {
        for (int k = 0; k < numberOfApexes; k++) {
            List<Distance> minDistances = new ArrayList<>();
            for (int i = 0; i < apexes.size() - 1; i++) {
                List<Distance> distances = new ArrayList<>();
                if (apexes.get(i).getPreviousApexId() != -1) {
                    for (TreeApex apex : apexes) {
                        if (apex.getPreviousApexId() == -1) {
                            double distance = measureDistance(apexes.get(i), apex);
                            if (distance < apex.getDistanceToParent()) {
                                Distance distance1 = new Distance();
                                distance1.distanceToParent = distance;
                                distance1.startingApexId = apex.getId();
                                distance1.endingApexId = apexes.get(i).getId();
                                distances.add(distance1);
                            }
                        }
                    }
                    minDistances.add(findMinDistance(distances));
                }
            }
            Distance minDistance = findMinDistance(minDistances);
            for (TreeApex e : apexes) {
                if (e.getId() == minDistance.endingApexId) {
                    e.setConnectedFurther(true);
                    break;
                }
            }
            for (TreeApex e : apexes) {
                if (e.getId() == minDistance.startingApexId) {
                    e.setPreviousApexId(minDistance.endingApexId);
                    e.setDistanceToParent(minDistance.distanceToParent);
                    break;
                }
            }
        }
        return apexes;
    }

    private static Distance findMinDistance(List<Distance> distances) {
        Distance minDistance = new Distance();
        minDistance.distanceToParent = Double.MAX_VALUE;
        for (Distance e : distances) {
            if (e.distanceToParent < minDistance.distanceToParent) {
                minDistance = e;
            }
        }
        return minDistance;
    }

    private static double measureDistance(TreeApex firstTreeApex, TreeApex secondTreeApex) {
        List<Double> coordinates = new ArrayList<>();
        for (int i = 0; i < firstTreeApex.getCoordinates().size(); i++) {
            coordinates.add(Math.abs(firstTreeApex.getCoordinates().get(i) - secondTreeApex.getCoordinates().get(i)));
        }
        double length = 0.0;
        for (double e : coordinates) {
            e = Math.pow(e, 2);
            length += e;
        }
        return length;
    }

    static class Distance {
        double distanceToParent;
        int startingApexId;
        int endingApexId;
    }
}
