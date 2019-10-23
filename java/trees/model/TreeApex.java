package trees.model;

import java.util.List;

public class TreeApex {
    private int id;
    private List<Double> coordinates;
    private int PreviousApexId;
    private boolean connectedFurther;
    private double distanceToParent;
    private boolean isAdditional;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public int getPreviousApexId() {
        return PreviousApexId;
    }

    public void setPreviousApexId(int previousApexId) {
        PreviousApexId = previousApexId;
    }

    public boolean isConnectedFurther() {
        return connectedFurther;
    }

    public void setConnectedFurther(boolean connectedFurther) {
        this.connectedFurther = connectedFurther;
    }

    public double getDistanceToParent() {
        return distanceToParent;
    }

    public void setDistanceToParent(double distanceToParent) {
        this.distanceToParent = distanceToParent;
    }

    public boolean isAdditional() {
        return isAdditional;
    }

    public void setAdditional(boolean additional) {
        isAdditional = additional;
    }
}
