package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.users.Faculty;

import java.sql.SQLException;

public class SectionOwnershipCheckHandler implements PipelineHandler<GradeSubmissionContext> {
    @Override
    public PipelineResult handle(GradeSubmissionContext context) {
        try {
            Faculty owner = context.getSection().getFaculty();
            if (owner == null) {
                return PipelineResult.failed("SECTION_OWNERSHIP_CHECK",
                        "No faculty assigned to section " + context.getSection().getCourseName());
            }
            if (context.getFaculty().getId() != owner.getId()){
                return PipelineResult.failed("SECTION_OWNERSHIP_CHECK",
                        "Only " +
                                owner.getFullName() +
                                " can submit grades for this section.");
            }
        } catch (SQLException e) {
            return PipelineResult.failed("SECTION_OWNERSHIP_CHECK",
                    "Error retrieving faculty member assigned to "
                            + context.getSection().getCourseName() +
                            " - " + e.getMessage());
        }
        return PipelineResult.passed();
    }
}
