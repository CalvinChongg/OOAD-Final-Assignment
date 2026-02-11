
package composite;

import java.util.ArrayList;
import java.util.List;

public class Row extends ParkingComponent {
    private int rowNumber;
    private List<ParkingComponent> spots = new ArrayList<>();

    public Row(int rowNumber) { this.rowNumber = rowNumber; }

    @Override
    public void add(ParkingComponent component) { spots.add(component); }
    @Override
    public void remove(ParkingComponent component) { spots.remove(component); }
    @Override
    public ParkingComponent getChild(int index) { return spots.get(index); }
    @Override
    public String getId() { return "R" + rowNumber; }

    @Override
    public void display() {
        System.out.println("    Row " + rowNumber);
        for (ParkingComponent spot : spots) spot.display();
    }

    @Override
    public int getAvailableCount() {
        return (int) spots.stream().filter(ParkingComponent::isAvailable).count();
    }
    @Override
    public int getTotalCount() { return spots.size(); }
    @Override
    public int getOccupiedCount() { return spots.size() - getAvailableCount(); }
}
