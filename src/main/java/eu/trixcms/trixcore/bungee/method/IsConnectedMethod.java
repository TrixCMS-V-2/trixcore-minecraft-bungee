package eu.trixcms.trixcore.bungee.method;

import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.ArgsPrecondition;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.response.IResponse;
import eu.trixcms.trixcore.common.response.SuccessResponse;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;

@MethodName(method = Methods.IS_CONNECTED)
public class IsConnectedMethod implements IMethod {

    @Override
    @ArgsPrecondition(amount = 1)
    public IResponse exec(String[] args) {
        return new SuccessResponse(ProxyServer.getInstance().getPlayers().stream()
                .map(ProxiedPlayer::getName).filter(Objects::nonNull)
                .anyMatch(name -> name.equals(args[0]))
        );
    }
}
