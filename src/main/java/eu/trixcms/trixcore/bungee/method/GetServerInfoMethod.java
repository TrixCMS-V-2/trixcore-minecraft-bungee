package eu.trixcms.trixcore.bungee.method;

import eu.trixcms.trixcore.api.container.ProxyInfoContainer;
import eu.trixcms.trixcore.api.container.ProxyServerContainer;
import eu.trixcms.trixcore.api.method.IMethod;
import eu.trixcms.trixcore.api.method.Methods;
import eu.trixcms.trixcore.api.method.annotation.MethodName;
import eu.trixcms.trixcore.api.response.IResponse;
import eu.trixcms.trixcore.common.response.SuccessResponse;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

@MethodName(method = Methods.GET_SERVER_INFO)
public class GetServerInfoMethod implements IMethod {

    @Override
    public IResponse exec(String[] args) {
        InetAddress ip = null;

        try {
            ip = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String completeIP = ((ip == null) ? "localhost" : ip.getHostAddress());

        return new SuccessResponse(
                new ProxyInfoContainer(
                        completeIP,
                        ProxyServer.getInstance().getVersion(),
                        ProxyServer.getInstance().getServers().values().stream()
                                .map(serverInfo -> new ProxyServerContainer(serverInfo.getName(), serverInfo.getMotd(), serverInfo.getPlayers().size()))
                                .collect(Collectors.toList())
                )
        );
    }
}
