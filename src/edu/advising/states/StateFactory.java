package edu.advising.states;

import edu.advising.states.transcriptstates.*;
import edu.advising.states.enrollmentstates.*;
import edu.advising.states.permissionstates.*;
import edu.advising.states.registrationstates.*;
import edu.advising.states.waitliststates.*;

public class StateFactory {

    private StateFactory(){

    }

    public static State transcriptStateFor(String s) {
        if (s == null) {
            return GoodStanding.getInstance();
        }

        return switch (s) {
            case "ACADEMIC_DISQUALIFICATION" -> AcademicDisqualification.getInstance();
            case "DISMISSED" -> Dismissed.getInstance();
            case "GOOD_STANDING" -> GoodStanding.getInstance();
            case "GRADUATED" -> Graduated.getInstance();
            case "PROBATION" -> Probation.getInstance();
            case "WITHDRAWN" -> WithdrawnT.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Transcript Status - " + s + " - "
            );
        };
    }

    public static State enrollmentStateFor(String s) {
        if (s == null) {
            return Enrolled.getInstance();
        }

        return switch (s) {
            case "COMPLETED" -> Completed.getInstance();
            case "DROPPED" -> Dropped.getInstance();
            case "ENROLLED" -> Enrolled.getInstance();
            case "WAITLISTED" -> Waitlisted.getInstance();
            case "WITHDRAWN" -> WithdrawnE.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Enrollment Status - " + s + " - "
            );
        };
    }

    public static State waitlistStateFor(String s) {
        if (s == null) {
            return Waiting.getInstance();
        }

        return switch (s) {
            case "EXPIRED" -> Expired.getInstance();
            case "FULFILLED" -> Fulfilled.getInstance();
            case "PROMOTED" -> Promoted.getInstance();
            case "REMOVED" -> Removed.getInstance();
            case "WAITING" -> Waiting.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Waitlist Status - " + s + " - "
            );
        };
    }

    public static State registrationStateFor(String s) {
        if (s == null) {
            return OpenEnrollment.getInstance();
        }

        return switch (s) {
            case "ADD_DROP" -> AddDrop.getInstance();
            case "CLOSED" -> Closed.getInstance();
            case "OPEN_ENROLLMENT" -> OpenEnrollment.getInstance();
            case "PRIORITY_OPEN" -> PriorityOpen.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Registration Status - " + s + " - "
            );
        };
    }

    public static State permissionStateFor(String s) {
        if (s == null) {
            return NotRequested.getInstance();
        }

        return switch (s) {
            case "DENIED" -> Denied.getInstance();
            case "GRANTED" -> Granted.getInstance();
            case "NOT_REQUESTED" -> NotRequested.getInstance();
            case "PENDING" -> Pending.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Permission Status - " + s + " - "
            );
        };
    }

    public static void load() {

    }
}
