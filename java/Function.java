public class Function {
    public static Apex testFunction(Apex p) {
        double functionValue = 0;
        for (Double e : p.getCoordinates()) {
            functionValue = e + functionValue;
        }
        p.setFunctionValue(functionValue);
        return p;
    }
}
