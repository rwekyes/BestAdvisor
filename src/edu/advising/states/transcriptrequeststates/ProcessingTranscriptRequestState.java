package edu.advising.states.transcriptrequeststates;

import edu.advising.contexts.TranscriptRequestContext;
import edu.advising.states.TranscriptRequestState;

public class ProcessingTranscriptRequestState implements TranscriptRequestState {

    private static final ProcessingTranscriptRequestState instance = new ProcessingTranscriptRequestState();

    private ProcessingTranscriptRequestState(){};

    @Override
    public void process(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot process a PROCESSING transcript request");
    }

    @Override
    public void ready(TranscriptRequestContext ctx) {
        ctx.setState(ReadyTranscriptRequestState.getInstance());
        ctx.getNotificationManager().notifyTranscriptRequestStatusChange(ctx.getStudent(),"READY");
    }

    @Override
    public void send(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot send a PROCESSING transcript request");
    }

    @Override
    public void cancel(TranscriptRequestContext ctx) {
        ctx.setState(CancelledTranscriptRequestState.getInstance());
        ctx.getNotificationManager().notifyTranscriptRequestStatusChange(ctx.getStudent(),"CANCELLED");
    }

    @Override
    public void fail(TranscriptRequestContext ctx, String reason) {
        ctx.setState(FailedTranscriptRequestState.getInstance());
        ctx.getNotificationManager().notifyTranscriptRequestStatusChange(ctx.getStudent(),"FAILED");
    }

    @Override
    public String getStatusName() {
        return "PROCESSING";
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
        return true;
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
        return true;
    }
}
