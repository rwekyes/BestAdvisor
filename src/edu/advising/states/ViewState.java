package edu.advising.states;

import edu.advising.contexts.ViewContext;

public interface ViewState {

    public abstract boolean requiresAuthentication();

    public abstract void handleAction();

    public abstract void handleAction(ViewContext viewContext, String command);

    public abstract void handleAction(ViewContext viewContext, String command1, String command2);

    public abstract void handleAction(ViewContext viewContext, String command, String p1, String p2, String p3);

    public abstract void enter(ViewContext viewContext);

    public abstract void exit(ViewContext viewContext);

    public abstract void render(ViewContext viewContext);

    public abstract String getViewName();

}
