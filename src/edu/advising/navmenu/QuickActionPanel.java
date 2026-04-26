package edu.advising.navmenu;

import java.util.List;

public interface QuickActionPanel {
    List<NavItem> getButtons();
    void render();
}
