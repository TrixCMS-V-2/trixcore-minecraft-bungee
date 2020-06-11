package eu.trixcms.trixcore.bungee;

import eu.trixcms.trixcore.api.config.exception.InvalidConfigException;
import eu.trixcms.trixcore.api.server.exception.InvalidPortException;
import eu.trixcms.trixcore.common.i18n.Translator;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TrixCommand extends Command {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private TrixCore trixCore;
    private Translator translator;

    public TrixCommand(TrixCore trixcore, Translator translator) {
        super("trixcore");
        this.trixCore = trixcore;
        this.translator = translator;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reset")) {
            if (trixCore.getServerPort() == 0)
                logger.info(translator.of("CMD_PORT_ALREADY_RESET"));

            trixCore.getTrixServer().stop();

            try {
                trixCore.getTrixServer().setSecretKey("");
                trixCore.getTrixServer().setPort(-1);
            } catch (IOException e) {
                logger.error(translator.of("ERROR"), e);
            } catch (InvalidPortException ignored) {
            }

            sender.sendMessage(new TextComponent(translator.of("CMD_PORT_SUCCESSFULLY_RESET")));
            logger.info(translator.of("CMD_PORT_SUCCESSFULLY_RESET"));
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setup")) {
            if (trixCore.getServerPort() != 0)
                logger.error(translator.of("CMD_NEED_RESET_BEFORE"));

            trixCore.getTrixServer().stop();
            try {
                trixCore.getTrixServer().setPort(Integer.parseInt(args[1]));
            } catch (InvalidPortException e) {
                logger.error(translator.of("PORT_HELP"));
                logger.error(translator.of("ERROR"), e);
                sender.sendMessage(new TextComponent(translator.of("PORT_HELP")));
                sender.sendMessage(new TextComponent(translator.of("ERROR") + e.getMessage()));
            } catch (IOException e) {
                logger.error(translator.of("ERROR"), e);
            }

            sender.sendMessage(new TextComponent(translator.of("CMD_PORT_SUCCESSFULLY_SETUP", trixCore.getServerPort() + "")));
            logger.info(translator.of("CMD_PORT_SUCCESSFULLY_SETUP", trixCore.getServerPort() + ""));

            try {
                trixCore.getTrixServer().config(trixCore).start();
            } catch (InvalidPortException e) {
                logger.error(translator.of("PORT_HELP"));
                logger.error(translator.of("ERROR"), e);
                sender.sendMessage(new TextComponent(translator.of("PORT_HELP")));
                sender.sendMessage(new TextComponent(translator.of("ERROR") + e.getMessage()));
            } catch (IOException e) {
                logger.error(translator.of("ERROR"), e);
                sender.sendMessage(new TextComponent(translator.of("ERROR") + e.getMessage()));
            } catch (InvalidConfigException e) {
                sender.sendMessage(new TextComponent(translator.of("UNKNOWN_SAVER") + e.getMessage()));
                logger.error(translator.of("UNKNOWN_SAVER"), e);
            }
        }
    }
}

