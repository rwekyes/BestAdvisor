package edu.advising.contexts;

import edu.advising.auth.AuthenticationContext;
import edu.advising.auth.BasicAuthentication;
import edu.advising.commands.Command;
import edu.advising.commands.CommandExecutor;
import edu.advising.states.ViewState;
import edu.advising.states.viewstates.GuestViewState;
import edu.advising.states.viewstates.RegistrationViewState;

import java.util.ArrayList;

public class ViewContext {
    private ArrayList<ViewState> undo;
    private ArrayList<ViewState> redo;
    private ViewState currentState;
    private AuthenticationContext authContext;
    private FacultyPermissionContext facultyPermissionContext;
    private RegistrationPeriodContext registrationPeriodContext;
    private CommandExecutor commandExecutor;

    public ViewContext(){
        this.authContext = new AuthenticationContext(new BasicAuthentication());
        this.facultyPermissionContext = new FacultyPermissionContext();
        this.registrationPeriodContext = new RegistrationPeriodContext();
        this.currentState = GuestViewState.getInstance();
        this.currentState.enter(this);
        this.undo = new ArrayList<ViewState>();
        this.redo = new ArrayList<ViewState>();
    }

    public void setCommand(Command c){

    }

    public ViewState getCurrentState(){
        return currentState;
    }

    // TODO: set up the verification logic
    public void navigateTo(ViewState s){
        if(s.requiresAuthentication() && !authContext.verify("","").isFullyAuthenticated()){
            System.out.println("Error - Unauthorized Access Attempt!");
            logout();
        } else { // TODO: maybe a login here? maybe not
            currentState.exit(this);
            undo.add(currentState);
            s.enter(this);
            s.render(this);
        }
    }

    public void execute(){

    }

    public void logout(){
        undo.removeAll(undo);
        redo.removeAll(redo);
        authContext.logout();
        currentState.exit(this);
        currentState = GuestViewState.getInstance();
    }

    public void back(){
        redo.add(currentState);
        currentState.exit(this);
        currentState = undo.getLast();
        undo.remove(undo.getLast());
        currentState.enter(this);
        currentState.render(this);
    }

    public void start(){

    }

    public ArrayList<ViewState> getUndo(){
        return undo;
    }

    public AuthenticationContext getAuthContext() {
        return authContext;
    }

    public FacultyPermissionContext getFacultyPermissionContext() {
        return facultyPermissionContext;
    }

    public RegistrationPeriodContext getRegistrationPeriodContext() {
        return registrationPeriodContext;
    }
}
