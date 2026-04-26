package edu.advising.navmenu;

import java.util.List;

public interface NotificationPanel {
    List<String> getFilteredTypes();
    void render();
}
