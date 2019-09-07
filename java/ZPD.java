import NelderMead.FunctionSumSquares;
import NelderMead.NelderMead;
import NelderMead.calculations;
import NelderMead.model.Apex;

import java.util.ArrayList;
import java.util.List;

public class ZPD {
    public static void main(String[] args) {
        Double x1 = -1.0;
        Double x2 = 3.0;
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(x1);
        coordinates.add(x2);
        Apex p = new Apex(coordinates, 0.00);
        calculations f = new FunctionSumSquares();
        Apex result = (NelderMead.minimise(p, f));
        System.out.println(result.getCoordinates());
        System.out.println(result.getFunctionValue());
    }
}
