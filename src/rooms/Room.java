import java.io.Serializable;

abstract class Room implements Serializable  {
    int roomNumber;
    double price;
    boolean isBooked;

    public Room(int roomNumber, double price) {
        this.roomNumber = roomNumber;
        this.price = price;
        this.isBooked = false;
    }

    abstract double calculateTariff(int days);
}
