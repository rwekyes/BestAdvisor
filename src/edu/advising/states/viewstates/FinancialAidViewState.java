package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

public class FinancialAidViewState implements ViewState {

    private static final FinancialAidViewState instance = new FinancialAidViewState();

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
        System.out.printf("  ---- Financial Aid: %s ----%n", student.getFullName());
        System.out.println();
        System.out.println("  Financial aid information is managed through the Student Financial Services portal.");
        System.out.println("  Contact your advisor or visit the Financial Aid office for details.");
        System.out.println();
        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "FINANCIAL_AID"; }
    public static FinancialAidViewState getInstance() { return instance; }
}
