package edu.advising.navmenu;

import java.util.List;

public interface ReportViewer {
    List<NavItem> getAvailableReports();
    void render();
}
