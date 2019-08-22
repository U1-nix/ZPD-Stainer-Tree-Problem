import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NelderMead {
    private static double alpha = 1;
    private static double beta = 0.5;
    private static double gamma = 2;
    private static int n = 2;
    private static double epsilon = 0.1;

    public static Apex minimise(Apex p0) {
        List<Apex> startingValues = new ArrayList<>(initialize(p0));

        for (Apex e : startingValues) {
            e.setFunctionValue(Function.testFunction(e));
        }

        List<Apex> minimum = new ArrayList<>();
        while (true) {
            Apex maxApex = findMaxApex(startingValues);
            Apex secondMaxApex = findSecondMaxApex(startingValues);
            Apex minApex = findMinApex(startingValues);
            debugMain(maxApex, secondMaxApex, minApex);

            List<Apex> action = deeperMinimise(maxApex, secondMaxApex, minApex);
            startingValues.clear();
            startingValues.add(action.get(1));
            startingValues.add(action.get(2));
            startingValues.add(action.get(3));

            if (action.get(0).getFunctionValue() == 1.0) {
                minimum.add(action.get(3));
                break;
            }
        }

        return minimum.get(0);
    }

    private static List<Apex> deeperMinimise(Apex maxApex, Apex secondMaxApex, Apex minApex) {
        //int action = 1;
        Apex action = new Apex(new ArrayList<>(), 0);         //1 - full stop
        List<Apex> list = new ArrayList<>();
        list.add(action);
        list.add(maxApex);
        list.add(secondMaxApex);
        list.add(minApex);
        while (true) {
            Apex centerOfGravity = initializeCenterOfGravity(secondMaxApex.getCoordinates(), minApex.getCoordinates());
            Apex reflectedApex = initializeReflectedApex(centerOfGravity.getCoordinates(), maxApex.getCoordinates());
            debugAdditional(centerOfGravity, reflectedApex);

            if (reflectedApex.getFunctionValue() < minApex.getFunctionValue()) {
                //right way
                Apex strainedApex = initializeStrainedApex(reflectedApex.getCoordinates(), centerOfGravity.getCoordinates());
                if (strainedApex.getFunctionValue() < minApex.getFunctionValue()) {
                    // very good result
                    maxApex = strainedApex.clone();
                    list.set(1, maxApex);
                    if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                        //action = 1;
                        //action.setFunctionValue(1);
                        list.get(0).setFunctionValue(1);
                    }
                    break;
                    // else find max, secondMax, min
                } else {
                    if (strainedApex.getFunctionValue() >= minApex.getFunctionValue()) {
                        // too far
                        maxApex = reflectedApex.clone();
                        list.set(1, maxApex);
                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            //action = 1;
                            //action.setFunctionValue(1);
                            list.get(0).setFunctionValue(1);
                        }
                        break;
                        // else find centerOfGravity
                    }
                }
            } else {
                if (reflectedApex.getFunctionValue() > minApex.getFunctionValue()
                        && reflectedApex.getFunctionValue() <= secondMaxApex.getFunctionValue()) {
                    maxApex = reflectedApex.clone();
                    list.set(1, maxApex);
                    if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                        //action = 1;
                        //action.setFunctionValue(1);
                        list.get(0).setFunctionValue(1);
                    }
                    break;
                    // else find max, secondMax, min
                } else {
                    if (reflectedApex.getFunctionValue() < maxApex.getFunctionValue()) {
                        maxApex = reflectedApex.clone();
                        list.set(1, maxApex);
                    }
                    // too far
                    Apex compressedApex = initializeCompressedApex(maxApex.getCoordinates(), centerOfGravity.getCoordinates());
                    if (compressedApex.getFunctionValue() < maxApex.getFunctionValue()) {
                        maxApex = compressedApex.clone();
                        list.set(1, maxApex);
                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            //action = 1;
                            //action.setFunctionValue(1);
                            list.get(0).setFunctionValue(1);
                        }
                        break;
                        // else find max, secondMax, min
                    } else {
                        // double downsizing

                        //!!!
                        maxApex = new Apex(new ArrayList<>(downsize(maxApex, minApex).getCoordinates()), 0.0);
                        //maxApex.setCoordinates(new ArrayList<>(downsize(maxApex,minApex).getCoordinates()));
                        maxApex.setFunctionValue(Function.testFunction(maxApex));
                        secondMaxApex = downsize(secondMaxApex, minApex);
                        secondMaxApex.setFunctionValue(Function.testFunction(secondMaxApex));
                        //!!!

                        list.set(1, maxApex);
                        list.set(2, secondMaxApex);

                        minApex.setFunctionValue(Function.testFunction(minApex));
                        list.set(3, minApex);


                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            //action = 1;
                            //action.setFunctionValue(1);
                            list.get(0).setFunctionValue(1);
                        }
                        break;
                        // else findCenterOfGravity
                    }
                }
            }
        }
        //return action;
        return list;
    }

    private static Apex initializeCompressedApex(List<Double> maxApexCoordinates, List<Double> centerOfGravityCoordinates) {
        Apex compressedApex = new Apex(compress(maxApexCoordinates, centerOfGravityCoordinates), 0.0);
        compressedApex.setFunctionValue(Function.testFunction(compressedApex));
        return compressedApex;
    }

    private static Apex initializeReflectedApex(List<Double> centerOfGravityCoordinates, List<Double> maxApexCoordinates) {
        Apex reflectedApex = new Apex(reflect(centerOfGravityCoordinates, maxApexCoordinates),
                0.0);
        reflectedApex.setFunctionValue(Function.testFunction(reflectedApex));
        return reflectedApex;
    }

    private static Apex initializeCenterOfGravity(List<Double> secondMaxApexCoordinates, List<Double> minApexCoordinates) {
        Apex centerOfGravity = new Apex(findCenterOfGravity(secondMaxApexCoordinates, minApexCoordinates),
                0.0);
        centerOfGravity.setFunctionValue(Function.testFunction(centerOfGravity));
        return centerOfGravity;
    }

    private static Apex initializeStrainedApex(List<Double> reflectedApexCoordinates, List<Double> centerOfGravityCoordinates) {
        Apex strainedApex = new Apex(strain(reflectedApexCoordinates, centerOfGravityCoordinates), 0.00);
        strainedApex.setFunctionValue(Function.testFunction(strainedApex));
        return strainedApex;
    }

    private static void debugAdditional(Apex centerOfGravity, Apex reflectedApex) {
        System.out.println("center of gravity coordinates: " + centerOfGravity.getCoordinates());
        System.out.println("center of gravity function value: " + centerOfGravity.getFunctionValue());
        System.out.println("reflected apex coords: " + reflectedApex.getCoordinates());
        System.out.println("reflected apex function value: " + reflectedApex.getFunctionValue());
    }

    private static boolean checkPrecision(Apex maxApex, Apex secondMaxApex, Apex minApex) {
        boolean state = false;
        boolean result = checkTwoApexes(maxApex, secondMaxApex);
        if (result) {
            result = checkTwoApexes(secondMaxApex, minApex);
            if (result) {
                result = checkTwoApexes(maxApex, minApex);
                if (result) {
                    state = true;
                }
            }
        }
        return state;
    }

    private static boolean checkTwoApexes(Apex firstApex, Apex secondApex) {
        List<Double> coords = new ArrayList<>();
        for (int i = 0; i < firstApex.getCoordinates().size(); i++) {
            coords.add(Math.abs(firstApex.getCoordinates().get(i) - secondApex.getCoordinates().get(i)));
        }
        double length = 0.0;
        for (double e : coords) {
            e = Math.pow(e, 2);
            length += e;
        }
        return Math.sqrt(length) < epsilon;
    }

    private static Apex downsize(Apex otherApex, Apex minApex) {
        Apex result = new Apex(new ArrayList<>(), 0.0);
        for (int i = 0; i < otherApex.getCoordinates().size(); i++) {
            double xi = (otherApex.getCoordinates().get(i) + minApex.getCoordinates().get(i)) / 2;
            result.getCoordinates().add(xi);
        }
        return result;
    }

    private static List<Double> compress(List<Double> maxApexCoordinates, List<Double> centerOfGravityCoordinates) {
        List<Double> compressedCoordinates = new ArrayList<>();
        for (int i = 0; i < maxApexCoordinates.size(); i++) {
            double result = beta * maxApexCoordinates.get(i) + (1 - beta) * centerOfGravityCoordinates.get(i);
            compressedCoordinates.add(result);
        }
        return compressedCoordinates;
    }

    private static List<Double> strain(List<Double> reflectedApexCoordinates, List<Double> centerOfGravityCoordinates) {
        List<Double> strainedCoordinates = new ArrayList<>();
        for (int i = 0; i < centerOfGravityCoordinates.size(); i++) {
            double result = gamma * reflectedApexCoordinates.get(i) + (1 - gamma) * centerOfGravityCoordinates.get(i);
            strainedCoordinates.add(result);
        }
        return strainedCoordinates;
    }

    private static void debugMain(Apex maxApex, Apex secondMaxApex, Apex minApex) {
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
            Apex p = p0.clone();
            double x = p.getCoordinates().get(i - 1);
            p.getCoordinates().remove(i - 1);
            x++;
            p.getCoordinates().add(i - 1, x);
            startingValues.add(p);
        }
        return startingValues;
    }

    private static Apex findMaxApex(List<Apex> startingValues) {
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
            } else if (e.getFunctionValue() >= secondMax.getFunctionValue() && e.getFunctionValue() <= max.getFunctionValue()) {
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