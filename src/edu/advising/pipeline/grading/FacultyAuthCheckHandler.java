package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

public class FacultyAuthCheckHandler implements PipelineHandler<GradeSubmissionContext> {
    @Override
    public PipelineResult handle(GradeSubmissionContext context) {
        if (context.getAuthenticationResult() == null || !context.getAuthenticationResult().isFullyAuthenticated()) {
            return PipelineResult.failed("AUTH_CHECK",
                    "You must be logged in to submit student grades.");
        }
        return PipelineResult.passed();
    }
}
