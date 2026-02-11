
package composite;

import java.util.ArrayList;
import java.util.List;

public class Floor extends ParkingComponent {
    private int floorNumber;
    private List<ParkingComponent> rows = new ArrayList<>();

    public Floor(int floorNumber) { this.floorNumber = floorNumber; }

    @Override
    public void add(ParkingComponent component) { rows.add(component); }
    @Override
    public void remove(ParkingComponent component) { rows.remove(component); }
    @Override
    public ParkingComponent getChild(int index) { return rows.get(index); }
    @Override
    public String getId() { return "F" + floorNumber; }

    @Override
    public void display() {
        System.out.println("  Floor " + floorNumber);
        for (ParkingComponent row : rows) row.display();
    }

    @Override
    public int getAvailableCount() {
        return rows.stream().mapToInt(ParkingComponent::getAvailableCount).sum();
    }
    @Override
    public int getTotalCount() {
        return rows.stream().mapToInt(ParkingComponent::getTotalCount).sum();
    }
    @Override
    public int getOccupiedCount() {
        return rows.stream().mapToInt(ParkingComponent::getOccupiedCount).sum();
    }
}
