
package composite;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot extends ParkingComponent {
    private String name;
    private List<ParkingComponent> floors = new ArrayList<>();

    public ParkingLot(String name) { this.name = name; }

    @Override
    public void add(ParkingComponent component) { floors.add(component); }
    @Override
    public void remove(ParkingComponent component) { floors.remove(component); }
    @Override
    public ParkingComponent getChild(int index) { return floors.get(index); }

    @Override
    public void display() {
        System.out.println("Parking Lot: " + name);
        for (ParkingComponent floor : floors) floor.display();
    }

    @Override
    public int getAvailableCount() {
        return floors.stream().mapToInt(ParkingComponent::getAvailableCount).sum();
    }
    @Override
    public int getTotalCount() {
        return floors.stream().mapToInt(ParkingComponent::getTotalCount).sum();
    }
    @Override
    public int getOccupiedCount() {
        return floors.stream().mapToInt(ParkingComponent::getOccupiedCount).sum();
    }
}