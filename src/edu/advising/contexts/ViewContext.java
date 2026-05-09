package edu.advising.contexts;

import edu.advising.auth.AuthenticationContext;
import edu.advising.auth.BasicAuthentication;
import edu.advising.commands.Command;
import edu.advising.commands.CommandExecutor;
import edu.advising.core.DatabaseManager;
import edu.advising.model.FacultyPermission;
import edu.advising.model.Section;
import edu.advising.model.WaitlistEntry;
import edu.advising.navmenu.StudentNavMenuFactory;
import edu.advising.navmenu.FacultyNavMenuFactory;
import edu.advising.navmenu.AdminNavMenuFactory;
import edu.advising.permissions.PermissionTree;
import edu.advising.states.ViewState;
import edu.advising.states.viewstates.GuestViewState;
import edu.advising.users.Faculty;
import edu.advising.users.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class ViewContext {
    private ArrayList<ViewState> undo;
    private ArrayList<ViewState> redo;
    private ViewState currentState;
    private AuthenticationContext authContext;
    private FacultyPermissionContext facultyPermissionContext;
    private RegistrationPeriodContext registrationPeriodContext;
    private CommandExecutor commandExecutor;
    private User currentUser;
    private PermissionTree permissionTree;

    public ViewContext() {
        this.authContext = new AuthenticationContext(new BasicAuthentication());
        this.facultyPermissionContext = null;
        this.registrationPeriodContext = null;
        this.currentState = GuestViewState.getInstance();
        this.currentState.enter(this);
        this.undo = new ArrayList<ViewState>();
        this.redo = new ArrayList<ViewState>();
    }

    // -------------------------------------------------------------------------
    // CLI entry point — starts the read-eval-print loop
    // -------------------------------------------------------------------------

    public void start() {
        Scanner in = new Scanner(System.in);
        currentState.render(this);
        while (true) {
            System.out.print("\n> ");
            if (!in.hasNextLine()) break;
            String raw = in.nextLine().trim();
            if (raw.isEmpty()) {
                currentState.render(this);
                continue;
            }
            if (!dispatch(in, raw)) break;
        }
        in.close();
        System.out.println("Session ended.");
    }

    // -------------------------------------------------------------------------
    // Input dispatch — maps typed commands to handleAction calls
    // -------------------------------------------------------------------------

    private boolean dispatch(Scanner in, String raw) {
        String cmd = raw.toLowerCase();

        if (cmd.equals("q") || cmd.equals("quit") || cmd.equals("exit")) {
            System.out.println("Goodbye.");
            return false;
        }

        if (cmd.equals("b") || cmd.equals("back")) {
            if (!undo.isEmpty()) back();
            else System.out.println("  Nothing to go back to.");
            return true;
        }

        if (cmd.equals("u") || cmd.equals("undo")) {
            if (commandExecutor != null && commandExecutor.canUndo()) {
                System.out.println("  Undoing: " + commandExecutor.peekUndoDescription());
                commandExecutor.undo();
            } else {
                System.out.println("  Nothing to undo.");
            }
            return true;
        }

        switch (currentState.getViewName()) {
            case "GUEST"                -> handleGuestInput(in, cmd);
            case "STUDENT_DASHBOARD"    -> handleStudentDashInput(raw, cmd);
            case "FACULTY_DASHBOARD"    -> handleFacultyDashInput(cmd);
            case "ADMIN_DASHBOARD"      -> handleAdminDashInput(cmd);
            case "REGISTRATION"         -> handleRegistrationInput(in, raw, cmd);
            case "TRANSCRIPT_VIEW"      -> handleSimpleViewInput(cmd);
            case "MY_SCHEDULE"          -> handleSimpleViewInput(cmd);
            case "GRADES"               -> handleSimpleViewInput(cmd);
            case "FINANCIAL_INFO"       -> handleSimpleViewInput(cmd);
            case "FINANCIAL_AID"        -> handleSimpleViewInput(cmd);
            case "PROGRAM_EVAL"         -> handleSimpleViewInput(cmd);
            case "EDUCATIONAL_PLAN"     -> handleSimpleViewInput(cmd);
            case "DOCUMENTS"            -> handleSimpleViewInput(cmd);
            case "RESTRICTIONS"         -> handleSimpleViewInput(cmd);
            case "PERMISSION_MANAGEMENT"-> handlePermissionInput(cmd);
            default -> System.out.println("  Unknown screen. Type 'b' to go back or 'q' to quit.");
        }
        return true;
    }

    private void handleGuestInput(Scanner in, String cmd) {
        if (cmd.equals("l") || cmd.equals("login")) {
            System.out.print("  Username: ");
            String username = in.nextLine().trim();
            System.out.print("  Password: ");
            String password = in.nextLine().trim();
            currentState.handleAction(this, "LOGIN", username, password, "", "127.0.0.1");
            if (currentUser != null && commandExecutor == null) {
                commandExecutor = new CommandExecutor(currentUser.getId());
            }
        } else {
            System.out.println("  Unknown command. Type 'l' to login or 'q' to quit.");
        }
    }

    private void handleStudentDashInput(String raw, String cmd) {
        switch (cmd) {
            case "s", "schedule"        -> currentState.handleAction(this, "NAVIGATE", "SCHEDULE");
            case "r", "register"        -> currentState.handleAction(this, "NAVIGATE", "REGISTRATION");
            case "g", "grades"          -> currentState.handleAction(this, "NAVIGATE", "GRADES");
            case "f", "financial"       -> currentState.handleAction(this, "NAVIGATE", "FINANCIAL_INFO");
            case "a", "aid"             -> currentState.handleAction(this, "NAVIGATE", "FINANCIAL_AID");
            case "t", "transcript"      -> currentState.handleAction(this, "NAVIGATE", "TRANSCRIPT");
            case "e", "eval"            -> currentState.handleAction(this, "NAVIGATE", "PROGRAM_EVAL");
            case "p", "plan"            -> currentState.handleAction(this, "NAVIGATE", "EDUCATIONAL_PLAN");
            case "d", "documents"       -> currentState.handleAction(this, "NAVIGATE", "DOCUMENTS");
            case "h", "restrictions"    -> currentState.handleAction(this, "NAVIGATE", "RESTRICTIONS");
            case "l", "logout"          -> currentState.handleAction(this, "LOGOUT");
            default -> {
                try {
                    int n = Integer.parseInt(raw.trim());
                    switch (n) {
                        case 1  -> currentState.handleAction(this, "NAVIGATE", "SCHEDULE");
                        case 2  -> currentState.handleAction(this, "NAVIGATE", "REGISTRATION");
                        case 3  -> currentState.handleAction(this, "NAVIGATE", "GRADES");
                        case 4  -> currentState.handleAction(this, "NAVIGATE", "FINANCIAL_INFO");
                        case 5  -> currentState.handleAction(this, "NAVIGATE", "FINANCIAL_AID");
                        case 6  -> currentState.handleAction(this, "NAVIGATE", "TRANSCRIPT");
                        case 7  -> currentState.handleAction(this, "NAVIGATE", "PROGRAM_EVAL");
                        case 8  -> currentState.handleAction(this, "NAVIGATE", "EDUCATIONAL_PLAN");
                        case 9  -> currentState.handleAction(this, "NAVIGATE", "DOCUMENTS");
                        case 10 -> currentState.handleAction(this, "NAVIGATE", "RESTRICTIONS");
                        default -> System.out.println("  Invalid option. See shortcuts above.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("  Unknown command. See shortcuts above.");
                }
            }
        }
    }

    private void handleFacultyDashInput(String cmd) {
        switch (cmd) {
            case "p", "permission"  -> currentState.handleAction(this, "NAVIGATE", "PERMISSIONS");
            case "l", "logout"      -> currentState.handleAction(this, "LOGOUT");
            default -> {
                try {
                    int n = Integer.parseInt(cmd);
                    if (n == 6)      currentState.handleAction(this, "NAVIGATE", "PERMISSIONS");
                    else if (n >= 1 && n <= FacultyNavMenuFactory.facultyItems().size())
                        System.out.println("  Feature coming soon.");
                    else
                        System.out.println("  Invalid option. See shortcuts above.");
                } catch (NumberFormatException e) {
                    System.out.println("  Unknown command. See shortcuts above.");
                }
            }
        }
    }

    private void handleAdminDashInput(String cmd) {
        switch (cmd) {
            case "l", "logout" -> currentState.handleAction(this, "LOGOUT");
            default -> {
                try {
                    int n = Integer.parseInt(cmd);
                    int total = new AdminNavMenuFactory().createMenuItems().size();
                    if (n >= 1 && n <= total) System.out.println("  Feature coming soon.");
                    else System.out.println("  Invalid option. See shortcuts above.");
                } catch (NumberFormatException e) {
                    System.out.println("  Unknown command. See shortcuts above.");
                }
            }
        }
    }

    private void handleRegistrationInput(Scanner in, String raw, String cmd) {
        if (cmd.equals("l") || cmd.equals("logout")) {
            currentState.handleAction(this, "LOGOUT");
            return;
        }
        // "reg <sectionId>"
        if (cmd.startsWith("reg ") || cmd.startsWith("register ")) {
            String[] parts = raw.trim().split("\\s+", 2);
            if (parts.length == 2) {
                currentState.handleAction(this, "REGISTER", parts[1]);
            } else {
                System.out.println("  Usage: reg <sectionId>");
            }
            return;
        }
        // plain number treated as section ID
        try {
            Integer.parseInt(raw.trim());
            currentState.handleAction(this, "REGISTER", raw.trim());
        } catch (NumberFormatException e) {
            System.out.println("  Type 'reg <sectionId>' to register, 'b' to go back, or 'l' to logout.");
        }
    }

    private void handleSimpleViewInput(String cmd) {
        if (cmd.equals("l") || cmd.equals("logout"))
            currentState.handleAction(this, "LOGOUT");
        else
            System.out.println("  Type 'b' to go back or 'l' to logout.");
    }

    private void handlePermissionInput(String cmd) {
        if (cmd.equals("l") || cmd.equals("logout")) {
            currentState.handleAction(this, "LOGOUT");
            return;
        }
        // "a <id>" or "approve <id>" — load and approve a specific request
        String[] parts = cmd.split("\\s+", 2);
        if ((parts[0].equals("a") || parts[0].equals("approve")) && parts.length == 2) {
            loadAndApprovePermission(parts[1].trim());
            return;
        }
        // bare "a" — approve the already-loaded context if one exists
        if (cmd.equals("a") || cmd.equals("approve")) {
            if (facultyPermissionContext != null) {
                currentState.handleAction(this, "APPROVE");
                facultyPermissionContext = null;
            } else {
                System.out.println("  Type 'a <requestId>' to approve a specific request.");
            }
            return;
        }
        // bare number — shorthand for "a <id>"
        try {
            Integer.parseInt(cmd);
            loadAndApprovePermission(cmd);
        } catch (NumberFormatException e) {
            System.out.println("  Type 'a <requestId>' to approve, 'b' to go back, or 'l' to logout.");
        }
    }

    private void loadAndApprovePermission(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            FacultyPermission fp = DatabaseManager.getInstance()
                    .fetchOne(FacultyPermission.class, "id", id);
            if (fp == null) { System.out.println("  Request #" + id + " not found."); return; }
            Section sec = DatabaseManager.getInstance()
                    .fetchOne(Section.class, "id", fp.getSectionId());
            WaitlistEntry entry = DatabaseManager.getInstance()
                    .fetchOne(WaitlistEntry.class, "id", fp.getWaitlistId());
            facultyPermissionContext = FacultyPermissionContext.load(
                    fp, (Faculty) currentUser, sec, entry);
            currentState.handleAction(this, "APPROVE");
            System.out.println("  Permission request #" + id + " approved.");
            facultyPermissionContext = null;
        } catch (NumberFormatException e) {
            System.out.println("  Invalid request ID.");
        } catch (SQLException e) {
            System.out.println("  Database error: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    public void setCommand(Command c) {

    }

    public ViewState getCurrentState() {
        return currentState;
    }

    public void navigateTo(ViewState s) {
        if (s.requiresAuthentication() && currentUser == null) {
            System.out.println("Error - Unauthorized Access Attempt!");
            logout();
            return;
        }
        currentState.exit(this);
        undo.add(currentState);
        currentState = s;
        s.enter(this);
        s.render(this);
    }

    public void execute() {

    }

    public void logout() {
        undo.removeAll(undo);
        redo.removeAll(redo);
        currentUser = null;
        commandExecutor = null;
        authContext.logout();
        currentState.exit(this);
        currentState = GuestViewState.getInstance();
        currentState.render(this);
    }

    public void back() {
        currentState.exit(this);
        if (undo.isEmpty()) {
            currentState = GuestViewState.getInstance();
        } else {
            redo.add(currentState);
            currentState = undo.getLast();
            undo.removeLast();
        }
        currentState.enter(this);
        currentState.render(this);
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------

    public ArrayList<ViewState> getUndo() {
        return undo;
    }

    public ArrayList<ViewState> getRedo() {
        return redo;
    }

    public AuthenticationContext getAuthContext() {
        return authContext;
    }

    public FacultyPermissionContext getFacultyPermissionContext() {
        return facultyPermissionContext;
    }

    public void setFacultyPermissionContext(FacultyPermissionContext ctx) {
        this.facultyPermissionContext = ctx;
    }

    public RegistrationPeriodContext getRegistrationPeriodContext() {
        return registrationPeriodContext;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public PermissionTree getPermissionTree() {
        return permissionTree;
    }

    public void setPermissionTree(PermissionTree permissionTree) {
        this.permissionTree = permissionTree;
    }
}
