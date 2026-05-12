package edu.advising.views;

// ============================================================================
// ADMIN USER MANAGEMENT — UserManagementView
// ============================================================================
//
// A self-contained JavaFX panel for full CRUD on user accounts.
// Drop it into any container — typically the center content area:
//
//   CommandExecutor executor = new CommandExecutor(loggedInAdmin.getId());
//   root.setCenter(new UserManagementView(executor));
//
// FEATURES
//   • Live-search filter across all visible columns
//   • Add User  — dialog form with type-specific extra fields
//   • Edit User — double-click or button; each changed field becomes an
//                 AdminUpdateUserCommand; multiple changes wrap in MacroCommand
//   • Toggle Active — deactivate or reactivate in one click
//   • All mutations go through CommandExecutor → full undo/redo + audit trail
//
// ============================================================================

import edu.advising.commands.*;
import edu.advising.users.*;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.util.*;

public class UserManagementView extends VBox {

    private final CommandExecutor executor;
    private final UserFactory     factory = new UserFactory();

    private final ObservableList<User> allUsers    = FXCollections.observableArrayList();
    private final FilteredList<User>   filtered    = new FilteredList<>(allUsers, u -> true);
    private final TableView<User>      table       = new TableView<>(filtered);
    private final Label                statusLabel = new Label();

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    public UserManagementView(CommandExecutor executor) {
        this.executor = executor;
        setSpacing(10);
        setPadding(new Insets(16));
        getChildren().addAll(buildHeader(), buildToolbar(), buildTable(), statusLabel);
        VBox.setVgrow(buildTable(), Priority.ALWAYS);
        loadUsers();
    }

    // -------------------------------------------------------------------------
    // Header
    // -------------------------------------------------------------------------

    private Node buildHeader() {
        Label title = new Label("User Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        return title;
    }

    // -------------------------------------------------------------------------
    // Toolbar  (search + action buttons)
    // -------------------------------------------------------------------------

    private Node buildToolbar() {
        TextField search = new TextField();
        search.setPromptText("Search users…");
        search.setPrefWidth(220);
        search.textProperty().addListener((obs, old, val) -> applyFilter(val));

        Button addBtn    = new Button("Add User");
        Button editBtn   = new Button("Edit");
        Button toggleBtn = new Button("Toggle Active");
        Button refreshBtn= new Button("Refresh");

        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) showEditDialog(sel);
            else showStatus("Select a user to edit.", false);
        });
        toggleBtn.setOnAction(e -> {
            User sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) toggleActive(sel);
            else showStatus("Select a user to toggle.", false);
        });
        refreshBtn.setOnAction(e -> loadUsers());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(8, search, spacer, addBtn, editBtn, toggleBtn, refreshBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // -------------------------------------------------------------------------
    // Table
    // -------------------------------------------------------------------------

    private Node buildTable() {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No users found."));

        table.getColumns().addAll(List.of(
            col("ID",       80,  u -> new SimpleStringProperty(String.valueOf(u.getId()))),
            col("Type",     90,  u -> new SimpleStringProperty(u.getUserType())),
            col("Username", 130, u -> new SimpleStringProperty(u.getUsername())),
            col("Name",     160, u -> new SimpleStringProperty(u.getFullName())),
            col("Email",    200, u -> new SimpleStringProperty(u.getEmail())),
            col("Phone",    120, u -> new SimpleStringProperty(Objects.toString(u.getPhone(), ""))),
            col("Status",   80,  u -> new SimpleStringProperty(Boolean.TRUE.equals(u.isActive()) ? "Active" : "Inactive"))
        ));

        // Colour-code the Status cell
        TableColumn<User, String> statusCol = (TableColumn<User, String>) table.getColumns().get(6);
        statusCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Active".equals(item)
                    ? "-fx-text-fill: green; -fx-font-weight: bold;"
                    : "-fx-text-fill: grey;");
            }
        });

        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null)
                showEditDialog(table.getSelectionModel().getSelectedItem());
        });

        VBox.setVgrow(table, Priority.ALWAYS);
        return table;
    }

    private <T extends Node> TableColumn<User, String> col(
            String title, double prefWidth,
            java.util.function.Function<User, ObservableValue<String>> extractor) {
        TableColumn<User, String> c = new TableColumn<>(title);
        c.setPrefWidth(prefWidth);
        c.setCellValueFactory(cell -> extractor.apply(cell.getValue()));
        return c;
    }

    // -------------------------------------------------------------------------
    // Data loading
    // -------------------------------------------------------------------------

    private void loadUsers() {
        try {
            List<User> users = factory.getAllUsers();
            allUsers.setAll(users);
            showStatus("Loaded " + users.size() + " users.", true);
        } catch (SQLException e) {
            showStatus("Failed to load users: " + e.getMessage(), false);
        }
    }

    private void applyFilter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        filtered.setPredicate(u -> {
            if (q.isEmpty()) return true;
            return Objects.toString(u.getUsername(), "").toLowerCase().contains(q)
                || Objects.toString(u.getFullName(),  "").toLowerCase().contains(q)
                || Objects.toString(u.getEmail(),     "").toLowerCase().contains(q)
                || Objects.toString(u.getUserType(),  "").toLowerCase().contains(q);
        });
    }

    // -------------------------------------------------------------------------
    // Add User dialog
    // -------------------------------------------------------------------------

    private void showAddDialog() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a new user account");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));

        // Common fields
        ComboBox<String> typeBox = new ComboBox<>(
            FXCollections.observableArrayList("STUDENT", "FACULTY", "ADMIN"));
        typeBox.setValue("STUDENT");

        TextField usernameF  = field("");
        TextField passwordF  = field("");
        TextField emailF     = field("");
        TextField firstF     = field("");
        TextField lastF      = field("");

        // Type-specific extras (shown/hidden based on typeBox)
        Label      extraLabel = new Label("Student ID:");
        TextField  extraF1    = field("");  // studentId / employeeId / accessLevel
        Label      extra2Label= new Label("Department:");
        TextField  extraF2    = field("");  // department (faculty only)

        int row = 0;
        grid.add(lbl("User Type:"), 0, row); grid.add(typeBox,   1, row++);
        grid.add(lbl("Username:"),  0, row); grid.add(usernameF, 1, row++);
        grid.add(lbl("Password:"),  0, row); grid.add(passwordF, 1, row++);
        grid.add(lbl("First Name:"),0, row); grid.add(firstF,    1, row++);
        grid.add(lbl("Last Name:"), 0, row); grid.add(lastF,     1, row++);
        grid.add(lbl("Email:"),     0, row); grid.add(emailF,    1, row++);
        grid.add(extraLabel,        0, row); grid.add(extraF1,   1, row++);
        grid.add(extra2Label,       0, row); grid.add(extraF2,   1, row);

        // Update extra labels when type changes
        typeBox.setOnAction(e -> {
            switch (typeBox.getValue()) {
                case "STUDENT" -> { extraLabel.setText("Student ID:"); extra2Label.setVisible(false); extraF2.setVisible(false); }
                case "FACULTY" -> { extraLabel.setText("Employee ID:");extra2Label.setVisible(true);  extraF2.setVisible(true); }
                case "ADMIN"   -> { extraLabel.setText("Access Level:"); extra2Label.setVisible(false); extraF2.setVisible(false); }
            }
        });
        extra2Label.setVisible(false);
        extraF2.setVisible(false);

        dialog.getDialogPane().setContent(grid);

        // Disable OK until required fields filled
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);
        Runnable validate = () -> okButton.setDisable(
            usernameF.getText().isBlank() || passwordF.getText().isBlank()
            || firstF.getText().isBlank() || lastF.getText().isBlank()
            || emailF.getText().isBlank()  || extraF1.getText().isBlank());
        List.of(usernameF, passwordF, firstF, lastF, emailF, extraF1)
            .forEach(tf -> tf.textProperty().addListener((o, ov, nv) -> validate.run()));

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            Map<String, String> result = new LinkedHashMap<>();
            result.put("userType",  typeBox.getValue());
            result.put("username",  usernameF.getText().trim());
            result.put("password",  passwordF.getText());
            result.put("firstName", firstF.getText().trim());
            result.put("lastName",  lastF.getText().trim());
            result.put("email",     emailF.getText().trim());
            result.put("extra1",    extraF1.getText().trim());
            result.put("extra2",    extraF2.getText().trim());
            return result;
        });

        dialog.showAndWait().ifPresent(data -> {
            String type  = data.get("userType");
            String extra1 = data.get("extra1");
            String extra2 = data.get("extra2");

            String[] extras = "FACULTY".equals(type) && !extra2.isBlank()
                ? new String[]{extra1, extra2}
                : new String[]{extra1};

            AdminCreateUserCommand cmd = new AdminCreateUserCommand(
                type, data.get("username"), data.get("password"),
                data.get("email"), data.get("firstName"), data.get("lastName"),
                extras
            );
            executor.execute(cmd);

            if (cmd.wasSuccessful()) {
                loadUsers();
                showStatus("User '" + data.get("username") + "' created.", true);
            } else {
                showStatus("Create failed: " + cmd.getErrorMessage(), false);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Edit User dialog
    // -------------------------------------------------------------------------

    private void showEditDialog(User tableRow) {
        // Load the fully-typed object so we can read/write subtype fields.
        User typedUser;
        try {
            typedUser = factory.getTypedUser(tableRow.getId());
        } catch (SQLException e) {
            showStatus("Could not load user for editing: " + e.getMessage(), false);
            return;
        }
        if (typedUser == null) { showStatus("User not found.", false); return; }

        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit User — " + typedUser.getFullName());
        dialog.setHeaderText(typedUser.getUserType() + "  •  ID " + typedUser.getId());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(16));

        // Capture original values so we only save what changed.
        Map<String, TextField> fieldMap = new LinkedHashMap<>();

        int row = 0;
        row = addEditRow(grid, fieldMap, "firstName",  "First Name:",  typedUser, row);
        row = addEditRow(grid, fieldMap, "lastName",   "Last Name:",   typedUser, row);
        row = addEditRow(grid, fieldMap, "email",      "Email:",       typedUser, row);
        row = addEditRow(grid, fieldMap, "phone",      "Phone:",       typedUser, row);
        row = addEditRow(grid, fieldMap, "username",   "Username:",    typedUser, row);

        if (typedUser instanceof Student) {
            row = addEditRow(grid, fieldMap, "gpa",             "GPA:",             typedUser, row);
            row = addEditRow(grid, fieldMap, "major",           "Major:",           typedUser, row);
            row = addEditRow(grid, fieldMap, "minor",           "Minor:",           typedUser, row);
            row = addEditRow(grid, fieldMap, "classification",  "Classification:",  typedUser, row);
            row = addEditRow(grid, fieldMap, "academicStanding","Academic Standing:",typedUser, row);
            row = addEditRow(grid, fieldMap, "enrollmentStatus","Enrollment Status:",typedUser, row);
            row = addEditRow(grid, fieldMap, "creditsEarned",   "Credits Earned:",  typedUser, row);
        } else if (typedUser instanceof Faculty) {
            row = addEditRow(grid, fieldMap, "department",    "Department:",     typedUser, row);
            row = addEditRow(grid, fieldMap, "title",         "Title:",          typedUser, row);
            row = addEditRow(grid, fieldMap, "officeLocation","Office Location:", typedUser, row);
            row = addEditRow(grid, fieldMap, "officeHours",   "Office Hours:",   typedUser, row);
        } else if (typedUser instanceof Admin) {
            row = addEditRow(grid, fieldMap, "accessLevel", "Access Level:", typedUser, row);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);
        dialog.getDialogPane().setContent(scroll);

        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            Map<String, String> result = new LinkedHashMap<>();
            fieldMap.forEach((fname, tf) -> result.put(fname, tf.getText()));
            return result;
        });

        dialog.showAndWait().ifPresent(newValues -> {
            MacroCommand macro = new MacroCommand(
                "Edit user " + typedUser.getFullName() + " (ID " + typedUser.getId() + ")");

            final User ref = typedUser; // effectively final for lambda
            fieldMap.forEach((fname, tf) -> {
                String oldVal = AdminUpdateUserCommand.readField(ref, fname);
                String newVal = newValues.get(fname);
                if (newVal != null && !newVal.equals(oldVal)) {
                    macro.addCommand(new AdminUpdateUserCommand(ref, fname, newVal));
                }
            });

            if (macro.getCommandCount() == 0) {
                showStatus("No changes made.", true);
                return;
            }

            executor.execute(macro);
            if (macro.wasSuccessful()) {
                loadUsers();
                showStatus("User '" + typedUser.getUsername() + "' updated ("
                    + macro.getCommandCount() + " field(s) changed).", true);
            } else {
                showStatus("Update failed (changes rolled back).", false);
            }
        });
    }

    /** Adds a labelled TextField row to the edit grid; returns the next row index. */
    private int addEditRow(GridPane grid, Map<String, TextField> map,
                           String fieldName, String labelText, User user, int row) {
        String current = AdminUpdateUserCommand.readField(user, fieldName);
        TextField tf = field(current);
        grid.add(lbl(labelText), 0, row);
        grid.add(tf,             1, row);
        map.put(fieldName, tf);
        return row + 1;
    }

    // -------------------------------------------------------------------------
    // Toggle active status
    // -------------------------------------------------------------------------

    private void toggleActive(User tableRow) {
        boolean currentlyActive = Boolean.TRUE.equals(tableRow.isActive());
        String  action          = currentlyActive ? "deactivate" : "reactivate";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Are you sure you want to " + action + " user '"
            + tableRow.getUsername() + "'?",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm");
        confirm.setHeaderText(null);

        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            // Need the typed object so deserialization works later
            User typed;
            try { typed = factory.getTypedUser(tableRow.getId()); }
            catch (SQLException e) { showStatus("Load failed: " + e.getMessage(), false); return; }

            AdminSetUserActiveCommand cmd =
                new AdminSetUserActiveCommand(typed, !currentlyActive);
            executor.execute(cmd);

            if (cmd.wasSuccessful()) {
                loadUsers();
                showStatus("User '" + tableRow.getUsername() + "' " + action + "d.", true);
            } else {
                showStatus("Status change failed: " + cmd.getErrorMessage(), false);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Small helpers
    // -------------------------------------------------------------------------

    private void showStatus(String message, boolean ok) {
        statusLabel.setText(message);
        statusLabel.setTextFill(ok ? Color.DARKGREEN : Color.DARKRED);
    }

    private static Label lbl(String text) {
        Label l = new Label(text);
        l.setMinWidth(140);
        return l;
    }

    private static TextField field(String initial) {
        TextField tf = new TextField(initial);
        tf.setPrefWidth(240);
        return tf;
    }
}
