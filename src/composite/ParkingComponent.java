
package composite;

public abstract class ParkingComponent {
    public void add(ParkingComponent component) {
        throw new UnsupportedOperationException();
    }
    public void remove(ParkingComponent component) {
        throw new UnsupportedOperationException();
    }
    public ParkingComponent getChild(int index) {
        throw new UnsupportedOperationException();
    }
    public String getId() { return ""; }
    public boolean isAvailable() { return true; }
    public void display() { }
    public int getAvailableCount() { return 0; }
    public int getTotalCount() { return 0; }
    public int getOccupiedCount() { return 0; }
}