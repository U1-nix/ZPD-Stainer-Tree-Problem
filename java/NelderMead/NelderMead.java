package NelderMead;

import NelderMead.model.Apex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NelderMead {
    private static double alpha = 1;
    private static double beta = 0.5;
    private static double gamma = 2;
    private static double epsilon = 0.000001;

    public static Apex minimise(Apex p0, calculations f) {
        //calculations f = new Functions();

        // create Apexes according to p0
        List<Apex> startingValues = new ArrayList<>(initialize(p0));
        for (Apex e : startingValues) {
            e.setFunctionValue(f.calculate(e));
        }

        boolean contin = true;
        while (contin) {
            // ascending sort
            Apex maxApex = findMaxApex(startingValues);
            Apex secondMaxApex = findSecondMaxApex(startingValues);
            Apex minApex = findMinApex(startingValues);
            startingValues.set(0, maxApex);                          // 0 - maxApex
            startingValues.set(1, secondMaxApex);                    // 1 - secondMaxApex
            startingValues.set(2, minApex);                          // 2 - minApex

            // centerOfGravity and reflectedApex
            Apex centerOfGravity = initializeCenterOfGravity(secondMaxApex.getCoordinates(), minApex.getCoordinates(), f);
            Apex reflectedApex = initializeReflectedApex(centerOfGravity.getCoordinates(), maxApex.getCoordinates(), f);

            if (reflectedApex.getFunctionValue() < minApex.getFunctionValue()) {
                //right way
                Apex strainedApex = initializeStrainedApex(reflectedApex.getCoordinates(), centerOfGravity.getCoordinates(), f);
                if (strainedApex.getFunctionValue() < minApex.getFunctionValue()) {
                    // very good result
                    maxApex = strainedApex.clone();
                    startingValues.set(0, maxApex);
                    if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                        // start again
                        contin = false;
                    }
                } else {
                    if (strainedApex.getFunctionValue() >= minApex.getFunctionValue()) {
                        // too far
                        maxApex = reflectedApex.clone();
                        startingValues.set(0, maxApex);
                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            // start again
                            contin = false;
                        }
                    }
                }
            } else {
                if (reflectedApex.getFunctionValue() > minApex.getFunctionValue()
                        && reflectedApex.getFunctionValue() <= secondMaxApex.getFunctionValue()) {
                    maxApex = reflectedApex.clone();
                    startingValues.set(0, maxApex);
                    if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                        // start again
                        contin = false;
                    }
                } else {
                    if (reflectedApex.getFunctionValue() < maxApex.getFunctionValue()) {
                        maxApex = reflectedApex.clone();
                        startingValues.set(0, maxApex);
                    }
                    // too far
                    Apex compressedApex = initializeCompressedApex(maxApex.getCoordinates(), centerOfGravity.getCoordinates(), f);
                    if (compressedApex.getFunctionValue() < maxApex.getFunctionValue()) {
                        maxApex = compressedApex.clone();
                        startingValues.set(0, maxApex);
                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            // start again
                            contin = false;
                        }
                    } else {
                        // double downsizing
                        maxApex = downsize(maxApex, minApex);
                        maxApex.setFunctionValue(f.calculate(maxApex));
                        startingValues.set(0, maxApex);

                        secondMaxApex = downsize(secondMaxApex, minApex);
                        secondMaxApex.setFunctionValue(f.calculate(secondMaxApex));
                        startingValues.set(1, secondMaxApex);

                        minApex.setFunctionValue(f.calculate(minApex));
                        startingValues.set(2, minApex);
                        if (checkPrecision(maxApex, secondMaxApex, minApex)) {
                            // start again
                            contin = false;
                        }
                    }
                }
            }
        }
        // return minApex;
        return startingValues.get(2);
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

    private static Apex initializeCenterOfGravity(List<Double> secondMaxApexCoordinates, List<Double> minApexCoordinates, calculations f) {
        Apex centerOfGravity = new Apex(findCenterOfGravity(secondMaxApexCoordinates, minApexCoordinates),
                0.0);
        centerOfGravity.setFunctionValue(f.calculate(centerOfGravity));
        return centerOfGravity;
    }

    private static List<Double> findCenterOfGravity(List<Double> secondMaxApexCoordinates, List<Double> minApexCoordinates) {
        List<Double> centerOfGravity = new ArrayList<>();
        for (int i = 0; i < secondMaxApexCoordinates.size(); i++) {
            double sum = 0;
            sum += secondMaxApexCoordinates.get(i) + minApexCoordinates.get(i);
            centerOfGravity.add(sum / secondMaxApexCoordinates.size());
        }
        return centerOfGravity;
    }

    private static Apex initializeReflectedApex(List<Double> centerOfGravityCoordinates, List<Double> maxApexCoordinates, calculations f) {
        Apex reflectedApex = new Apex(reflect(centerOfGravityCoordinates, maxApexCoordinates),
                0.0);
        reflectedApex.setFunctionValue(f.calculate(reflectedApex));
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

    private static Apex initializeStrainedApex(List<Double> reflectedApexCoordinates, List<Double> centerOfGravityCoordinates, calculations f) {
        Apex strainedApex = new Apex(strain(reflectedApexCoordinates, centerOfGravityCoordinates), 0.00);
        strainedApex.setFunctionValue(f.calculate(strainedApex));
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

    private static Apex initializeCompressedApex(List<Double> maxApexCoordinates, List<Double> centerOfGravityCoordinates, calculations f) {
        Apex compressedApex = new Apex(compress(maxApexCoordinates, centerOfGravityCoordinates), 0.0);
        compressedApex.setFunctionValue(f.calculate(compressedApex));
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
        List<Double> coordinates = new ArrayList<>();
        for (int i = 0; i < firstApex.getCoordinates().size(); i++) {
            coordinates.add(Math.abs(firstApex.getCoordinates().get(i) - secondApex.getCoordinates().get(i)));
        }
        double length = 0.0;
        for (double e : coordinates) {
            e = Math.pow(e, 2);
            length += e;
        }
        return Math.sqrt(length) < epsilon;
    }
}