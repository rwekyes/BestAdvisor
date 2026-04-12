package edu.advising.states.transcriptrequeststates;

import edu.advising.contexts.TranscriptRequestContext;
import edu.advising.states.TranscriptRequestState;

public class FailedTranscriptRequestState implements TranscriptRequestState {

    private static final FailedTranscriptRequestState instance = new FailedTranscriptRequestState();

    private FailedTranscriptRequestState(){};

    @Override
    public void process(TranscriptRequestContext ctx) {
        //TODO: Registrar logic, will need to check for admin access. Still need to make an Admin user type.
        ctx.setState(ProcessingTranscriptRequestState.getInstance());
        ctx.getNotificationManager().notifyTranscriptRequestStatusChange(ctx.getStudent(),"PROCESSING");
    }

    @Override
    public void ready(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot ready a FAILED transcript request");
    }

    @Override
    public void send(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot send a FAILED transcript request");
    }

    @Override
    public void cancel(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot cancel a FAILED transcript request");
    }

    @Override
    public void fail(TranscriptRequestContext ctx, String reason) {
        throw new IllegalStateException("Cannot fail a FAILED transcript request");
    }

    @Override
    public String getStatusName() {
        return "FAILED";
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
        return false;
    }

    @Override
    public boolean canFail() {
        return false;
    }
}
