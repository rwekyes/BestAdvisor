package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.navmenu.*;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.util.List;

public class StudentDashboardViewState implements ViewState {

    private static final StudentDashboardViewState instance = new StudentDashboardViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    public void handleAction(ViewContext ctx, String command){
        switch(command){
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    public void handleAction(ViewContext ctx, String command, String... params) {
        switch(command){
            case "NAVIGATE" -> {
                switch (params[0]) {
                    case "SCHEDULE"         -> ctx.navigateTo(ScheduleViewState.getInstance());
                    case "REGISTRATION"     -> ctx.navigateTo(RegistrationViewState.getInstance());
                    case "GRADES"           -> ctx.navigateTo(GradesViewState.getInstance());
                    case "FINANCIAL_INFO"   -> ctx.navigateTo(FinancialInfoViewState.getInstance());
                    case "FINANCIAL_AID"    -> ctx.navigateTo(FinancialAidViewState.getInstance());
                    case "TRANSCRIPT"       -> ctx.navigateTo(TranscriptViewState.getInstance());
                    case "PROGRAM_EVAL"     -> ctx.navigateTo(ProgramEvalViewState.getInstance());
                    case "EDUCATIONAL_PLAN" -> ctx.navigateTo(EducationalPlanViewState.getInstance());
                    case "DOCUMENTS"        -> ctx.navigateTo(DocumentsViewState.getInstance());
                    case "RESTRICTIONS"     -> ctx.navigateTo(RestrictionsViewState.getInstance());
                    default -> throw new IllegalArgumentException("Unknown handleAction parameter - " + params[0]);
                }
            }
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    @Override
    public void enter(ViewContext viewContext) {

    }

    @Override
    public void exit(ViewContext viewContext) {

    }

    @Override
    public void render(ViewContext viewContext) {
        Student student = (Student) viewContext.getCurrentUser();
        System.out.println();
        new StudentDashboardWidget(student).render();
        System.out.println();
        new StudentProfilePanel(student).render();
        System.out.println();
        new StudentNotificationPanel().render();
        System.out.println();
        new StudentQuickActionsPanel().render();
        System.out.println();
        System.out.println("--- Navigation ---");
        List<NavItem> items = StudentNavMenuFactory.studentItems();
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, items.get(i).getLabel());
        }
        System.out.println();
        System.out.println("  Shortcuts: [s] Schedule  [r] Register  [g] Grades  [f] Financial Info");
        System.out.println("             [a] Aid  [t] Transcript  [e] Prog Eval  [p] Ed Plan");
        System.out.println("             [d] Documents  [h] Restrictions  [l] Logout  [b] Back  [q] Quit");
    }

    @Override
    public String getViewName() {
        return "STUDENT_DASHBOARD";
    }

    public static StudentDashboardViewState getInstance() {
        return instance;
    }
}
