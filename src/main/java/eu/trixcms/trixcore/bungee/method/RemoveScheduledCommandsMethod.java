package eu.trixcms.trixcore.bungee.method;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.bungee.TrixCore;
import eu.trixcms.trixcore.common.response.Response;
import eu.trixcms.trixcore.common.response.SuccessResponse;

@MethodName(method = Methods.REMOVE_SCHEDULED_COMMANDS)
public class RemoveScheduledCommandsMethod implements IMethod {

    @Override
    public Response exec(String[] args) {
        TrixCore.getInstance().getCommandManager().clear();
        TrixCore.getInstance().getTrixServer().scheduler().resetScheduler();
        return new SuccessResponse(TrixCore.getInstance().getTranslator().of("TASK_DELETED_SUCCESSFULLY"));
    }
}
