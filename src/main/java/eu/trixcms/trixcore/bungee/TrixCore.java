package eu.trixcms.trixcore.bungee;

import eu.trixcms.trixcore.api.command.ICommandExecutor;
import eu.trixcms.trixcore.api.config.IConfig;
import eu.trixcms.trixcore.api.config.exception.InvalidConfigException;
import eu.trixcms.trixcore.api.container.CommandContainer;
import eu.trixcms.trixcore.api.i18n.Lang;
import eu.trixcms.trixcore.api.method.exception.DuplicateMethodNameException;
import eu.trixcms.trixcore.api.method.exception.InvalidMethodDefinitionException;
import eu.trixcms.trixcore.api.server.exception.InvalidPortException;
import eu.trixcms.trixcore.api.util.ServerTypeEnum;
import eu.trixcms.trixcore.bungee.method.*;
import eu.trixcms.trixcore.bungee.util.ConfigUtil;
import eu.trixcms.trixcore.common.CommandManager;
import eu.trixcms.trixcore.common.SchedulerManager;
import eu.trixcms.trixcore.common.TrixServer;
import eu.trixcms.trixcore.common.i18n.JsonMessageSource;
import eu.trixcms.trixcore.common.i18n.Translator;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class TrixCore extends Plugin implements Listener, IConfig, ICommandExecutor<CommandContainer> {

    private static final Logger logger = LoggerFactory.getLogger(TrixCore.class);
    private static final int TRIXCORE_PLUGIN_ID = 6623;
    private static final String CONFIG_FILE_NAME = "config";

    @Getter private static TrixCore instance;
    @Getter private TrixServer trixServer;
    @Getter private Translator translator;
    @Getter private SchedulerManager schedulerManager;
    @Getter private CommandManager commandManager;

    private Configuration config;

    @Override
    public void onEnable() {
        instance = this;
        translator = new Translator(JsonMessageSource.class, Lang.values());

        if (getProxy().getPluginManager().getPlugin("MinewebBridge") != null) {
            throw new RuntimeException(translator.of("PLUGIN_MINEWEB"));
        }

        if (getProxy().getPluginManager().getPlugin("AzLink") != null) {
            throw new RuntimeException(translator.of("PLUGIN_AZLINK"));
        }

        ConfigUtil.createFile(getDataFolder(), CONFIG_FILE_NAME);
        config = ConfigUtil.getConfig(getDataFolder(), CONFIG_FILE_NAME);

        schedulerManager = new SchedulerManager(translator);
        commandManager = new CommandManager(this,
                translator,
                schedulerManager,
                new File(getDataFolder(), "commands.json"));
        trixServer = new TrixServer();

        try {
            trixServer
                    .translator(translator)
                    .scheduler(schedulerManager)
                    .commandManager(commandManager)
                    .serverType(ServerTypeEnum.BUNGEE)
                    .registerMethods(
                            new IsConnectedMethod(),
                            new GetPlayerListMethod(),
                            new GetServerInfoMethod(),
                            new RunCommandMethod(),
                            new RunScheduledCommandMethod(),
                            new RemoveScheduledCommandsMethod(),
                            new SetMOTDMethod()
                    );
        } catch (DuplicateMethodNameException | InvalidMethodDefinitionException e) {
            logger.error(translator.of("ERROR"), e);
        }

        try {
            trixServer.config(this);

            logger.info(translator.of("STARTING_SERVER"));
            trixServer.start();
        } catch (InvalidPortException e) {
            logger.error(translator.of("PORT_HELP"));
            logger.error(translator.of("ERROR"), e);
        } catch (IOException e) {
            logger.error(translator.of("ERROR"), e);
        } catch (InvalidConfigException e) {
            logger.error(translator.of("UNKNOWN_SAVER"), e);
        }

        new Metrics(this, TRIXCORE_PLUGIN_ID);
        getProxy().getPluginManager().registerCommand(this, new TrixCommand(this, translator));
    }

    @Override
    public void onDisable() {
        logger.info(translator.of("STOPPING_SERVER"));
        trixServer.stop();
        ConfigUtil.saveConfig(getDataFolder(), CONFIG_FILE_NAME, config);
    }

    @Override
    public boolean executeCommand(CommandContainer commandContainer) {
        logger.debug(translator.of("HTTP_RUNNING_COMMAND", commandContainer.getCmd()));
        return getProxy().getPluginManager().dispatchCommand(getProxy().getConsole(), commandContainer.getCmd());
    }

    @Override
    public String getSecretKey() {
        if (this.config != null && this.config.getString("secret_key") != null && !this.config.getString("secret_key").isEmpty())
            return config.getString("secret_key");

        return "";
    }

    @Override
    public Integer getServerPort() {
        if (this.config != null)
            return this.config.getInt("port");

        return 0;
    }

    @Override
    public void saveSecretKey(String key) throws IOException {
        logger.info(translator.of("SAVER_SAVING_SECRET_KEY"));
        this.config.set("secret_key", key);
        ConfigUtil.saveConfig(getDataFolder(), CONFIG_FILE_NAME, config);
    }

    @Override
    public void saveServerPort(Integer port) throws IOException {
        logger.info(translator.of("SAVER_SAVING_SERVER_PORT"));
        this.config.set("port", port);
        ConfigUtil.saveConfig(getDataFolder(), CONFIG_FILE_NAME, config);
    }

    public void saveMotd(String motd) throws IOException {
        this.config.set("custom_motd", motd);
        ConfigUtil.saveConfig(getDataFolder(), CONFIG_FILE_NAME, config);
    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        if (config.contains("custom_motd")) {
            e.getResponse().setDescriptionComponent(new TextComponent(config.getString("custom_motd")));
        }
    }
}
