package edu.advising.states.viewstates;

import edu.advising.commands.RegisterCommand;
import edu.advising.commands.RegistrationPeriod;
import edu.advising.commands.Section;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.ObservableStudent;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;

public class RegistrationViewState implements ViewState {

    private static final RegistrationViewState instance = new RegistrationViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "CHECK_STATUS" -> render(ctx, params[0], params[1]);
            case "REGISTER" -> {
                try {
                    int sectionId = Integer.parseInt(params[0]);
                    Section section = DatabaseManager.getInstance()
                            .fetchOne(Section.class, "id", sectionId);
                    if (section == null) {
                        System.out.println("Section not found.");
                        return;
                    }
                    ObservableStudent student = ObservableStudent.fromSuperType(
                            (Student) ctx.getCurrentUser()
                    );
                    RegisterCommand cmd = new RegisterCommand(
                            student, section, ctx.getPermissionTree()
                    );
                    ctx.getCommandExecutor().execute(cmd);
                } catch (SQLException e) {
                    System.out.println("Registration failed - database error.");
                }
            }
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException(
                    "Unknown handleAction command - " + command
            );
        };
    }

    @Override
    public void enter(ViewContext viewContext) {

    }

    @Override
    public void exit(ViewContext viewContext) {

    }

    @Override
    public void render(ViewContext viewContext) {

    }

    public void render(ViewContext viewContext, String p1, String p2) {
        RegistrationPeriod currentPeriod = viewContext.getRegistrationPeriodContext().getRegistrationPeriod();
        //TODO: Use the view template object to make the Strings needed
    }

    @Override
    public String getViewName() {
        return "REGISTRATION";
    }

    public static RegistrationViewState getInstance() {
        return instance;
    }
}
