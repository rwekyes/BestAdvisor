package edu.advising.states;

import edu.advising.contexts.ViewContext;

public interface ViewState {
    void handleAction(ViewContext ctx, String command, String... params);
    void enter(ViewContext ctx);
    void exit(ViewContext ctx);
    void render(ViewContext ctx);
    boolean requiresAuthentication();
    String getViewName();
}
