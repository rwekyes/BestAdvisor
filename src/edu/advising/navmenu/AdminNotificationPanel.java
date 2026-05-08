package edu.advising.navmenu;

import edu.advising.notifications.NotificationTypes;

import java.util.List;

public class AdminNotificationPanel implements NotificationPanel {
    @Override
    public List<String> getFilteredTypes() {
        return List.of(
                NotificationTypes.GRADE_CHANGE,
                NotificationTypes.GRADE_DEADLINE,
                NotificationTypes.REGISTRATION,
                NotificationTypes.FINANCIAL_AID,
                NotificationTypes.ROSTER_CHANGE,
                NotificationTypes.BUDGET_ALERT
        );
    }

    @Override
    public void render() {
        System.out.println("--- Notification Channels ---");
        getFilteredTypes().forEach(t -> System.out.println("  • " + t));
    }
}
