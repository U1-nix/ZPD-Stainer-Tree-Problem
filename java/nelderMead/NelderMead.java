package nelderMead;

import nelderMead.model.Apex;
import trees.Minimising;
import trees.model.TreeApex;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NelderMead {
    private static double alpha = 1.0; // 1
    private static double beta = 0.5; // 0.5
    private static double gamma = 2.0; // 2
    private static double epsilon = 0.1;
    //private static FunctionRastrigin f = new FunctionRastrigin();
    //private static FunctionSumSquares f = new FunctionSumSquares();
    //private static FunctionRozenbrock f = new FunctionRozenbrock();
    //private static FunctionTest f = new FunctionTest();

    public static List<Double> minimise(List<Double> startingCoordinates, List<TreeApex> apexes) {
        System.out.println("Beginning Nelder Mead's algorithm");
        Apex p0 = new Apex(startingCoordinates, 0.0);

        // create Apexes according to p0
        List<Apex> startingValues = new ArrayList<>(initialize(p0));

        // А - find function values
        for (Apex e : startingValues) {
            e.setFunctionValue(Minimising.minimisingFunction(e.getCoordinates(), apexes));
            //e.setFunctionValue(f.calculate(e));
        }

        // iterator counter
        int it = 0;
        while (true) {
            it++;

            // Б - sort by function value; fh > fg > fl
            sort(startingValues);
            int maxApex = findMaxApex(startingValues);
            int secondMaxApex = findSecondMaxApex(startingValues, maxApex);
            int minApex = findMinApex(startingValues);

            // B - calculate center of gravity; centerOfGravity; f0
            Apex centerOfGravity = initializeCenterOfGravity(startingValues, apexes, startingValues.get(maxApex).getCoordinates());
            // Г - calculate reflected apex; reflectedApex; fr
            Apex reflectedApex = initializeReflectedApex(centerOfGravity.getCoordinates(), startingValues.get(maxApex).getCoordinates(), apexes);

            // Д - compare fr and fl
            if (reflectedApex.getFunctionValue() < startingValues.get(minApex).getFunctionValue()) {
                // fr < fl

                // Д.1) - calculate strained apex; strainedApex; fe
                Apex strainedApex = initializeStrainedApex(reflectedApex.getCoordinates(), centerOfGravity.getCoordinates(), apexes);

                // compare fe and fl
                if (strainedApex.getFunctionValue() < startingValues.get(minApex).getFunctionValue()) {
                    // fe < fl

                    // a) - replace xh with xe
                    startingValues.set(maxApex, strainedApex.clone());

                    if (checkPrecision(startingValues)) {
                        // stop
                        break;
                    }
                    // else --> В
                } else {
                    // fe >= fl

                    // б) - replace xh with xr
                    startingValues.set(maxApex, reflectedApex.clone());

                    if (checkPrecision(startingValues)) {
                        // stop
                        break;
                    }
                    // else --> B
                }
            } else {
                // fr > fl

                // compare fr and fg
                if (reflectedApex.getFunctionValue() <= startingValues.get(secondMaxApex).getFunctionValue()) {
                    // fr <= fg

                    // Д.2) - replace xh with xr
                    startingValues.set(maxApex, reflectedApex.clone());

                    if (checkPrecision(startingValues)) {
                        // stop
                        break;
                    }
                    // else --> B
                } else {
                    // E - compare fr and fh
                    if (reflectedApex.getFunctionValue() < startingValues.get(maxApex).getFunctionValue()) {
                        // fr < fh

                        // E.1) - replace xh with xr
                        startingValues.set(maxApex, reflectedApex.clone());
                    }

                    // fr >= fh
                    // E.2) - calculate compressed apex; compressedApex; fc
                    Apex compressedApex = initializeCompressedApex(startingValues.get(maxApex).getCoordinates(), centerOfGravity.getCoordinates(), apexes);

                    // Ж - compare fc and fh
                    if (compressedApex.getFunctionValue() <= startingValues.get(maxApex).getFunctionValue()) {
                        // fc <= fh

                        // Ж.1) - replace xh with xc;
                        startingValues.set(maxApex, compressedApex.clone());

                        if (checkPrecision(startingValues)) {
                            // stop
                            break;
                        }
                        // else --> Б
                    } else {
                        // fc > fh
                        // З - double downsizing
                        for (int i = 0; i < startingValues.size(); i++) {
                            if (i != minApex) {
                                startingValues.set(i, downsize(startingValues.get(i), startingValues.get(minApex)).clone());
                                startingValues.get(i).setFunctionValue(Minimising.minimisingFunction(startingValues.get(i).getCoordinates(), apexes));
                                //startingValues.get(i).setFunctionValue(f.calculate(startingValues.get(i)));
                            }
                        }

                        startingValues.get(minApex).setFunctionValue(Minimising.minimisingFunction(startingValues.get(minApex).getCoordinates(), apexes));
                        //startingValues.get(minApex).setFunctionValue(f.calculate(startingValues.get(minApex)));

                        if (checkPrecision(startingValues)) {
                            // stop
                            break;
                        }
                        // else --> В
                    }
                }
            }
        }
        // show all simplex's apexes
//        System.out.println("Finishing Nelder Mead's algorithm");
//        for (Apex e : startingValues) {
//            System.out.println(e.getCoordinates());
//            System.out.println(e.getFunctionValue());
//            System.out.println();
//        }

        System.out.println("Iterations: " + it);

        // return minApex;
        System.out.println(startingValues.get(startingValues.size() - 1).getFunctionValue());
        return startingValues.get(startingValues.size() - 1).getCoordinates();
    }

    public static void sort(List<Apex> apexes) {
        // simple descending bubble sort
        boolean sorted;
        do {
            sorted = true;
            for (int i = 0; i < apexes.size() - 1; i++) {
                if (apexes.get(i).getFunctionValue() < apexes.get(i + 1).getFunctionValue()) {
                    Apex tmp = apexes.get(i);
                    apexes.set(i, apexes.get(i + 1).clone());
                    apexes.set(i + 1, tmp.clone());
                    sorted = false;
                }
            }
        } while (!sorted);
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

    private static int findMaxApex(List<Apex> startingValues) {
        // 0 by index is biggest by function value
        int max = 0;
//        for (int i = 0; i < startingValues.size(); i++) {
//            if (startingValues.get(max).getFunctionValue() < startingValues.get(i).getFunctionValue()) {
//                max = i;
//            }
//        }
        return max;
    }

    private static int findSecondMaxApex(List<Apex> startingValues, int max) {
        // 1 by index is second biggest by function value
        int secondMax = 1;
        return secondMax;
    }

    private static int findMinApex(List<Apex> startingValues) {
        // last by index is smallest by function value
        int min = startingValues.size() - 1;
//        for (int i = 0; i < startingValues.size(); i++) {
//            if (startingValues.get(min).getFunctionValue() > startingValues.get(i).getFunctionValue()) {
//                min = i;
//            }
//        }
        return min;
    }

    private static Apex initializeCenterOfGravity(List<Apex> startingValues, List<TreeApex> apexes, List<Double> maxApexCoordinates) {
        Apex centerOfGravity = new Apex(findCenterOfGravity(startingValues, maxApexCoordinates),
                0.0);
        centerOfGravity.setFunctionValue(Minimising.minimisingFunction(centerOfGravity.getCoordinates(), apexes));
        //centerOfGravity.setFunctionValue(f.calculate(centerOfGravity));

        System.out.println("Center Of Gravity");
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravity.getCoordinates());
        System.out.println("Max Apex Coordinates: " + maxApexCoordinates);
        System.out.println("Beginning Prim's algorithm");
        System.out.println("Finishing Prim's algorithm");
        System.out.println(centerOfGravity.getFunctionValue());
        System.out.println();

        return centerOfGravity;
    }

    private static List<Double> findCenterOfGravity(List<Apex> startingValues, List<Double> maxApexCoordinates) {
        List<Double> centerOfGravityCoordinates = new ArrayList<>();
        // i - номер координаты
        for (int i = 0; i < startingValues.get(0).getCoordinates().size(); i++) {
            // sum - сумма всех i-ых координат
            double sum = 0;
            for (Apex e : startingValues) {
                sum += e.getCoordinates().get(i);
            }
            // вычитание i-ой координаты максимальной вершины
            sum -= maxApexCoordinates.get(i);
            centerOfGravityCoordinates.add(sum / (startingValues.size() - 1));
        }
        return centerOfGravityCoordinates;
    }

    private static Apex initializeReflectedApex(List<Double> centerOfGravityCoordinates, List<Double> maxApexCoordinates, List<TreeApex> apexes) {
        Apex reflectedApex = new Apex(reflect(centerOfGravityCoordinates, maxApexCoordinates),
                0.0);
        reflectedApex.setFunctionValue(Minimising.minimisingFunction(reflectedApex.getCoordinates(), apexes));
        //reflectedApex.setFunctionValue(f.calculate(reflectedApex));

        System.out.println("Reflected Apex");
        System.out.println("Reflected Apex Coordinates: " + reflectedApex.getCoordinates());
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravityCoordinates);
        System.out.println("Max Apex Coordinates: " + maxApexCoordinates);
        System.out.println("Beginning Prim's algorithm");
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
        strainedApex.setFunctionValue(Minimising.minimisingFunction(strainedApex.getCoordinates(), apexes));
        //strainedApex.setFunctionValue(f.calculate(strainedApex));

        //System.exit(0);
        System.out.println("Strained Apex");
        System.out.println("Strained Apex Coordinates: " + strainedApex.getCoordinates());
        System.out.println("Reflected Apex Coordinates: " + reflectedApexCoordinates);
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravityCoordinates);
        System.out.println("Beginning Prim's algorithm");
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
        compressedApex.setFunctionValue(Minimising.minimisingFunction(compressedApex.getCoordinates(), apexes));
        //compressedApex.setFunctionValue(f.calculate(compressedApex));

        System.out.println("Compressed Apex");
        System.out.println("Compressed Apex Coordinates" + compressedApex.getCoordinates());
        System.out.println("Max Apex Coordinates: " + maxApexCoordinates);
        System.out.println("Center Of Gravity Coordinates: " + centerOfGravityCoordinates);
        System.out.println("Beginning Prim's algorithm");
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

    private static boolean checkPrecision(List<Apex> values) {
        for (int j = 0; j < values.size() - 1; j++) {
            for (int k = j + 1; k < values.size(); k++) {
                double distance = checkTwoApexes(values.get(j), values.get(k));
                if (distance > epsilon) {
                    return false;
                }
            }
        }
        return true;
    }

    private static double checkTwoApexes(Apex firstApex, Apex secondApex) {
        double distance = 0.0;
        for (int i = 0; i < firstApex.getCoordinates().size(); i++) {
            distance += Math.pow(firstApex.getCoordinates().get(i) - secondApex.getCoordinates().get(i), 2);
        }
        return distance;
    }
}
