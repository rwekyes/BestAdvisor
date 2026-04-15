package edu.advising.users;

import edu.advising.core.*;
/*
Admin concrete user type
 */
@Table(name = "admins", isSubTable = true)
public class Admin extends User{
    @Id
    @Column(name = "employee_id")
    private int employeeId;
    @Column(name = "access_level")
    private String accessLevel;

    @Override
    public void showDashboard() {
        System.out.println("\n=== ADMIN DASHBOARD ==="); //TODO: When the UI gets made, this is going to be submenus, for now I'm using it as a running list
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Access level: " + accessLevel);
        System.out.println("\nAvailable Features:");
        System.out.println("- View Users");
        System.out.println("- Add A User");
        System.out.println("- Remove A User");
        System.out.println("- Revoke User Access");
        System.out.println("- Reinstate User Access");
        System.out.println("- Edit A User");
        System.out.println("- View Transcript Requests");
        System.out.println("- Process Transcript");
        System.out.println("- Fail Transcript Process");
        System.out.println("- Send Transcript");
        System.out.println("- View Registration Dates");
        System.out.println("- Create Registration Dates");
        System.out.println("- Remove Registration Dates");
        System.out.println("- Edit Registration Dates");
        System.out.println("- View Sections");
        System.out.println("- Create Sections");
        System.out.println("- Remove Sections");
        System.out.println("- Edit Sections");
        System.out.println("- View Enrollments");
        System.out.println("- Create Enrollments");
        System.out.println("- Remove Enrollments");
        System.out.println("- Edit Enrollments");
        System.out.println("- View Waitlists");
        System.out.println("- Create Waitlists");
        System.out.println("- Remove Waitlists");
        System.out.println("- Edit Waitlists");
    }
}
