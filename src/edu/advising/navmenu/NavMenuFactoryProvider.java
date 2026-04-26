package edu.advising.navmenu;

import edu.advising.users.*;

public class NavMenuFactoryProvider {
    public static NavMenuFactory getFactory(User user){
        switch(user.getUserType()){
            case "ADMIN" -> {
                return new AdminNavMenuFactory();
            }
            case "FACULTY" -> {
                return new FacultyNavMenuFactory((Faculty) user);
            }
            case "STUDENT" -> {
                return new StudentNavMenuFactory((Student) user);
            }
            default -> throw new IllegalArgumentException("Error - unknown userType - "+ user.getUserType());
        }
    }
}
