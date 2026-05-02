package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.contexts.FacultyPermissionContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.FacultyPermission;
import edu.advising.model.Section;
import edu.advising.users.Student;

import java.sql.SQLException;

public class CapacityCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        if (!ctx.getSection().hasCapacity()) {
            // Check if student has a valid faculty permission to bypass capacity
            try {
                FacultyPermission fp = fetchPermissionForStudent(ctx.getStudent(), ctx.getSection());
                FacultyPermissionContext permCtx = fp != null
                        ? FacultyPermissionContext.load(fp, null, ctx.getSection(), null)
                        : null;
                if (permCtx == null || !permCtx.isValid()) {
                    String errorMessage = String.format("Registration failed for %s - section full",
                            ctx.getSection().getCourseCode());
                    return PipelineResult.failed("CAPACITY_CHECK", errorMessage);
                }
                // Valid permission — bypass capacity check and continue
            } catch (SQLException e) {
                String errorMessage = "Could not verify faculty permission";
                return PipelineResult.failed("CAPACITY_CHECK", errorMessage);
            }
        }
        return PipelineResult.passed();
    }

    //Helper
    private FacultyPermission fetchPermissionForStudent(Student student, Section section) throws SQLException {
        String sql = "SELECT fp.* FROM faculty_permissions fp " +
                "JOIN waitlist w ON fp.waitlist_id = w.id " +
                "WHERE w.student_id = ? AND fp.section_id = ? AND fp.status = 'APPROVED'";
        return DatabaseManager.getInstance().fetch(sql, rs -> {
            FacultyPermission fp = new FacultyPermission();
            fp.setId(rs.getInt("id"));
            fp.setSectionId(rs.getInt("section_id"));
            fp.setWaitlistId(rs.getInt("waitlist_id"));
            var requestTs = rs.getTimestamp("request_date");
            fp.setRequestDate(requestTs != null ? requestTs.toLocalDateTime() : null);
            var expiryTs = rs.getTimestamp("expiry_date");
            fp.setExpiryDate(expiryTs != null ? expiryTs.toLocalDateTime() : null);
            fp.setDenyReason(rs.getString("deny_reason"));
            fp.setStatus(rs.getString("status"));
            return fp;
        }, student.getId(), section.getId());
    }
}
