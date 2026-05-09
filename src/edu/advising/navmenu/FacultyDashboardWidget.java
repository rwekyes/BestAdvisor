package edu.advising.navmenu;

import edu.advising.users.Faculty;

public class FacultyDashboardWidget implements DashboardWidget{

    private Faculty faculty;

    public FacultyDashboardWidget(Faculty faculty){
        this.faculty = faculty;
    }

    @Override
    public void render() {
        System.out.println("=== Faculty Dashboard ===");
        System.out.printf("  Welcome, %s%n", faculty.getFullName());
        System.out.printf("  Dept: %-22s  Office: %s%n",
                faculty.getDepartment() != null ? faculty.getDepartment() : "—",
                faculty.getOfficeLocation() != null ? faculty.getOfficeLocation() : "—");
    }

    @Override
    public void refresh() {

    }
}
