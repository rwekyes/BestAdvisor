package edu.advising.states.transcriptrequeststates;

import edu.advising.contexts.TranscriptRequestContext;
import edu.advising.states.TranscriptRequestState;

public class CancelledTranscriptRequestState implements TranscriptRequestState {

    private static final CancelledTranscriptRequestState instance = new CancelledTranscriptRequestState();

    private CancelledTranscriptRequestState(){};

    @Override
    public void process(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot process a CANCELLED transcript request");
    }

    @Override
    public void ready(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot ready a CANCELLED transcript request");
    }

    @Override
    public void send(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot send a CANCELLED transcript request");
    }

    @Override
    public void cancel(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot cancel a CANCELLED transcript request");
    }

    @Override
    public void fail(TranscriptRequestContext ctx, String reason) {
        throw new IllegalStateException("Cannot fail a CANCELLED transcript request");
    }

    @Override
    public String getStatusName() {
        return "CANCELLED";
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
