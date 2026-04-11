package edu.advising.states.transcriptrequeststates;

import edu.advising.contexts.TranscriptRequestContext;
import edu.advising.states.TranscriptRequestState;

public class PendingTranscriptRequestState implements TranscriptRequestState {

    private static final PendingTranscriptRequestState instance = new PendingTranscriptRequestState();

    private PendingTranscriptRequestState(){};

    @Override
    public void process(TranscriptRequestContext ctx) {
        ctx.setState(ProcessingTranscriptRequestState.getInstance());
        ctx.getNotificationManager().notifyTranscriptRequestStatusChange(ctx.getStudent(),"PROCESSING");
    }

    @Override
    public void ready(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot ready a PENDING transcript request");
    }

    @Override
    public void send(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot send a PENDING transcript request");
    }

    @Override
    public void cancel(TranscriptRequestContext ctx) {

    }

    @Override
    public void fail(TranscriptRequestContext ctx, String reason) {
        throw new IllegalStateException("Cannot fail a PENDING transcript request");
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }

    public static TranscriptRequestState getInstance() {
        return instance;
    }

    @Override
    public boolean canProcess() {
        return true;
    }

    @Override
    public boolean canReady() {
        return false;
    }

    @Override
    public boolean canSend() {
        return false;
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
