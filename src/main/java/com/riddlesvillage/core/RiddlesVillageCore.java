package com.riddlesvillage.core;

import com.riddlesvillage.core.api.file.config.FileConfig;
import com.riddlesvillage.core.api.file.yaml.YamlFileImpl;
import com.riddlesvillage.core.api.mechanic.MechanicManager;
import com.riddlesvillage.core.commands.CommandStats;
import com.riddlesvillage.core.database.DatabaseAPI;
import com.riddlesvillage.core.player.PlayerHandler;
import com.riddlesvillage.core.api.file.yaml.YamlFile;
import com.riddlesvillage.core.api.file.yaml.YamlLoadException;
import com.riddlesvillage.core.bungee.Messenger;
import com.riddlesvillage.core.database.DatabaseInstance;
import com.riddlesvillage.core.player.events.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class RiddlesVillageCore extends JavaPlugin implements Listener {

    public static final ExecutorService ASYNC = Executors.newFixedThreadPool(2);
    private Messenger messenger;
    private static RiddlesVillageCore core;

    public static RiddlesVillageCore getCore() {
        return core;
    }

    @Override
    public void onEnable() {
        core = this;
        File file = new File(getDataFolder() + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            YamlFile yamlConfig = new YamlFileImpl().load(new File(getDataFolder() + "/", "mongodb.yml"));
            this.generateMongoDb(yamlConfig);
            DatabaseInstance.getInstance().startInitialization(true, yamlConfig.get().getString("uri"));
        } catch (YamlLoadException e) {
            e.printStackTrace();
        }
        DatabaseAPI.getInstance().startInitialization();
        this.messenger = new Messenger(this, true, 60);
        registerMechanics();
        PlayerJumpEvent.register(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("stats").setExecutor(new CommandStats());
    }

    private void registerMechanics() {
        MechanicManager manager = MechanicManager.getInstance();
        manager.registerMechanic(new PlayerHandler());
        manager.registerMechanics(this);
    }

    private void generateMongoDb(YamlFile yamlFile) {
        yamlFile.get().set("uri", "mongodb://matthew:password@localhost:27017/ +");
    }

    public static void initApi(JavaPlugin plugin) {
        Bukkit.getServer().getLogger().log(Level.INFO, "Hooked with " + plugin.getName());
    }

    @Override
    public void onDisable() {
        try {
            YamlFile yamlConfig = new YamlFileImpl().load(new File(getDataFolder() + "/", "mongodb.yml"));
            yamlConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (YamlLoadException e) {
            e.printStackTrace();
        }
    }

    private FileConfig fileConfig;

    public Messenger getMessenger() {
        return messenger;
    }
}
