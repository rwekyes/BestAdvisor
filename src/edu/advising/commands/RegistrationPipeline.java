package edu.advising.commands;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

import java.util.List;

public class RegistrationPipeline {
    private final List<PipelineHandler<RegistrationContext>> handlers;

    public RegistrationPipeline(List<PipelineHandler<RegistrationContext>> handlers){
        this.handlers = handlers;
    }

    // Default wiring for normal registration — one call to get a ready-to-use pipeline
    public static RegistrationPipeline standard() {
        return new RegistrationPipeline(List.of(
                new PermissionCheckHandler(),
                new RegistrationPeriodHandler(),
                new CapacityCheckHandler(),
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
