class DeluxeRoom extends Room {

    public DeluxeRoom(int roomNumber, double price) {
        super(roomNumber, price);
    }

    double calculateTariff(int days) {
        return (price * days) + 500;
    }
}