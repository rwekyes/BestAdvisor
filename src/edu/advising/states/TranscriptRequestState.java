package edu.advising.states;

import edu.advising.contexts.TranscriptRequestContext;

public interface TranscriptRequestState {
    void process(TranscriptRequestContext ctx);
    void ready(TranscriptRequestContext ctx);
    void send(TranscriptRequestContext ctx);
    void cancel(TranscriptRequestContext ctx);
    void fail(TranscriptRequestContext ctx, String reason);
    String getStatusName();

    boolean canProcess();
    boolean canReady();
    boolean canSend();
    boolean canCancel();
    boolean canFail();
}
