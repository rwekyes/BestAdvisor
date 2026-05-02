package edu.advising.pipeline.waitlist;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.pipeline.registration.PermissionCheckHandler;
import edu.advising.pipeline.registration.RegistrationContext;

import java.util.List;

public class WaitlistPipeline {
    private final List<PipelineHandler<RegistrationContext>> handlers;

    public WaitlistPipeline(List<PipelineHandler<RegistrationContext>> handlers){this.handlers = handlers;}

    public static WaitlistPipeline standard(){
        return new WaitlistPipeline(List.of(
                new PermissionCheckHandler(),       // reused verbatim
                new WaitlistDuplicateCheckHandler(),
                new WaitlistAddHandler()
        ));
    }

    public PipelineResult run(RegistrationContext ctx){
        for (PipelineHandler<RegistrationContext> handler : handlers) {
            PipelineResult result = handler.handle(ctx);
            if (!result.isPassed()) return result;
        }
        return PipelineResult.passed();
    }
}
