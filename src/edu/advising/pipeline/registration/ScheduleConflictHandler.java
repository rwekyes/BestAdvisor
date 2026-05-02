package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.model.Section;
import edu.advising.model.SectionMeetingTime;

import java.sql.SQLException;
import java.util.List;

public class ScheduleConflictHandler implements PipelineHandler<RegistrationContext> {

    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        try {
            List<SectionMeetingTime> incomingTimes = ctx.getSection().getMeetingTimes();

            for (Section enrolledSection : ctx.getStudent().getSections()) {
                if (enrolledSection.getId() == ctx.getSection().getId()) continue;

                if (timesOverlap(enrolledSection.getMeetingTimes(), incomingTimes)) {
                    return PipelineResult.failedWithConflict("ScheduleConflictHandler", enrolledSection);
                }
            }

            return PipelineResult.passed();
        } catch (SQLException e) {
            return PipelineResult.failed("ScheduleConflictHandler", "Could not verify schedule conflicts");
        }
    }

    private boolean timesOverlap(List<SectionMeetingTime> existing, List<SectionMeetingTime> incoming) {
        for (SectionMeetingTime a : existing) {
            for (SectionMeetingTime b : incoming) {
                if (overlaps(a, b)) return true;
            }
        }
        return false;
    }

    private boolean overlaps(SectionMeetingTime a, SectionMeetingTime b) {
        if (a.getDayOfWeek() != b.getDayOfWeek()) return false;
        return a.getStartTime().isBefore(b.getEndTime())
                && b.getStartTime().isBefore(a.getEndTime());
    }
}
