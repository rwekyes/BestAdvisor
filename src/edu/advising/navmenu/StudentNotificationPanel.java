package edu.advising.navmenu;

import edu.advising.notifications.NotificationTypes;

import java.util.ArrayList;
import java.util.List;

public class StudentNotificationPanel implements NotificationPanel{
    @Override
    public List<String> getFilteredTypes() {
        List<String> items = new ArrayList<>();
        items.addAll(List.of(
                NotificationTypes.GRADE_CHANGE,
                NotificationTypes.REGISTRATION,
                NotificationTypes.FINANCIAL_AID
        ));
        return items;
    }

    @Override
    public void render() {
        System.out.println("--- Notifications ---");
        getFilteredTypes().forEach(t -> System.out.println("  • " + t));
    }
}
