package edu.advising.states;

import edu.advising.contexts.ViewContext;

public interface ViewState {

    public abstract boolean requiresAuthentication();

    public abstract void handleAction();

    public abstract void enter(ViewContext viewContext);

    public abstract void exit();

    public abstract void render();

    public abstract String getViewName();

}
