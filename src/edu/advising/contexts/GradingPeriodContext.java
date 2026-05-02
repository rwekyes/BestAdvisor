package edu.advising.contexts;

import edu.advising.core.DatabaseManager;
import edu.advising.model.GradingPeriod;
import edu.advising.notifications.NotificationManager;

import java.sql.SQLException;
import java.util.Map;

public class GradingPeriodContext {
    private GradingPeriod gradingPeriod;
    private NotificationManager notificationManager;

    private GradingPeriodContext(GradingPeriod gradingPeriod){
        this.gradingPeriod = gradingPeriod;
        this.notificationManager = NotificationManager.getInstance();
    }

    public static GradingPeriodContext currentPeriod(String semester, int year) throws SQLException{
        GradingPeriod period = DatabaseManager.getInstance().fetchOne(
                GradingPeriod.class, Map.of("semester", semester, "year", year));

        if (period == null) return null; // no period configured for this semester/year view will have to handle nulls
        return new GradingPeriodContext(period);
    }

    public GradingPeriod getGradingPeriod(){
        return gradingPeriod;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
