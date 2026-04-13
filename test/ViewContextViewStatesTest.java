import edu.advising.auth.AuthenticationContext;
import edu.advising.contexts.*;
import edu.advising.core.DatabaseManager;
import edu.advising.states.ViewState;
import edu.advising.states.viewstates.*;
import edu.advising.users.UserFactory;

import java.sql.SQLException;

public class ViewContextViewStatesTest {
    public static void main(String[] args){
        DatabaseManager db = DatabaseManager.getInstance();
        db.seedDatabase();

        UserFactory factory = new UserFactory();
        factory.createUser("STUDENT", "jsmith", "Password1!", "jsmith@school.edu", "John", "Smith", "S12345");
        factory.createUser("FACULTY", "prof.jones", "Password1!", "jones@school.edu", "Diana", "Jones", "E001", "Computer Science");

        ViewContext viewContext = null;

        viewContext = new ViewContext();


        System.out.println("---");
        System.out.println("Navigating to initial view -");
        viewContext.navigateTo(viewContext.getCurrentState());
        System.out.println("Should be GUEST -");
        System.out.println(viewContext.getCurrentState().getViewName());
        System.out.println("---");
        System.out.println("Navigating to registration view - Should throw an error");
        viewContext.navigateTo(RegistrationViewState.getInstance());
        System.out.println("---");
        System.out.println("Should be GUEST -");
        System.out.println(viewContext.getCurrentState().getViewName());
        System.out.println("---");
        System.out.println("Logging in -");
        viewContext.getCurrentState().handleAction(viewContext, "LOGIN", "jsmith", "Password1!", "127.0.0.1");
        System.out.println("Should be STUDENT -");
        System.out.println(viewContext.getCurrentState().getViewName());
        System.out.println("---");
        System.out.println("Navigating to registration view - ");
        viewContext.getCurrentState().handleAction(viewContext, "NAVIGATE", "REGISTRATION");
        System.out.println("Should be REGISTRATION -");
        System.out.println(viewContext.getCurrentState().getViewName());
        System.out.println("---");
        System.out.println("Navigating back -");
        viewContext.back();
        System.out.println("Should be STUDENT -");
        System.out.println(viewContext.getCurrentState().getViewName());
        System.out.println("---");
        System.out.println("Navigating to transcript view - ");
        viewContext.getCurrentState().handleAction(viewContext, "NAVIGATE", "TRANSCRIPT");
        System.out.println("Should be TRANSCRIPT -");
        System.out.println(viewContext.getCurrentState().getViewName());
        System.out.println("---");
        System.out.println("Printing the undo stack - ");
        for(ViewState v : viewContext.getUndo()){
            System.out.println(v.getViewName());
        }
        System.out.println("---");
        System.out.println("Printing the redo stack - ");
        for(ViewState v : viewContext.getRedo()){
            System.out.println(v.getViewName());
        }
        System.out.println("---");
        System.out.println("Logging out - ");
        viewContext.getCurrentState().handleAction(viewContext, "LOGOUT");
        System.out.println("Printing the undo stack - Should be empty");
        for(ViewState v : viewContext.getUndo()){
            System.out.println(v.getViewName());
        }
        System.out.println("---");
        System.out.println("Should be GUEST -");
        System.out.println(viewContext.getCurrentState().getViewName());
        System.out.println("---");
        System.out.println("Logging in -");
        viewContext.getCurrentState().handleAction(viewContext, "LOGIN", "prof.jones", "Password1!", "127.0.0.1");
        System.out.println("Should be FACULTY -");
        System.out.println(viewContext.getCurrentState().getViewName());

        db.shutdown();
    }
}
