package edu.advising.navmenu;

import edu.advising.users.Faculty;

public class FacultyDashboardWidget implements DashboardWidget{

    private Faculty faculty;

    public FacultyDashboardWidget(Faculty faculty){
        this.faculty = faculty;
    }

    @Override
    public void render() {

    }

    @Override
    public void refresh() {

    }
}
