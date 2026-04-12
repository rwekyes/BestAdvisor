package edu.advising.states;

import edu.advising.states.transcriptrequeststates.*;
import edu.advising.states.transcriptstates.*;
import edu.advising.states.enrollmentstates.*;
import edu.advising.states.permissionstates.*;
import edu.advising.states.registrationstates.*;
import edu.advising.states.waitliststates.*;

public class StateFactory {

    private StateFactory(){

    }

    public static TranscriptState transcriptStateFor(String s) {
        if (s == null) {
            return GoodStandingTranscriptState.getInstance(); // Defaults to Good Standing, because I'm not sure of the business logic
        }

        return switch (s) {
            case "ACADEMIC_DISQUALIFICATION" -> AcademicDisqualificationTranscriptState.getInstance();
            case "DISMISSED" -> DismissedTranscriptState.getInstance();
            case "GOOD_STANDING" -> GoodStandingTranscriptState.getInstance();
            case "GRADUATED" -> GraduatedTranscriptState.getInstance();
            case "PROBATION" -> ProbationTranscriptState.getInstance();
            case "WITHDRAWN" -> WithdrawnTranscriptState.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Transcript Status - " + s + " - "
            );
        };
    }

    public static TranscriptRequestState transcriptRequestStateFor(String s) {
        if (s == null) {
            return PendingTranscriptRequestState.getInstance();
        }

        return switch (s) {
            case "PENDING" -> PendingTranscriptRequestState.getInstance();
            case "PROCESSING" -> ProcessingTranscriptRequestState.getInstance();
            case "READY" -> ReadyTranscriptRequestState.getInstance();
            case "SENT" -> SentTranscriptRequestState.getInstance();
            case "CANCELLED" -> CancelledTranscriptRequestState.getInstance();
            case "FAILED" -> FailedTranscriptRequestState.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Transcript Request Status - " + s + " - "
            );
        };
    }

    public static EnrollmentState enrollmentStateFor(String s) {
        if (s == null) {
            return EnrolledEnrollmentState.getInstance();
        }

        return switch (s) {
            case "PENDING" -> PendingEnrollmentState.getInstance();
            case "COMPLETED" -> CompletedEnrollmentState.getInstance();
            case "DROPPED" -> DroppedEnrollmentState.getInstance();
            case "ENROLLED" -> EnrolledEnrollmentState.getInstance();
            case "WAITLISTED" -> WaitlistedEnrollmentState.getInstance();
            case "WITHDRAWN" -> WithdrawnEnrollmentState.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Enrollment Status - " + s + " - "
            );
        };
    }

    public static WaitlistState waitlistStateFor(String s) {
        if (s == null) {
            return ActiveWaitlistState.getInstance();
        }

        return switch (s) {
            case "EXPIRED" -> ExpiredWaitlistState.getInstance();
            case "ENROLLED" -> EnrolledFromWaitlistState.getInstance();
            case "OFFERED" -> OfferedWaitlistState.getInstance();
            case "REMOVED" -> RemovedWaitlistState.getInstance();
            case "ACTIVE" -> ActiveWaitlistState.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Waitlist Status - " + s + " - "
            );
        };
    }

    public static RegistrationState registrationStateFor(String s) {
        if (s == null) {
            return OpenRegistrationState.getInstance();
        }

        return switch (s) {
            case "NOT_OPEN" -> NotOpenRegistrationState.getInstance();
            case "CLOSED" -> ClosedRegistrationState.getInstance();
            case "OPEN" -> OpenRegistrationState.getInstance();
            case "LATE" -> LateRegistrationState.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Registration Status - " + s + " - "
            );
        };
    }

    public static PermissionState permissionStateFor(String s) {
        if (s == null) {
            return NotRequestedPermissionState.getInstance();
        }

        return switch (s) {
            case "DENIED" -> DeniedPermissionState.getInstance();
            case "GRANTED" -> GrantedPermissionState.getInstance();
            case "NOT_REQUESTED" -> NotRequestedPermissionState.getInstance();
            case "PENDING" -> PendingPermissionState.getInstance();
            default -> throw new IllegalArgumentException(
                    "Unknown Permission Status - " + s + " - "
            );
        };
    }

    public static void load() {

    }
}
