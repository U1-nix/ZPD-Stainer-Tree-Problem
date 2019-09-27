package nelderMead;

import nelderMead.model.Apex;

public class FunctionSumSquares implements calculations {
    public double calculate(Apex p) {
        double element1 = p.getCoordinates().get(0);
        double element2 = p.getCoordinates().get(1);
        element1 = Math.pow(element1, 2);
        element2 = Math.pow(element2, 2);
        return element1 + element2;
    }
}
