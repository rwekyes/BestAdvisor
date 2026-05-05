package edu.advising.navmenu;

import edu.advising.users.Faculty;

import java.util.List;

public class FacultyProfilePanel implements ProfilePanel{

    private Faculty faculty;

    public FacultyProfilePanel(Faculty faculty){
        this.faculty = faculty;
    }

    @Override
    public List<ProfileField> getFields() {
        return List.of(
                new ProfileField("Instructor", faculty.getFullName(), false),
                new ProfileField("Id", faculty.getEmployeeId(), false),
                new ProfileField("Department", faculty.getDepartment(), false),
                new ProfileField("Title", faculty.getTitle(), false),
                new ProfileField("Office", faculty.getOfficeLocation(), false),
                new ProfileField("Office Hours", faculty.getOfficeHours(), false)
        );
    }

    @Override
    public void render() {

    }
}
