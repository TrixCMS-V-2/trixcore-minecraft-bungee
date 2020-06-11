package eu.trixcms.trixcore.bungee.util;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigUtil {

    public static boolean createFile(File folder, String fileName) {
        if (!folder.exists()) {
            folder.mkdir();
        }

        File file = new File(folder, fileName + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static Configuration getConfig(File folder, String fileName) {
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(folder, fileName + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveConfig(File folder, String fileName, Configuration config) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(folder, fileName + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
