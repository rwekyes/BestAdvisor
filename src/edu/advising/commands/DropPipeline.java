package edu.advising.commands;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

import java.util.List;

public class DropPipeline {
    private final List<PipelineHandler<RegistrationContext>> handlers;

    public DropPipeline(List<PipelineHandler<RegistrationContext>> handlers) {
        this.handlers = handlers;
    }

    public static DropPipeline standard() {
        return new DropPipeline(List.of(
                new PermissionCheckHandler(),
                new DropWindowCheckHandler(),
                new DropExecutionHandler()
        ));
    }

    public PipelineResult run(DropContext ctx) {
        for (PipelineHandler<RegistrationContext> handler : handlers) {
            PipelineResult result = handler.handle(ctx);
            if (!result.isPassed()) return result;
        }
        return PipelineResult.passed();
    }
}