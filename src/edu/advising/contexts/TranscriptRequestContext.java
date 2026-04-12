package edu.advising.contexts;

import edu.advising.commands.TranscriptRequest;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.NotificationManager;
import edu.advising.states.StateFactory;
import edu.advising.states.TranscriptRequestState;
import edu.advising.states.transcriptrequeststates.PendingTranscriptRequestState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.Random;

public class TranscriptRequestContext {

    private TranscriptRequestState state;
    private TranscriptRequest transcriptRequest;
    private Student student;
    private NotificationManager notificationManager;

    private TranscriptRequestContext(TranscriptRequest transcriptRequest, Student student) {
        this.transcriptRequest = transcriptRequest;
        this.student = student;
        this.notificationManager = NotificationManager.getInstance();
        this.state = StateFactory.transcriptRequestStateFor(transcriptRequest.getStatus());
    }

    public static TranscriptRequestContext create(Student student) throws SQLException, IllegalAccessException {
        TranscriptRequest t = new TranscriptRequest(student);
        t.setTrackingNumber(generateTrackingNumber());
        t.setStatus("PENDING");
        t.setRequestType("OFFICIAL");
        DatabaseManager.getInstance().upsert(t);
        return new TranscriptRequestContext(t, student);
    }

    public static TranscriptRequestContext load(TranscriptRequest transcriptRequest, Student student){
        return new TranscriptRequestContext(transcriptRequest, student);
    }

    public void setState(TranscriptRequestState newState){
        this.state = newState;
        this.transcriptRequest.setStatus(newState.getStatusName());
    }

    public void persist(){
        if (transcriptRequest.getId() == 0) return;  // in-memory only, no DB row to update
        try {
            DatabaseManager.getInstance().upsert(transcriptRequest);
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Failed to persist transcript request state - ", e);
        }
    }

    public static String generateTrackingNumber(){
        int random = new Random().nextInt(90000000) + 10000000; // TODO: Make tracking numbers sequential or calculated from DateTime
        String trackingNumber = "TR-"+String.valueOf(random);
        return trackingNumber;
    }

    public TranscriptRequestState getState(){return state;}
    public TranscriptRequest getTranscriptRequest(){return transcriptRequest;}
    public NotificationManager getNotificationManager(){return notificationManager;}
    public Student getStudent(){return student;}

    public void cancel(){
        state.cancel(this);
        persist();
    }

    public void fail(String reason){
        state.fail(this, reason);
        persist();
    }

    public void process(){
        state.process(this);
        persist();
    }

    public void ready(){
        state.ready(this);
        persist();
    }

    public void send(){
        state.send(this);
        persist();
    }

    public boolean canProcess(){return state.canProcess();}
    public boolean canReady(){return state.canReady();}
    public boolean canSend(){return state.canSend();}
    public boolean canCancel(){return state.canCancel();}
    public boolean canFail(){return state.canFail();}
}
