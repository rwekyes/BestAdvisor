package edu.advising.states.transcriptrequeststates;

import edu.advising.contexts.TranscriptRequestContext;
import edu.advising.states.TranscriptRequestState;

public class SentTranscriptRequestState implements TranscriptRequestState {

    private static final SentTranscriptRequestState instance = new SentTranscriptRequestState();

    private SentTranscriptRequestState(){};

    @Override
    public void process(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot process a SENT transcript request");
    }

    @Override
    public void ready(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot ready a SENT transcript request");
    }

    @Override
    public void send(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot send a SENT transcript request");
    }

    @Override
    public void cancel(TranscriptRequestContext ctx) {
        throw new IllegalStateException("Cannot cancel a SENT transcript request");
    }

    @Override
    public void fail(TranscriptRequestContext ctx, String reason) {
        throw new IllegalStateException("Cannot fail a SENT transcript request");
    }

    @Override
    public String getStatusName() {
        return "SENT";
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
