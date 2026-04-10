import edu.advising.auth.AuthenticationContext;
import edu.advising.contexts.*;
import edu.advising.states.ViewState;
import edu.advising.states.viewstates.*;

public class ViewContextViewStatesTest {
    public static void main(String[] args){
        ViewContext viewContext = new ViewContext();
        viewContext.navigateTo(viewContext.getCurrentState());
        viewContext.navigateTo(RegistrationViewState.getInstance()); // Should throw an error
        viewContext.getCurrentState().getViewName(); // Should be Guest
        viewContext.getCurrentState().handleAction(viewContext, "LOGIN", "jsmith", "Password1!", "127.0.0.1");
        System.out.println(viewContext.getCurrentState().getViewName()); // Should be STUDENT
        viewContext.getCurrentState().handleAction(viewContext, "NAVIGATE", "REGISTRATION");
        System.out.println(viewContext.getCurrentState().getViewName()); // Should be REGISTRATION
        viewContext.back();
        System.out.println(viewContext.getCurrentState().getViewName()); // Should be STUDENT
        viewContext.getCurrentState().handleAction(viewContext, "NAVIGATE", "TRANSCRIPT");
        for(ViewState v : viewContext.getUndo()){
            System.out.println(v.getViewName()); // Should print the undo stack
        }
        viewContext.getCurrentState().handleAction(viewContext, "LOGOUT");
        for(ViewState v : viewContext.getUndo()){
            System.out.println(v.getViewName()); // Should print the empty undo stack, might have Guest in it
        }
        System.out.println(viewContext.getCurrentState().getViewName()); // Should be GUEST
        viewContext.getCurrentState().handleAction(viewContext, "LOGIN", "prof.jones", "Password1!", "127.0.0.1");
        System.out.println(viewContext.getCurrentState().getViewName()); // Should be FACULTY

    }
}
