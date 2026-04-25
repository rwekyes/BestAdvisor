package edu.advising.contexts;

import edu.advising.commands.CommandExecutor;
import edu.advising.model.Section;
import edu.advising.model.WaitlistEntry;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.NotificationManager;
import edu.advising.notifications.ObservableStudent;
import edu.advising.states.StateFactory;
import edu.advising.states.WaitlistState;

import java.sql.SQLException;

public class WaitlistContext {

    private WaitlistState state;
    private WaitlistEntry entry;
    private Section section;
    private ObservableStudent student;
    private CommandExecutor commandExecutor;
    private NotificationManager notificationManager;

    private WaitlistContext(WaitlistEntry entry, Section section,
                            ObservableStudent student, CommandExecutor executor) {
        this.entry = entry;
        this.section = section;
        this.student = student;
        this.commandExecutor = executor;
        this.state = StateFactory.waitlistStateFor(entry.getStatus());
        this.notificationManager = NotificationManager.getInstance();
    }

    // Constructing this needs a CommandExecutor from the ViewContext
    public static WaitlistContext fromEntry(WaitlistEntry entry, Section section,
                                            ObservableStudent student, CommandExecutor executor){
        return new WaitlistContext(entry, section, student, executor);
    }

    public void persist(){
        if (entry.getId() == 0) return;
        try { DatabaseManager.getInstance().upsert(entry); }
        catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Failed to persist waitlist state", e);
        }
    }

    public void setState(WaitlistState s) {
        this.state = s;
        this.entry.setStatus(s.getStatusName());
    }

    // Action block
    public void offer(int expiryHours) {
        state.offer(this, expiryHours);
        persist();
    }
    public void accept()               {
        state.accept(this);
        persist();
    }
    public void decline()              {
        state.decline(this);
        persist();
    }
    public void remove(String reason)  {
        state.remove(this, reason);
        persist();
    }
    public void expire()               {
        state.expire(this);
        persist();
    }
    public boolean isActivelyWaiting() { return state.isActivelyWaiting(); }

    // Getters
    public WaitlistState getState() {
        return state;
    }
    public Section getSection() { return section; }
    public ObservableStudent getStudent() { return student; }
    public CommandExecutor getCommandExecutor() { return commandExecutor; }
    public NotificationManager getNotificationManager() { return notificationManager; }
    public WaitlistEntry getEntry() {
        return entry;
    }
}
