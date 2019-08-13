import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NelderMead {
    private static double alpha;
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

        List<Double> xh = maxApex.getCoordinates();
        //System.out.println("xh coords: " + xh);
        double fh = maxApex.getFunctionValue();
        //System.out.println("fh function value: " + fh);

        List<Double> xg = secondMaxApex.getCoordinates();
        //System.out.println("xg coords: " + xg);
        double fg = secondMaxApex.getFunctionValue();
        //System.out.println("fg function value: " + fg);

        List<Double> xl = minApex.getCoordinates();
        //System.out.println("xl coords: " + xl);
        double fl = minApex.getFunctionValue();
        //System.out.println("fl function value: " + fl);

        return startingValues;
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