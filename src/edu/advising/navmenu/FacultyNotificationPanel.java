package edu.advising.navmenu;

import edu.advising.notifications.NotificationTypes;

import java.util.ArrayList;
import java.util.List;

public class FacultyNotificationPanel implements NotificationPanel{
    @Override
    public List<String> getFilteredTypes() {
        List<String> items = new ArrayList<>();
        items.addAll(List.of(
                NotificationTypes.GRADE_DEADLINE,
                NotificationTypes.ROSTER_CHANGE,
                NotificationTypes.BUDGET_ALERT
        ));
        return items;
    }

    @Override
    public void render() {
        System.out.println("--- Notifications ---");
        getFilteredTypes().forEach(t -> System.out.println("  • " + t));
    }
}
