import java.util.ArrayList;
import java.util.List;

public class ZPD {
    public static void main(String[] args) {
        Double x1 = 5.0;
        Double x2 = -5.0;
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(x1);
        coordinates.add(x2);
        Apex p = new Apex(coordinates, 0.00);
        Apex result = p.clone();
        result = (NelderMead.minimise(p));
        System.out.println(result.getCoordinates());
        System.out.println(result.getFunctionValue());
    }
}
