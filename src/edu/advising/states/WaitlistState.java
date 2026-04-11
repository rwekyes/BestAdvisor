package edu.advising.states;

import edu.advising.contexts.WaitlistContext;

public interface WaitlistState extends State {
    void offer(WaitlistContext ctx, int expiryHours);
    void accept(WaitlistContext ctx);
    void decline(WaitlistContext ctx);
    void remove(WaitlistContext ctx, String reason);
    void expire(WaitlistContext ctx);
    boolean isActivelyWaiting();

    String getStatusName();
}
