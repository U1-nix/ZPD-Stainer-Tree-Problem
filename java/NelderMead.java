import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NelderMead {
    private static double alpha = 1;
    private static double beta;
    private static double gamma;
    private static int n = 2;

    public static List<Apex> minimise(Apex p0) {
        List<Apex> startingValues = new ArrayList<>(initialize(p0));

        for (Apex e : startingValues) {
            Function.testFunction(e);
        }

        Apex maxApex = findMaxFunctionValue(startingValues);
        Apex secondMaxApex = findSecondMaxApex(startingValues);
        Apex minApex = findMinApex(startingValues);

        debug(maxApex, secondMaxApex, minApex);

        Apex centerOfGravity = new Apex(findCenterOfGravity(secondMaxApex.getCoordinates(), minApex.getCoordinates()),
                0.0);
        System.out.println("center of gravity coordinates: " + centerOfGravity.getCoordinates());
        Function.testFunction(centerOfGravity);
        System.out.println("center of gravity function value: " + centerOfGravity.getFunctionValue());

        Apex reflectedApex = new Apex(reflect(centerOfGravity.getCoordinates(), maxApex.getCoordinates()), 0.00);
        System.out.println("reflected apex coords: " + reflectedApex.getCoordinates());
        Function.testFunction(reflectedApex);
        System.out.println("reflected apex function value: " + reflectedApex.getFunctionValue());

        comparison(reflectedApex, minApex, centerOfGravity, maxApex, secondMaxApex);

        return startingValues;
    }

    private static void comparison(Apex reflectedApex, Apex minApex, Apex centerOfGravity, Apex maxApex, Apex secondMaxApex) {
        if (reflectedApex.getFunctionValue() < minApex.getFunctionValue()) {
            //right way
            Apex strainedApex = new Apex(findStrainedApex(reflectedApex.getCoordinates(), centerOfGravity.getCoordinates()),
                    0.00);
            Function.testFunction(strainedApex);
            if (strainedApex.getFunctionValue() < minApex.getFunctionValue()) {
                // very good result
                maxApex = (Apex) strainedApex.clone();
                // precision check
                // if TRUE stop
                // else find max, secondMax, min
            } else {
                if (strainedApex.getFunctionValue() >= minApex.getFunctionValue()) {
                    // too far
                    maxApex = (Apex) reflectedApex.clone();
                    // precision check
                    // if TRUE stop
                    // else findCenterOfGravity
                }
            }
        } else {
            if (reflectedApex.getFunctionValue() > minApex.getFunctionValue()
                    && reflectedApex.getFunctionValue() <= secondMaxApex.getFunctionValue()) {
                maxApex = (Apex) reflectedApex.clone();
                // precision check
                // if TRUE stop
                // else find max, secondMax, min
            } else {
                if (reflectedApex.getFunctionValue() < maxApex.getFunctionValue()) {
                    maxApex = (Apex) reflectedApex.clone();
                }
                // reflectedApex.getFunctionValue > maxApex.getFunctionValue
                // too far
                Apex compressedApex = new Apex(findCompressedApex(maxApex, centerOfGravity), 0.00);
                Function.testFunction(compressedApex);
            }
        }
    }

    private static List<Double> findCompressedApex(Apex maxApex, Apex centerOfGravity) {
        List<Double> compressedCoordinates = new ArrayList<>();
        for (int i = 0; i < maxApex.getCoordinates().size(); i++) {
            double result = beta * maxApex.getCoordinates().get(i) + (1 - beta) * centerOfGravity.getCoordinates().get(i);
            compressedCoordinates.add(result);
        }
        return compressedCoordinates;
    }

    private static List<Double> findStrainedApex(List<Double> reflectedApexCoordinates, List<Double> centerOfGravityCoordinates) {
        List<Double> strainedCoordinates = new ArrayList<>();
        for (int i = 0; i < centerOfGravityCoordinates.size(); i++) {
            double result = gamma * reflectedApexCoordinates.get(i) + (1 - gamma) * centerOfGravityCoordinates.get(i);
            strainedCoordinates.add(result);
        }
        return strainedCoordinates;
    }

    private static void debug(Apex maxApex, Apex secondMaxApex, Apex minApex) {
        System.out.println("max apex coords: " + maxApex.getCoordinates());
        System.out.println("max apex function value: " + maxApex.getFunctionValue());
        System.out.println("second max apex coords: " + secondMaxApex.getCoordinates());
        System.out.println("second max apex function value: " + secondMaxApex.getFunctionValue());
        System.out.println("min apex coords: " + minApex.getCoordinates());
        System.out.println("min apex function value: " + minApex.getFunctionValue());
    }

    private static List<Double> reflect(List<Double> centerOfGravityCoordinates, List<Double> maxApexCoordinates) {
        List<Double> reflectedCoordinates = new ArrayList<>();
        for (int i = 0; i < centerOfGravityCoordinates.size(); i++) {
            double result = (1 + alpha) * centerOfGravityCoordinates.get(i) - alpha * maxApexCoordinates.get(i);
            reflectedCoordinates.add(result);
        }
        return reflectedCoordinates;
    }

    private static List<Double> findCenterOfGravity(List<Double> xg, List<Double> xl) {
        List<Double> centerOfGravity = new ArrayList<>();
        for (int j = 0; j < xg.size(); j++) {
            double sum = 0;
            sum += xg.get(j) + xl.get(j);
            centerOfGravity.add(sum / n);
        }
        return centerOfGravity;
    }

    private static List<Apex> initialize(Apex p0) {
        List<Apex> startingValues = new LinkedList<>();
        startingValues.add(p0);
        for (int i = 1; i <= n; i++) {
            Apex p = (Apex) p0.clone();
            double x = p.getCoordinates().get(i - 1);
            p.getCoordinates().remove(i - 1);
            x++;
            p.getCoordinates().add(i - 1, x);
            startingValues.add(p);
        }
        return startingValues;
    }

    private static Apex findMaxFunctionValue(List<Apex> startingValues) {
        Apex max = startingValues.get(0);
        for (Apex e : startingValues) {
            if (max.getFunctionValue() < e.getFunctionValue()) {
                max = e;
            }
        }
        return max;
    }

    private static Apex findSecondMaxApex(List<Apex> startingValues) {
        Apex max = startingValues.get(0);
        Apex secondMax = startingValues.get(0);
        for (Apex e : startingValues) {
            if (max.getFunctionValue() < e.getFunctionValue()) {
                secondMax = max;
                max = e;
            } else if (e.getFunctionValue() >= secondMax.getFunctionValue() && e.getFunctionValue() == max.getFunctionValue()) {
                secondMax = e;
            }
        }
        return secondMax;
    }

    private static Apex findMinApex(List<Apex> startingValues) {
        Apex min = startingValues.get(0);
        for (Apex e : startingValues) {
            if (min.getFunctionValue() > e.getFunctionValue()) {
                min = e;
            }
        }
        return min;
    }
}