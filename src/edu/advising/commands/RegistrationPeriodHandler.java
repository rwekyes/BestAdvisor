package edu.advising.commands;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.contexts.RegistrationPeriodContext;

import java.sql.SQLException;

public class RegistrationPeriodHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        String failedAt = "REG_PERIOD_CHECK";
        // Check if registration is closed before continuing
        try {
            RegistrationPeriodContext periodCtx = RegistrationPeriodContext.currentPeriod(
                    ctx.getSection().getSemester(), ctx.getSection().getYear());
            if (periodCtx == null || !periodCtx.canRegister()) {

                String errorMessage = "Registration is not currently open for " + ctx.getSection().getCourseCode();
                return PipelineResult.failed(failedAt, errorMessage);
            }
        } catch (SQLException e) {
            // If we can't check the period, fail safe — block registration
            String errorMessage = "Could not verify registration period status";
            return PipelineResult.failed(failedAt, errorMessage);
        }
        return PipelineResult.passed();
    }
}
