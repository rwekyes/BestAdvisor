package edu.advising.navmenu;

import java.util.List;

public class AdminQuickActionsPanel implements QuickActionPanel {
    @Override
    public List<NavItem> getButtons() {
        return List.of(
                new NavItem("Manage Users",               "USER_MGMT",            NavIcons.ICON_NAV),
                new NavItem("Audit Log",                  "AUDIT_LOG",            NavIcons.ICON_NAV),
                new NavItem("Advance Registration State", "ADVANCE_REGISTRATION", NavIcons.ICON_NAV),
                new NavItem("Manage Restrictions",        "RESTRICTION_MGMT",     NavIcons.ICON_NAV)
        );
    }

    @Override
    public void render() {
        System.out.println("--- Quick Actions ---");
        List<NavItem> buttons = getButtons();
        for (int i = 0; i < buttons.size(); i++) {
            System.out.printf("  [%d] %-30s  (%s)%n", i + 1, buttons.get(i).getLabel(), buttons.get(i).getSceneKey());
        }
    }
}
