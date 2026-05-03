package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.contexts.GradingPeriodContext;
import edu.advising.model.GradingPeriod;

import java.sql.SQLException;

public class GradeFinalizedCheck implements PipelineHandler<GradeSubmissionContext> {
    @Override
    public PipelineResult handle(GradeSubmissionContext context) {
        if(context.getSection().isGradesFinalized()) {
            return PipelineResult.failed("GRADE_FINALIZED_CHECK",
                    "Grades are finalized for " +
                    context.getSection().getCourseName());
        }
        return PipelineResult.passed();
    }
}
