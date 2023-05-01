package cc.fyre.universe.config;

import cc.fyre.universe.Universe;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class UniverseConfig {

    private File file;

    @Getter private Universe plugin;
    @Getter private Configuration configuration;

    public UniverseConfig(Universe plugin) {
        this.plugin = plugin;

        this.file = new File(plugin.getDataFolder().getAbsolutePath(),"config.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        if (!this.file.exists()) {

            try {
                this.file.createNewFile();

                try (
                        InputStream is = plugin.getResourceAsStream("config.yml");
                        OutputStream os = new FileOutputStream(this.file)
                ) {
                    ByteStreams.copy(is,os);
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }

        }

        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.save();
    }


    public void save() {

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration,file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}