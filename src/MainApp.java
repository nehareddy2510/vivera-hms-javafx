import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalTime;


public class MainApp extends Application {

    BookingManager manager = new BookingManager();
    
    Label statsLabel = new Label();
    Label clockLabel = new Label();

    TableView<RoomData> table = new TableView<>();

    public static class RoomData {
        private final SimpleStringProperty roomNo, type, name, status, priority;

        RoomData(String r, String t, String n, String s, String p) {
            roomNo = new SimpleStringProperty(r);
            type = new SimpleStringProperty(t);
            name = new SimpleStringProperty(n);
            status = new SimpleStringProperty(s);
            priority = new SimpleStringProperty(p);
        }

        public String getRoomNo() { return roomNo.get(); }
        public String getType() { return type.get(); }
        public String getName() { return name.get(); }
        public String getStatus() { return status.get(); }
        public String getPriority() { return priority.get(); }
    }

    //  CARD STYLE
   private VBox card(VBox v) {
    v.setStyle(
        "-fx-background-color: rgba(255,255,255,0.95);" + // slightly transparent white
        "-fx-padding:20;" +
        "-fx-spacing:12;" +
        "-fx-background-radius:12;" +
        "-fx-border-radius:12;"
    );
    return v;
}

    private Label title(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
        return l;
    }

    private void styleBtn(Button b) {
        b.setStyle("-fx-background-color:#4f46e5; -fx-text-fill:white;" +
                "-fx-font-size:13px; -fx-background-radius:8; -fx-padding:8 15;");
    }

   

// --- STYLE METHOD added ---
private void styleField(Control c) {
    c.setStyle(
        "-fx-background-color:#f9fafb;" +   // light grey instead of white
        "-fx-text-fill:black;" +            // ensure visible text
        "-fx-background-radius:8;" +
        "-fx-border-radius:8;" +
        "-fx-border-color:#ccc;" +
        "-fx-padding:8;"
    );
}
    private void refreshAll() {
        statsLabel.setText(manager.getStats());

        table.getItems().clear();

        for (Room r : manager.rooms) {
            table.getItems().add(
                    new RoomData(
                            String.valueOf(r.roomNumber),
                            (r instanceof DeluxeRoom ? "Deluxe" : "Standard"),
                            manager.customerNames.getOrDefault(r.roomNumber, "-"),
                            (r.isBooked ? " Booked" : " Available"),
                            manager.priorities.getOrDefault(r.roomNumber, "-")
                    )
            );
        }
    }
    //login pg added
    private void showLogin(Stage mainStage) {

    Stage loginStage = new Stage();

    Label title = new Label(" VIVERA ADMIN LOGIN");
    title.setStyle("-fx-text-fill:white; -fx-font-size:20px; -fx-font-weight:bold;");

    TextField user = new TextField();
    user.setPromptText("Username");

    PasswordField pass = new PasswordField();
    pass.setPromptText("Password");

    styleField(user);
    styleField(pass);

    Button loginBtn = new Button("Login");
    styleBtn(loginBtn);

    Label status = new Label();

    loginBtn.setOnAction(e -> {
        if (user.getText().equals("admin") && pass.getText().equals("1234")) {
            loginStage.close();
            mainStage.show();
        } else {
            status.setText("Invalid credentials");
            status.setStyle("-fx-text-fill:red;");
        }
    });

    VBox layout = new VBox(15, title, user, pass, loginBtn, status);
    layout.setAlignment(Pos.CENTER);
    layout.setPadding(new Insets(30));
    layout.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e3a8a, #4f46e5);");

    loginStage.setScene(new Scene(layout, 350, 300));
    loginStage.setTitle("Admin Login");
    loginStage.show();
}

    private Label textLabel(String t) {
    Label l = new Label(t);
    l.setStyle("-fx-text-fill:#1e293b; -fx-font-size:13px;");
    return l;
}
    @Override
    public void start(Stage stage) {

        TabPane tabPane = new TabPane();

        Tab roomTab = new Tab("Rooms");
        Tab bookingTab = new Tab("Booking");
        Tab checkoutTab = new Tab("Checkout");
        Tab serviceTab = new Tab("Services");
        Tab dashboardTab = new Tab("Dashboard");

        tabPane.getTabs().addAll(roomTab, bookingTab, checkoutTab, serviceTab, dashboardTab);

        //  CLOCK
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, e ->
                        clockLabel.setText("🕒" + LocalTime.now().withNano(0))),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        //  HEADER
        Label header = new Label("VIVERA HOTEL MANAGEMENT SYSTEM");
        header.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:white;");
        Button logout = new Button("Logout");
        styleBtn(logout);

            logout.setOnAction(e -> {
                stage.close();
            });
        HBox topBar = new HBox(header, clockLabel,logout);
        topBar.setSpacing(30);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color:#1e293b;");
        clockLabel.setStyle("-fx-text-fill:white;");

        // ---------------- ROOMS ----------------
        TextField roomNo = new TextField();
        roomNo.setPromptText("Enter Room Number");

        TextField price = new TextField();
        price.setPromptText("Enter Price");

        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Standard", "Deluxe");

        styleField(roomNo);
        styleField(price);
        styleField(type);

        Button add = new Button("Add Room");
        styleBtn(add);

        Label status = new Label();

        add.setOnAction(e -> {
            try {
                Room r = type.getValue().equals("Standard")
                        ? new StandardRoom(Integer.parseInt(roomNo.getText()), Double.parseDouble(price.getText()))
                        : new DeluxeRoom(Integer.parseInt(roomNo.getText()), Double.parseDouble(price.getText()));

                status.setText(manager.addRoom(r));

                roomNo.clear();
                price.clear();
                refreshAll();
            } catch (Exception ex) {
                status.setText("Invalid input!");
            }
        });

        VBox roomLayout = card(new VBox(
                title("Room Management"),
                roomNo, price, type,
                add, status
        ));

        roomTab.setContent(roomLayout);

        // ---------------- BOOKING ----------------
        TextField name = new TextField();
        name.setPromptText("Customer Name");

        ComboBox<String> priority = new ComboBox<>();
        priority.getItems().addAll("REGULAR", "VIP");

        ComboBox<String> roomSelect = new ComboBox<>();

        styleField(name);
        styleField(priority);
        styleField(roomSelect);

        Button loadRooms = new Button("Load Available Rooms");
        styleBtn(loadRooms);

        loadRooms.setOnAction(e -> {
            roomSelect.getItems().clear();
            for (Room r : manager.rooms) {
                if (!r.isBooked)
                    roomSelect.getItems().add(String.valueOf(r.roomNumber));
            }
        });

        Button book = new Button("Book Room");
        styleBtn(book);

        Label bStatus = new Label();

        book.setOnAction(e -> {
            try {
                int selectedRoom = Integer.parseInt(roomSelect.getValue());

                bStatus.setText(
                        manager.bookSpecificRoom(
                                selectedRoom,
                                name.getText(),
                                priority.getValue()
                        )
                );

                refreshAll();
            } catch (Exception ex) {
                bStatus.setText("Select valid inputs!");
            }
        });

        VBox bookingLayout = card(new VBox(
                title("Booking"),
                loadRooms, roomSelect,
                name, priority,
                book, bStatus
        ));

        bookingTab.setContent(bookingLayout);

       //  CHECKOUT 
TextField rno = new TextField();
rno.setPromptText("Enter Room Number");

TextField days = new TextField();
days.setPromptText("Enter Days Stayed");

// APPLY FIX
styleField(rno);
styleField(days);

Button checkout = new Button("Checkout");
styleBtn(checkout);

Label bill = new Label();

// MAKE TEXT CLEARLY VISIBLE
bill.setStyle(
    "-fx-text-fill:#1e293b;" +
    "-fx-font-weight:bold;" +
    "-fx-font-size:13px;"
);

checkout.setOnAction(e -> {
    try {
        bill.setText(manager.checkout(
                Integer.parseInt(rno.getText()),
                Integer.parseInt(days.getText())
        ));
        refreshAll();
    } catch (Exception ex) {
        bill.setText("Invalid input!");
    }
});

VBox checkoutLayout = card(new VBox(
        title("Checkout"),
        rno, days,
        checkout, bill
));

// ADD LIGHT BACKGROUND CONTRAST
checkoutLayout.setStyle(
    "-fx-background-color:#ffffff;" +
    "-fx-padding:20;" +
    "-fx-spacing:10;"
);

checkoutTab.setContent(checkoutLayout);

       //  SERVICES
TextField sRoom = new TextField();
sRoom.setPromptText("Enter Room Number");

TextField sName = new TextField();
sName.setPromptText("Enter Customer Name");

ComboBox<String> sType = new ComboBox<>();
sType.getItems().addAll("Room Cleaning", "Food Service", "Laundry");

styleField(sRoom);
styleField(sName);
styleField(sType);

Button req = new Button("Request Service");
styleBtn(req);

Label sStatus = new Label();
sStatus.setStyle("-fx-text-fill:#1e293b; -fx-font-weight:bold;");

req.setOnAction(e -> {
    try {
        sStatus.setText(manager.requestService(
                Integer.parseInt(sRoom.getText()),
                sName.getText(),
                sType.getValue()
        ));
        refreshAll();
    } catch (Exception ex) {
        sStatus.setText("Invalid input!");
    }
});

VBox serviceLayout = card(new VBox(
        title("Services"),
        textLabel("Room Number"), sRoom,
        textLabel("Customer Name"), sName,
        textLabel("Service Type"), sType,
        req, sStatus
));

serviceTab.setContent(serviceLayout);

        //  DASHBOARD 

        TableColumn<RoomData, String> c1 = new TableColumn<>("Room");
        c1.setCellValueFactory(new PropertyValueFactory<>("roomNo"));

        TableColumn<RoomData, String> c2 = new TableColumn<>("Type");
        c2.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<RoomData, String> c3 = new TableColumn<>("Customer");
        c3.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<RoomData, String> c4 = new TableColumn<>("Status");
        c4.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<RoomData, String> c5 = new TableColumn<>("Priority");
        c5.setCellValueFactory(new PropertyValueFactory<>("priority"));

        table.getColumns().addAll(c1, c2, c3, c4, c5);
        table.setStyle("-fx-background-radius:10;");

        Button refresh = new Button("Refresh");
        styleBtn(refresh);
        refresh.setOnAction(e -> refreshAll());

        VBox dashboardLayout = card(new VBox(
                title("Dashboard"),
                refresh,
                statsLabel,
                table
        ));

        dashboardTab.setContent(dashboardLayout);

        VBox root = new VBox(topBar, tabPane);

        Scene scene = new Scene(root, 900, 600);
       scene.getRoot().setStyle(
    "-fx-background-color: linear-gradient(to bottom right, #1e3a8a, #4f46e5);"
);

        stage.setTitle("VIVERA HOTEL MANAGEMENT SYSTEM");
        stage.setScene(scene);
        stage.hide();
        showLogin(stage);

        refreshAll();
    }

    public static void main(String[] args) {
        launch();
    }
}