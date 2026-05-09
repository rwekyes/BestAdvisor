package edu.advising.navmenu;

import java.util.List;

public class StudentQuickActionsPanel implements QuickActionPanel{
    @Override
    public List<NavItem> getButtons() {
        return List.of(
                new NavItem("Register For Classes", "REGISTER", NavIcons.ICON_NAV),
                new NavItem("Make a Payment", "PAYMENT", NavIcons.ICON_NAV),
                new NavItem("Grades",                "GRADES", NavIcons.ICON_NAV),
                new NavItem("Transcript", "TRANSCRIPT", NavIcons.ICON_NAV)
                );
    }

    @Override
    public void render() {
        System.out.println("--- Quick Actions ---");
        getButtons().forEach(b -> System.out.println("  • " + b.getLabel()));
    }
}
