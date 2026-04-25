package edu.advising.contexts;

import edu.advising.model.RegistrationPeriod;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.NotificationManager;
import edu.advising.states.RegistrationState;
import edu.advising.states.StateFactory;

import java.sql.SQLException;

public class RegistrationPeriodContext {

    private RegistrationState state;
    private RegistrationPeriod registrationPeriod;
    private NotificationManager notificationManager;

    private RegistrationPeriodContext(RegistrationPeriod registrationPeriod){
        this.registrationPeriod = registrationPeriod;
        this.notificationManager = NotificationManager.getInstance();
        this.state = StateFactory.registrationStateFor(registrationPeriod.getCurrentState());
    }

    public static RegistrationPeriodContext currentPeriod(String semester, int year) throws SQLException {
        RegistrationPeriodContext ctx = forPeriod(semester, year);
        if (ctx != null) ctx.checkAndAdvance();
        return ctx; // view will handle nulls
    }

    // forPeriod grabs an existing registration period from the db. To actually create one in the app, it will be on the admin dashboard
    public static RegistrationPeriodContext forPeriod(String semester, int year) throws SQLException {
        String sql = "SELECT * FROM registration_periods WHERE semester = ? AND `year` = ?";
        RegistrationPeriod period = DatabaseManager.getInstance().fetch(sql, rs -> {
            RegistrationPeriod p = new RegistrationPeriod();
            p.setId(rs.getInt("id"));
            p.setSemester(rs.getString("semester"));
            p.setYear(rs.getInt("year"));
            p.setOpenDate(rs.getTimestamp("open_date").toLocalDateTime());
            p.setCloseDate(rs.getTimestamp("close_date").toLocalDateTime());
            var late = rs.getTimestamp("late_registration_end");
            if (late != null) p.setLateRegistrationEnd(late.toLocalDateTime());
            p.setCurrentState(rs.getString("current_state"));
            return p;
        }, semester, year);

        if (period == null) return null; // no period configured for this semester/year view will have to handle nulls
        return new RegistrationPeriodContext(period);
    }

    // persist needs to be public for Admin calls
    public void persist(){
        if (registrationPeriod.getId() == 0) return;  // in-memory only, no DB row to update
        try {
            DatabaseManager.getInstance().upsert(registrationPeriod);
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Failed to persist registration period state - ", e);
        }
    }

    public void checkAndAdvance() {
        state.checkAndAdvance(this);
        persist();
    }

    public void setState(RegistrationState newState){
        this.state = newState;
        this.registrationPeriod.setCurrentState(newState.getStatusName());
    }

    public boolean canRegister() { return state.canRegister(this); }
    public boolean canDrop()     { return state.canDrop(this); }

    public RegistrationState getState(){return state;}
    public String getStateName(){return state.getStatusName();}

    public void setRegistrationPeriod(RegistrationPeriod p){
        this.registrationPeriod = p;
    }
    public RegistrationPeriod getRegistrationPeriod(){return registrationPeriod;}

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
}
