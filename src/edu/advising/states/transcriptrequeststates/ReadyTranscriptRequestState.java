package edu.advising.states.transcriptrequeststates;

import edu.advising.contexts.TranscriptRequestContext;
import edu.advising.states.TranscriptRequestState;

public class ReadyTranscriptRequestState implements TranscriptRequestState {

    private static final ReadyTranscriptRequestState instance = new ReadyTranscriptRequestState();

    private ReadyTranscriptRequestState(){};

    @Override
    public void process(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot process a READY transcript request");
    }

    @Override
    public void ready(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot ready a READY transcript request");
    }

    @Override
    public void send(TranscriptRequestContext ctx) {
        ctx.setState(SentTranscriptRequestState.getInstance());
        ctx.getNotificationManager().notifyTranscriptRequestStatusChange(ctx.getStudent(),"SENT");
    }

    @Override
    public void cancel(TranscriptRequestContext ctx) {
        ctx.setState(CancelledTranscriptRequestState.getInstance());
        ctx.getNotificationManager().notifyTranscriptRequestStatusChange(ctx.getStudent(),"CANCELLED");
    }

    @Override
    public void fail(TranscriptRequestContext ctx, String reason) {
        throw new IllegalStateException("Cannot fail a READY transcript request");
    }

    @Override
    public String getStatusName() {
        return "READY";
    }

    public static TranscriptRequestState getInstance() {
        return instance;
    }

    @Override
    public boolean canProcess() {
        return false;
    }

    @Override
    public boolean canReady() {
        return false;
    }

    @Override
    public boolean canSend() {
        return true;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public boolean canFail() {
        return false;
    }
}
