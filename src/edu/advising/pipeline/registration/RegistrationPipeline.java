package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

import java.util.List;

public class RegistrationPipeline {
    private final List<PipelineHandler<RegistrationContext>> handlers;

    // Pass in a pre-made list, mostly for testing purposes
    public RegistrationPipeline(List<PipelineHandler<RegistrationContext>> handlers){
        this.handlers = handlers;
    }

    // Default wiring for normal registration — one call to get a ready-to-use pipeline
    public static RegistrationPipeline standard() {
        return new RegistrationPipeline(List.of(
                new AuthCheckHandler(),
                new PermissionCheckHandler(),
                new RegistrationPeriodHandler(),
                new FinancialHoldCheckHandler(),
                new AcademicHoldCheckHandler(),
                new PrerequisiteCheckHandler(),
                new CorequisiteCheckHandler(),
                new CapacityCheckHandler(),
                new CreditLimitCheckHandler(),
                new DuplicateEnrollmentCheckHandler(),
                new ScheduleConflictHandler(),
                new EnrollmentHandler()
        ));
    }

    public PipelineResult run(RegistrationContext ctx) {
        for (PipelineHandler<RegistrationContext> handler : handlers) {
            PipelineResult result = handler.handle(ctx);
            if (!result.isPassed()) return result;
        }
        return PipelineResult.passed();
    }
}
