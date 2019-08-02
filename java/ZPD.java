import java.util.ArrayList;
import java.util.List;

public class ZPD {
    public static void main(String[] args) {
        Double x1 = 1.00;
        Double x2 = 2.00;
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(x1);
        coordinates.add(x2);
        Apex p = new Apex(coordinates, 0.00);
        List<Apex> check = new ArrayList<>(NelderMead.minimise(p));
        for (Apex e : check) {
            System.out.println(e.getCoordinates());
        }
    }
}
