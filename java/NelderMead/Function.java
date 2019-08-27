package NelderMead;

import model.Apex;

public class Function {
    public static double testFunction(Apex p) {
        double functionValue = 0;
        double element1 = p.getCoordinates().get(0);
        double element2 = p.getCoordinates().get(1);
        element1 = Math.pow(element1, 2);
        element2 = Math.pow(element2, 2);
        functionValue = element1 + element2;
        return functionValue;
    }
}
