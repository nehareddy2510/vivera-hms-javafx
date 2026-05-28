
class StandardRoom extends Room {

    public StandardRoom(int roomNumber, double price) {
        super(roomNumber, price);
    }

    double calculateTariff(int days) {
        return price * days;
    }
}