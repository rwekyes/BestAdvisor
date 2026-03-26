import edu.advising.states.StateFactory;

public class StateFactoryTest {
    public static void main(String[] args) {

        // Test to make sure all the correct States are returned

        // Transcript States:
        System.out.println("Transcript States: ");

        System.out.println("ACADEMIC_DISQUALIFICATION - " +
                StateFactory.transcriptStateFor("ACADEMIC_DISQUALIFICATION").getClass().getSimpleName()
        );
        System.out.println("DISMISSED - " +
                StateFactory.transcriptStateFor("DISMISSED").getClass().getSimpleName()
        );
        System.out.println("GOOD_STANDING - " +
                StateFactory.transcriptStateFor("GOOD_STANDING").getClass().getSimpleName()
        );
        System.out.println("GRADUATED - " +
                StateFactory.transcriptStateFor("GRADUATED").getClass().getSimpleName()
        );
        System.out.println("PROBATION - " +
                StateFactory.transcriptStateFor("PROBATION").getClass().getSimpleName()
        );
        System.out.println("WITHDRAWN - " +
                StateFactory.transcriptStateFor("WITHDRAWN").getClass().getSimpleName()
        );
        System.out.println();

        // Enrollment States:
        System.out.println("Enrollment States: ");

        System.out.println("COMPLETED - " +
                StateFactory.enrollmentStateFor("COMPLETED").getClass().getSimpleName()
        );
        System.out.println("DROPPED - " +
                StateFactory.enrollmentStateFor("DROPPED").getClass().getSimpleName()
        );
        System.out.println("ENROLLED - " +
                StateFactory.enrollmentStateFor("ENROLLED").getClass().getSimpleName()
        );
        System.out.println("WAITLISTED - " +
                StateFactory.enrollmentStateFor("WAITLISTED").getClass().getSimpleName()
        );
        System.out.println("WITHDRAWN - " +
                StateFactory.enrollmentStateFor("WITHDRAWN").getClass().getSimpleName()
        );
        System.out.println();

        // Waitlist States:
        System.out.println("Waitlist States: ");

        System.out.println("EXPIRED - " +
                StateFactory.waitlistStateFor("EXPIRED").getClass().getSimpleName()
        );
        System.out.println("FULFILLED - " +
                StateFactory.waitlistStateFor("FULFILLED").getClass().getSimpleName()
        );
        System.out.println("PROMOTED - " +
                StateFactory.waitlistStateFor("PROMOTED").getClass().getSimpleName()
        );
        System.out.println("REMOVED - " +
                StateFactory.waitlistStateFor("REMOVED").getClass().getSimpleName()
        );
        System.out.println("WAITING - " +
                StateFactory.waitlistStateFor("WAITING").getClass().getSimpleName()
        );
        System.out.println();

        // Registration States:
        System.out.println("Registration States: ");

        System.out.println("ADD_DROP - " +
                StateFactory.registrationStateFor("ADD_DROP").getClass().getSimpleName()
        );
        System.out.println("CLOSED - " +
                StateFactory.registrationStateFor("CLOSED").getClass().getSimpleName()
        );
        System.out.println("OPEN_ENROLLMENT - " +
                StateFactory.registrationStateFor("OPEN_ENROLLMENT").getClass().getSimpleName()
        );
        System.out.println("PRIORITY_OPEN - " +
                StateFactory.registrationStateFor("PRIORITY_OPEN").getClass().getSimpleName()
        );
        System.out.println();

        // Permission States:
        System.out.println("Permission States: ");

        System.out.println("DENIED - " +
                StateFactory.permissionStateFor("DENIED").getClass().getSimpleName()
        );
        System.out.println("GRANTED - " +
                StateFactory.permissionStateFor("GRANTED").getClass().getSimpleName()
        );
        System.out.println("NOT_REQUESTED - " +
                StateFactory.permissionStateFor("NOT_REQUESTED").getClass().getSimpleName()
        );
        System.out.println("PENDING - " +
                StateFactory.permissionStateFor("PENDING").getClass().getSimpleName()
        );

    }
}
