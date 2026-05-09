package edu.advising.navmenu;

import edu.advising.users.Student;

public class StudentDashboardWidget implements DashboardWidget{

    private Student student;

    public StudentDashboardWidget(Student student){
        this.student = student;
    }

    @Override
    public void render() {
        System.out.println("=== Student Dashboard ===");
        System.out.printf("  Welcome, %s%n", student.getFullName());
        System.out.printf("  ID: %-14s  GPA: %-6s  Credits: %d%n",
                student.getStudentId(),
                student.getGpa() != null ? student.getGpa().toPlainString() : "N/A",
                student.getCreditsEarned());
    }

    @Override
    public void refresh() {

    }
}
