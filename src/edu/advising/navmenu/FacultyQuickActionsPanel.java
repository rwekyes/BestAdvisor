package edu.advising.navmenu;

import java.util.List;

public class FacultyQuickActionsPanel implements QuickActionPanel{
    @Override
    public List<NavItem> getButtons() {
        return List.of(
                new NavItem("Class Roster", "ROSTER", NavIcons.ICON_NAV),
                new NavItem("Submit Grades", "SUBMIT_GRADES", NavIcons.ICON_NAV),
                new NavItem("My Schedule", "SCHEDULE", NavIcons.ICON_NAV),
                new NavItem("Budget Summary", "BUDGET_SUMMARY", NavIcons.ICON_NAV)
                );
    }

    @Override
    public void render() {
        System.out.println("--- Quick Actions ---");
        getButtons().forEach(b -> System.out.println("  • " + b.getLabel()));
    }
}
