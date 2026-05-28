import java.io.*;
import java.util.*;
class FileManager {

    static String FILE = "rooms.dat";  // saved in project folder

    public static void saveRooms(ArrayList<Room> rooms) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE));
            oos.writeObject(rooms);
            oos.close();
        } catch (Exception e) {
            System.out.println("Save error");
        }
    }

    public static ArrayList<Room> loadRooms() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE));
            ArrayList<Room> rooms = (ArrayList<Room>) ois.readObject();
            ois.close();
            return rooms;
        } catch (Exception e) {
            return new ArrayList<>(); 
        }
    }
}