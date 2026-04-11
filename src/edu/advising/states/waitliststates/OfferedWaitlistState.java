package edu.advising.states.waitliststates;

import edu.advising.commands.CommandExecutor;
import edu.advising.commands.RegisterCommand;
import edu.advising.commands.Section;
import edu.advising.commands.WaitlistEntry;
import edu.advising.contexts.EnrollmentContext;
import edu.advising.contexts.WaitlistContext;
import edu.advising.notifications.ObservableStudent;
import edu.advising.states.WaitlistState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class OfferedWaitlistState implements WaitlistState {

    private static final OfferedWaitlistState instance = new OfferedWaitlistState();

    private OfferedWaitlistState() {

    }

    public static OfferedWaitlistState getInstance(){
        return instance;
    }

    @Override
    public void offer(WaitlistContext ctx, int expiryHours) {
        throw new IllegalStateException("Cannot offer an OFFERED waitlist entry");
    }

    @Override
    public void accept(WaitlistContext ctx) {
        // Check if the seat is taken before acceptance
        if (!ctx.getSection().hasCapacity()) {
            ctx.setState(ExpiredWaitlistState.getInstance());
            return;
        }

        ctx.getCommandExecutor().execute(
                new RegisterCommand(ctx.getStudent(), ctx.getSection())
        );
        ctx.setState(EnrolledFromWaitlistState.getInstance());
    }

    @Override
    public void decline(WaitlistContext ctx) {
        ctx.setState(RemovedWaitlistState.getInstance());

        // Promote next student if section has capacity
        try {
            Section section = ctx.getSection();
            List<WaitlistEntry> waitlist = section.getWaitlist();
            // Find the next ACTIVE entry by position
            waitlist.stream()
                    .filter(e -> "ACTIVE".equals(e.getStatus()))
                    .min(Comparator.comparingInt(WaitlistEntry::getPosition))
                    .ifPresent(next -> {
                        try {
                            Student nextStudent = next.getStudent();
                            ObservableStudent nextObs = ObservableStudent.fromSuperType(nextStudent);
                            WaitlistContext nextCtx = WaitlistContext.fromEntry(
                                    next, section, nextObs,
                                    new CommandExecutor(nextStudent.getId()));
                            nextCtx.offer(24);
                        } catch (SQLException e) {
                            System.err.println("Failed to promote next waitlisted student: " + e.getMessage());
                        }
                    });
        } catch (SQLException e) {
            System.err.println("Failed to fetch waitlist for promotion: " + e.getMessage());
        }
    }

    @Override
    public void remove(WaitlistContext ctx, String reason) {
        ctx.setState(RemovedWaitlistState.getInstance());
        // Remove that student from the waitlist
        ctx.getSection().removeFromWaitlist(ctx.getStudent());
    }

    @Override
    public void expire(WaitlistContext ctx) {
        ctx.setState(ExpiredWaitlistState.getInstance());
        // Remove that student from the waitlist
        ctx.getSection().removeFromWaitlist(ctx.getStudent());
    }

    @Override
    public boolean isActivelyWaiting() {
        return true;
    }

    @Override
    public String getStatusName() {
        return "OFFERED";
    }
}
