package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.contexts.GradingPeriodContext;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.model.GradingPeriod;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class GradingWindowCheckHandler implements PipelineHandler<GradeSubmissionContext> {
    @Override
    public PipelineResult handle(GradeSubmissionContext context) {
        try {
            GradingPeriodContext periodCtx = GradingPeriodContext.currentPeriod(
                    context.getSection().getSemester(), context.getSection().getYear());
            if (periodCtx == null) {
                return PipelineResult.failed("GRADING_WINDOW_CHECK",
                        "No grading period configured for " + context.getSection().getCourseName());
            }
            GradingPeriod period = periodCtx.getGradingPeriod();
            if (period.getCloseDate().isBefore(LocalDateTime.now()) ||
                    period.getOpenDate().isAfter(LocalDateTime.now())) {
                return PipelineResult.failed("GRADING_WINDOW_CHECK",
                        "Grading is not currently open for " + context.getSection().getCourseName());
            }
        } catch (SQLException e) {
            return PipelineResult.failed("GRADING_WINDOW_CHECK",
                    "Could not verify grading period status - " +
                    e.getMessage());
        }
        return PipelineResult.passed();
    }
}
