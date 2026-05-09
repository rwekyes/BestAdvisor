package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.TranscriptRequest;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;

public class DocumentsViewState implements ViewState {

    private static final DocumentsViewState instance = new DocumentsViewState();

    @Override public boolean requiresAuthentication() { return true; }

    @Override
    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    @Override public void enter(ViewContext ctx) {}
    @Override public void exit(ViewContext ctx)  {}

    @Override
    public void render(ViewContext viewContext) {
        Student student = (Student) viewContext.getCurrentUser();
        System.out.println();
        System.out.printf("  ---- Documents: %s ----%n", student.getFullName());
        System.out.println();

        try {
            List<TranscriptRequest> requests = DatabaseManager.getInstance()
                    .fetchMany(TranscriptRequest.class, "student_id", student.getId());

            if (requests.isEmpty()) {
                System.out.println("  No document requests on file.");
            } else {
                System.out.println("  Transcript Requests:");
                System.out.printf("  %-8s  %-14s  %-20s  %-10s  %-12s  %s%n",
                        "ID", "Tracking #", "Recipient", "Type", "Status", "Date");
                System.out.println("  " + "-".repeat(80));
                for (TranscriptRequest tr : requests) {
                    String tracking   = tr.getTrackingNumber() != null ? tr.getTrackingNumber() : "—";
                    String recipient  = tr.getRecipientName()  != null ? tr.getRecipientName()  : "—";
                    String type       = tr.getRequestType()    != null ? tr.getRequestType()    : "—";
                    String date       = tr.getRequestDate()    != null
                            ? tr.getRequestDate().toLocalDate().toString() : "—";
                    System.out.printf("  %-8d  %-14s  %-20s  %-10s  %-12s  %s%n",
                            tr.getId(),
                            tracking.length() > 14 ? tracking.substring(0, 14) : tracking,
                            recipient.length() > 20 ? recipient.substring(0, 20) : recipient,
                            type, tr.getStatus(), date);
                }
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load documents — " + ex.getMessage() + ")");
        }

        System.out.println();
        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "DOCUMENTS"; }
    public static DocumentsViewState getInstance() { return instance; }
}
