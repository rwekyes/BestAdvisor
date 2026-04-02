package edu.advising.contexts;

import edu.advising.auth.AuthenticationContext;
import edu.advising.auth.BasicAuthentication;
import edu.advising.commands.Command;
import edu.advising.commands.CommandExecutor;
import edu.advising.states.ViewState;
import edu.advising.states.viewstates.GuestViewState;

import java.util.ArrayList;

public class ViewContext {
    private ArrayList<ViewState> undo;
    private ArrayList<ViewState> redo;
    private ViewState currentState;
    private AuthenticationContext authContext;
    private CommandExecutor commandExecutor;

    public ViewContext(){
        this.authContext = new AuthenticationContext(new BasicAuthentication());
        this.currentState = GuestViewState.getInstance();
        this.currentState.enter(this);
    }

    public void setCommand(Command c){

    }

    public ViewState getCurrentState(){
        return currentState;
    }

    public void navigateTo(ViewState s){
        if(s.requiresAuthentication()){
            //above this, in the if statement, add an additional condition that does the auth check
        } else {
            currentState.exit();
            currentState = new GuestViewState();

        }
        currentState.exit();
        undo.add(currentState);
        s.enter(this);
        s.render();
    }

    public void execute(){

    }

    public void logout(){
        currentState.exit();
        currentState = new GuestViewState();
    }

    public void back(){
        redo.add(currentState);
        currentState.exit();
        currentState = undo.getLast();
        undo.remove(undo.getLast());
        currentState.enter(this);
        currentState.render();
    }

    public void start(){

    }

}
