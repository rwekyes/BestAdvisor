package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.permissions.FeatureCodes;

public class PermissionCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        if (!ctx.getPermissionTree().hasPermission(FeatureCodes.REGISTER_COURSES)) {
            String failedAt = "PERMISSION_CHECK";
            String errorMessage = ctx.getPermissionTree().explainDenial(FeatureCodes.REGISTER_COURSES);
            return PipelineResult.failed(failedAt, errorMessage);
        }
        return PipelineResult.passed();
    }
}
