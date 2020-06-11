package eu.trixcms.trixcore.bungee.method;

import eu.trixcms.trixcore.api.container.CommandContainer;
import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.ArgsPrecondition;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.bungee.TrixCore;
import eu.trixcms.trixcore.common.response.Response;
import eu.trixcms.trixcore.common.response.SuccessResponse;

@MethodName(method = Methods.RUN_COMMAND)
public class RunCommandMethod implements IMethod {

    @Override
    @ArgsPrecondition(amount = 1)
    public Response exec(String[] args) {
        String command = args[0].trim();
        return new SuccessResponse(TrixCore.getInstance().executeCommand(new CommandContainer(0, command)));
    }
}
