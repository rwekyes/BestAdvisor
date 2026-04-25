package edu.advising.states.enrollmentstates;

import edu.advising.commands.CommandExecutor;
import edu.advising.model.WaitlistEntry;
import edu.advising.contexts.EnrollmentContext;
import edu.advising.contexts.WaitlistContext;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.ObservableStudent;
import edu.advising.states.EnrollmentState;

import java.sql.SQLException;

public class WaitlistedEnrollmentState implements EnrollmentState {

    private static final WaitlistedEnrollmentState instance = new WaitlistedEnrollmentState();

    private WaitlistedEnrollmentState() {}

    public static WaitlistedEnrollmentState getInstance(){
        return instance;
    }

    @Override
    public void confirm(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot confirm a WAITLISTED enrollment");
    }

    @Override
    public void drop(EnrollmentContext ctx) throws SQLException{
        drop(ctx, "UNKNOWN");
    }

    @Override
    public void withdraw(EnrollmentContext ctx) throws SQLException{
        // TODO: Waitlist story — dropping from waitlist removes the WaitlistEntry
        String sql = "SELECT * FROM waitlist WHERE student_id = ? AND section_id = ? AND status = 'ACTIVE'";
        WaitlistEntry entry = DatabaseManager.getInstance().fetch(sql, rs -> {
            WaitlistEntry e = new WaitlistEntry();
            e.setId(rs.getInt("id"));
            e.setStudentId(rs.getInt("student_id"));
            e.setSectionId(rs.getInt("section_id"));
            e.setStatus(rs.getString("status"));
            return e;
        }, ctx.getStudent().getId(), ctx.getSection().getId());

        if (entry == null) return; // student wasn't on waitlist, nothing to clean up

        ObservableStudent obs = ObservableStudent.fromSuperType(ctx.getStudent());
        WaitlistContext wCtx = WaitlistContext.fromEntry(
                entry, ctx.getSection(), obs, new CommandExecutor(ctx.getStudent().getId())
        );
        wCtx.remove("WITHDRAWN");
    }

    @Override
    public void complete(EnrollmentContext ctx, String finalGrade) {
        throw new IllegalStateException("Cannot complete a WAITLISTED enrollment");
    }

    @Override
    public void reenroll(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot reenroll a WAITLISTED enrollment");
    }

    @Override
    public boolean canDrop() {
        return true;
    }

    @Override
    public boolean canWithdraw() {
        return false;
    }

    @Override
    public boolean canComplete() {
        return false;
    }

    @Override
    public boolean canReenroll() {
        return false;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public String getStatusName() {
        return "WAITLISTED";
    }

    @Override
    public void drop(EnrollmentContext ctx, String reason) throws SQLException {
        // TODO: Waitlist story — dropping from waitlist removes the WaitlistEntry
        String sql = "SELECT * FROM waitlist WHERE student_id = ? AND section_id = ? AND status = 'ACTIVE'";
        WaitlistEntry entry = DatabaseManager.getInstance().fetch(sql, rs -> {
            WaitlistEntry e = new WaitlistEntry();
            e.setId(rs.getInt("id"));
            e.setStudentId(rs.getInt("student_id"));
            e.setSectionId(rs.getInt("section_id"));
            e.setStatus(rs.getString("status"));
            return e;
        }, ctx.getStudent().getId(), ctx.getSection().getId());

        if (entry == null) return; // student wasn't on waitlist, nothing to clean up

        ObservableStudent obs = ObservableStudent.fromSuperType(ctx.getStudent());
        WaitlistContext wCtx = WaitlistContext.fromEntry(
                entry, ctx.getSection(), obs, new CommandExecutor(ctx.getStudent().getId())
        );
        wCtx.remove("DROPPED");
    }
}
