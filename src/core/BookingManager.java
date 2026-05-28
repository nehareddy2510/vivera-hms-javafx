import java.util.*;

class BookingManager {

    ArrayList<Room> rooms = FileManager.loadRooms();

    HashMap<Integer, String> customerNames = new HashMap<>();
    HashMap<Integer, String> priorities = new HashMap<>();

    HashMap<Integer, Double> serviceCharges = new HashMap<>();
    HashMap<String, Double> serviceMenu = new HashMap<>();

    public BookingManager() {
        rooms = FileManager.loadRooms();

        serviceMenu.put("Room Cleaning", 200.0);
        serviceMenu.put("Food Service", 300.0);
        serviceMenu.put("Laundry", 150.0);
    }

    public String addRoom(Room r) {
        for (Room room : rooms) {
            if (room.roomNumber == r.roomNumber)
                return "Room already exists!";
        }

        rooms.add(r);
        FileManager.saveRooms(rooms);
        return "Room added!";
    }

    public Room findAvailableRoom(String priority) {

        if (priority.equals("VIP")) {
            for (Room r : rooms) {
                if (!r.isBooked && r instanceof DeluxeRoom)
                    return r;
            }
        }

        for (Room r : rooms) {
            if (!r.isBooked)
                return r;
        }

        return null;
    }

    public String suggestRooms() {
        String result = "Available Rooms:\n";

        for (Room r : rooms) {
            if (!r.isBooked) {
                result += "Room " + r.roomNumber +
                        " (" + (r instanceof DeluxeRoom ? "Deluxe" : "Standard") + ")\n";
            }
        }

        return result.equals("Available Rooms:\n") ? "No rooms available" : result;
    }

    // Choosing which room to book based on room number 
    public String bookSpecificRoom(int roomNo, String name, String priority) {
        for (Room r : rooms) {
            if (r.roomNumber == roomNo && !r.isBooked) {

                r.isBooked = true;

                customerNames.put(roomNo, name);
                priorities.put(roomNo, priority);

                FileManager.saveRooms(rooms);

                return "Room " + roomNo + " booked for " + name;
            }
        }
        return "Room not available!";
    }

    public String bookRoom(String name, String priority) {
        Room r = findAvailableRoom(priority);

        if (r == null)
            return "No rooms available";

        r.isBooked = true;

        customerNames.put(r.roomNumber, name);
        priorities.put(r.roomNumber, priority);

        FileManager.saveRooms(rooms);

        return "Room " + r.roomNumber + " booked for " + name;
    }

    public String checkout(int roomNo, int days) {
        for (Room r : rooms) {
            if (r.roomNumber == roomNo && r.isBooked) {

                double roomCost = r.calculateTariff(days);
                double service = serviceCharges.getOrDefault(roomNo, 0.0);
                double subtotal = roomCost + service;
                double gst = subtotal * 0.18;
                double total = subtotal + gst;

                r.isBooked = false;

                customerNames.remove(roomNo);
                priorities.remove(roomNo);
                serviceCharges.remove(roomNo);

                FileManager.saveRooms(rooms);

                return "===== VIVERA STAY HOTEL =====\n" +
                        "Room Cost: ₹" + roomCost +
                        "\nService Charges: ₹" + service +
                        "\nSubtotal: ₹" + subtotal +
                        "\nGST (18%): ₹" + gst +
                        "\n----------------------" +
                        "\nTOTAL: ₹" + total;
            }
        }

        return "Invalid room";
    }

    public String viewAvailableRooms() {
        String result = "";

        for (Room r : rooms) {
            if (!r.isBooked)
                result += "Room " + r.roomNumber + " | Price: " + r.price + "\n";
        }

        return result.equals("") ? "No rooms available" : result;
    }

    public String requestService(int roomNo, String name, String serviceType) {

        if (!customerNames.containsKey(roomNo))
            return "Room not booked!";

        double cost = serviceMenu.get(serviceType);

        serviceCharges.put(roomNo,
                serviceCharges.getOrDefault(roomNo, 0.0) + cost);

        return serviceType + " requested by " + name + " | Cost: ₹" + cost;
    }

    public String getStats() {
        int total = rooms.size();
        int booked = 0;

        for (Room r : rooms) {
            if (r.isBooked) booked++;
        }

        return "Total Rooms: " + total +
                " | Booked: " + booked +
                " | Available: " + (total - booked);
    }

    public String getAllBookings() {
        String result = "";

        for (Room r : rooms) {
            if (r.isBooked) {
                result += "Room " + r.roomNumber +
                        " | Name: " + customerNames.get(r.roomNumber) +
                        " | Type: " + (r instanceof DeluxeRoom ? "Deluxe" : "Standard") +
                        " | Priority: " + priorities.get(r.roomNumber) + "\n";
            }
        }

        return result.equals("") ? "No active bookings" : result;
    }

    public String getRoomStatusColored() {
        String result = "";

        for (Room r : rooms) {
            result += "Room " + r.roomNumber + " | " +
                    (r.isBooked ? "🔴 Booked" : "🟢 Available") + "\n";
        }

        return result;
    }
}
