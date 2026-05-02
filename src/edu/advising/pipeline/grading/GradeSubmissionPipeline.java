package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

import java.util.List;

public class GradeSubmissionPipeline {
    private final List<PipelineHandler<GradeSubmissionContext>> handlers;

    public GradeSubmissionPipeline(List<PipelineHandler<GradeSubmissionContext>> handlers){
        this.handlers = handlers;
    }

    public static GradeSubmissionPipeline standard(){
        return new GradeSubmissionPipeline(List.of(
                new FacultyAuthCheckHandler(),
                new SectionOwnershipCheckHandler(),
                new EnrollmentVerificationHandler(),
                new GradingWindowCheckHandler(),
                new GradeSubmissionHandler()
        ));
    }

    public PipelineResult run(GradeSubmissionContext ctx){
        for (PipelineHandler<GradeSubmissionContext> handler : handlers){
            PipelineResult result = handler.handle(ctx);
            if (!result.isPassed()) return result;
        }
        return PipelineResult.passed();
    }
}
