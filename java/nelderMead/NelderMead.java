package nelderMead;

import nelderMead.model.Apex;
import trees.Minimising;
import trees.model.TreeApex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NelderMead {
    private static double alpha = 1;
    private static double beta = 0.5;
    private static double gamma = 2;
    private static double epsilon = 0.1;

    public static List<Double> minimise(List<Double> startingCoordinates, List<TreeApex> apexes) {
        System.out.println("Beginning Nelder Mead's algorithm");
        Apex p0 = new Apex(startingCoordinates, 0.0);
        // create Apexes according to p0
        List<Apex> startingValues = new ArrayList<>(initialize(p0));
        for (Apex e : startingValues) {
            e.setFunctionValue(Minimising.minimisingFunction(e.getCoordinates(), apexes));
        }

        while (true) {
            // ascending sort
            Apex maxApex = findMaxApex(startingValues);
            Apex secondMaxApex = findSecondMaxApex(startingValues);
            Apex minApex = findMinApex(startingValues);
            startingValues.clear();
            startingValues.add(0, maxApex);                          // 0 - maxApex
            startingValues.add(1, secondMaxApex);                    // 1 - secondMaxApex
            startingValues.add(2, minApex);                          // 2 - minApex

            // centerOfGravity and reflectedApex
            Apex centerOfGravity = initializeCenterOfGravity(secondMaxApex.getCoordinates(), minApex.getCoordinates(), apexes);
            Apex reflectedApex = initializeReflectedApex(centerOfGravity.getCoordinates(), maxApex.getCoordinates(), apexes);

            if (reflectedApex.getFunctionValue() < minApex.getFunctionValue()) {
                //right way
                Apex strainedApex = initializeStrainedApex(reflectedApex.getCoordinates(), centerOfGravity.getCoordinates(), apexes);
                if (strainedApex.getFunctionValue() < minApex.getFunctionValue()) {
                    // very good result
                    maxApex = strainedApex.clone();
                    startingValues.set(0, maxApex);
                    if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                        break;
                    }
                    // else start again
                } else {

                    // too far
                    maxApex = reflectedApex.clone();
                    startingValues.set(0, maxApex);
                    if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                        break;
                    }
                    // else start again

                }
            } else {
                if (reflectedApex.getFunctionValue() <= secondMaxApex.getFunctionValue()) {
                    maxApex = reflectedApex.clone();
                    startingValues.set(0, maxApex);
                    if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                        break;
                    }
                    // else start again
                } else {
                    if (reflectedApex.getFunctionValue() < maxApex.getFunctionValue()) {
                        maxApex = reflectedApex.clone();
                        startingValues.set(0, maxApex);
                    }
                    // too far
                    Apex compressedApex = initializeCompressedApex(maxApex.getCoordinates(), centerOfGravity.getCoordinates(), apexes);
                    if (compressedApex.getFunctionValue() < maxApex.getFunctionValue()) {
                        maxApex = compressedApex.clone();
                        startingValues.set(0, maxApex);
                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            break;
                        }
                        // else start again
                    } else {
                        // double downsizing
                        maxApex = downsize(maxApex, minApex).clone();
                        maxApex.setFunctionValue(Minimising.minimisingFunction(maxApex.getCoordinates(), apexes));
                        startingValues.set(0, maxApex);

                        secondMaxApex = downsize(secondMaxApex, minApex).clone();
                        secondMaxApex.setFunctionValue(Minimising.minimisingFunction(secondMaxApex.getCoordinates(), apexes));
                        startingValues.set(1, secondMaxApex);

                        minApex.setFunctionValue(Minimising.minimisingFunction(minApex.getCoordinates(), apexes));
                        startingValues.set(2, minApex);
                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            break;
                        }
                        // else start again
                    }
                }
            }
        }
        System.out.println("Finishing Nelder Mead's algorithm");
        // return minApex;
        return startingValues.get(2).getCoordinates();
    }

    private static List<Apex> initialize(Apex p0) {
        List<Apex> startingValues = new LinkedList<>();
        startingValues.add(p0);
        for (int i = 1; i <= p0.getCoordinates().size(); i++) {
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
        Apex max = new Apex(new ArrayList<>(), 0.0);
        max.setFunctionValue(-Double.MAX_VALUE);
        Apex secondMax = new Apex(new ArrayList<>(), 0.0);
        secondMax.setFunctionValue(-Double.MAX_VALUE);
        for (Apex startingValue : startingValues) {
            if (startingValue.getFunctionValue() > secondMax.getFunctionValue()) {
                if (startingValue.getFunctionValue() > max.getFunctionValue()) {
                    secondMax = max.clone();
                    max = startingValue;
                } else {
                    secondMax = startingValue;
                }
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

    private static Apex initializeCenterOfGravity(List<Double> secondMaxApexCoordinates, List<Double> minApexCoordinates, List<TreeApex> apexes) {
        Apex centerOfGravity = new Apex(findCenterOfGravity(secondMaxApexCoordinates, minApexCoordinates),
                0.0);
        System.out.println("Center Of Gravity");
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravity.getCoordinates());
        System.out.println("Second Max Apex Coordinates: " + secondMaxApexCoordinates);
        System.out.println("Min Apex Coordinates" + minApexCoordinates);
        System.out.println("Beginning Prim's algorithm");
        centerOfGravity.setFunctionValue(Minimising.minimisingFunction(centerOfGravity.getCoordinates(), apexes));
        System.out.println("Finishing Prim's algorithm");
        System.out.println(centerOfGravity.getFunctionValue());
        System.out.println();
        return centerOfGravity;
    }

    private static List<Double> findCenterOfGravity(List<Double> secondMaxApexCoordinates, List<Double> minApexCoordinates) {
        List<Double> centerOfGravityCoordinates = new ArrayList<>();
        for (int i = 0; i < secondMaxApexCoordinates.size(); i++) {
            double sum = secondMaxApexCoordinates.get(i) + minApexCoordinates.get(i);
            centerOfGravityCoordinates.add(sum / 2);
        }
        return centerOfGravityCoordinates;
    }

    private static Apex initializeReflectedApex(List<Double> centerOfGravityCoordinates, List<Double> maxApexCoordinates, List<TreeApex> apexes) {
        Apex reflectedApex = new Apex(reflect(centerOfGravityCoordinates, maxApexCoordinates),
                0.0);
        System.out.println("Reflected Apex");
        System.out.println("Reflected Apex Coordinates: " + reflectedApex.getCoordinates());
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravityCoordinates);
        System.out.println("Max Apex Coordinates: " + maxApexCoordinates);
        System.out.println("Beginning Prim's algorithm");
        reflectedApex.setFunctionValue(Minimising.minimisingFunction(reflectedApex.getCoordinates(), apexes));
        System.out.println("Finishing Prim's algorithm");
        System.out.println(reflectedApex.getFunctionValue());
        System.out.println();
        return reflectedApex;
    }

    private static List<Double> reflect(List<Double> centerOfGravityCoordinates, List<Double> maxApexCoordinates) {
        List<Double> reflectedCoordinates = new ArrayList<>();
        for (int i = 0; i < centerOfGravityCoordinates.size(); i++) {
            double result = (1 + alpha) * centerOfGravityCoordinates.get(i) - alpha * maxApexCoordinates.get(i);
            reflectedCoordinates.add(result);
        }
        return reflectedCoordinates;
    }

    private static Apex initializeStrainedApex(List<Double> reflectedApexCoordinates, List<Double> centerOfGravityCoordinates, List<TreeApex> apexes) {
        Apex strainedApex = new Apex(strain(reflectedApexCoordinates, centerOfGravityCoordinates), 0.00);
        System.out.println("Strained Apex");
        System.out.println("Strained Apex Coordinates: " + strainedApex.getCoordinates());
        System.out.println("Reflected Apex Coordinates: " + reflectedApexCoordinates);
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravityCoordinates);
        System.out.println("Beginning Prim's algorithm");
        strainedApex.setFunctionValue(Minimising.minimisingFunction(strainedApex.getCoordinates(), apexes));
        System.out.println("Finishing Prim's algorithm");
        System.out.println(strainedApex.getFunctionValue());
        System.out.println();
        return strainedApex;
    }

    private static List<Double> strain(List<Double> reflectedApexCoordinates, List<Double> centerOfGravityCoordinates) {
        List<Double> strainedCoordinates = new ArrayList<>();
        for (int i = 0; i < centerOfGravityCoordinates.size(); i++) {
            double result = gamma * reflectedApexCoordinates.get(i) + (1 - gamma) * centerOfGravityCoordinates.get(i);
            strainedCoordinates.add(result);
        }
        return strainedCoordinates;
    }

    private static Apex initializeCompressedApex(List<Double> maxApexCoordinates, List<Double> centerOfGravityCoordinates, List<TreeApex> apexes) {
        Apex compressedApex = new Apex(compress(maxApexCoordinates, centerOfGravityCoordinates), 0.0);
        System.out.println("Compressed Apex");
        System.out.println("Compressed Apex Coordinates" + compressedApex.getCoordinates());
        System.out.println("Max Apex Coordinates: " + maxApexCoordinates);
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravityCoordinates);
        System.out.println("Beginning Prim's algorithm");
        compressedApex.setFunctionValue(Minimising.minimisingFunction(compressedApex.getCoordinates(), apexes));
        System.out.println("Finishing Prim's algorithm");
        System.out.println(compressedApex.getFunctionValue());
        System.out.println();
        return compressedApex;
    }

    private static List<Double> compress(List<Double> maxApexCoordinates, List<Double> centerOfGravityCoordinates) {
        List<Double> compressedCoordinates = new ArrayList<>();
        for (int i = 0; i < maxApexCoordinates.size(); i++) {
            double result = beta * maxApexCoordinates.get(i) + (1 - beta) * centerOfGravityCoordinates.get(i);
            compressedCoordinates.add(result);
        }
        return compressedCoordinates;
    }

    private static Apex downsize(Apex otherApex, Apex minApex) {
        Apex result = new Apex(new ArrayList<>(), 0.0);
        for (int i = 0; i < otherApex.getCoordinates().size(); i++) {
            double xi = (otherApex.getCoordinates().get(i) + minApex.getCoordinates().get(i)) / 2;
            result.getCoordinates().add(xi);
        }
        return result;
    }

    private static boolean checkPrecision(Apex maxApex, Apex secondMaxApex, Apex minApex) {
        boolean state = false;
        if (checkTwoApexes(maxApex, secondMaxApex)) {
            if (checkTwoApexes(secondMaxApex, minApex)) {
                if (checkTwoApexes(maxApex, minApex)) {
                    state = true;
                }
            }
        }
        return state;
    }

    private static boolean checkTwoApexes(Apex firstApex, Apex secondApex) {
        return (Math.abs(firstApex.getFunctionValue() - secondApex.getFunctionValue()) < epsilon);
    }
}