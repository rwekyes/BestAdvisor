package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

import java.util.Set;

public class GradeValidationHandler implements PipelineHandler<GradeSubmissionContext> {

    private static final Set<String> VALID_GRADES = Set.of(
            "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "D-", "F", "W", "I", "P", "NP"
    );

    @Override
    public PipelineResult handle(GradeSubmissionContext context) {
        if (!VALID_GRADES.contains(context.getGrade())) {
            return PipelineResult.failed("GRADE_VALIDATION",
                    "'" + context.getGrade() + "' is not a valid grade. Valid grades are: " + VALID_GRADES);
        }
        return PipelineResult.passed();
    }
}
