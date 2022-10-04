package me.lythox.avatar;

import com.projectkorra.projectkorra.ability.CoreAbility;

import me.lythox.avatar.listeners.AvatarListener;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

public class Avatar extends JavaPlugin {
    public static Avatar plugin;
    private static String author;
    private static Logger log;
    private static String version;

    public Avatar() {
        plugin = this;
        author = "lythox";
        log = this.getLogger();
        version = "1.1.0";
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Enabled Avatar addon");
        CoreAbility.registerPluginAbilities(plugin, "me.lythox.avatar.abilities");
        new AvatarListener(this);

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Disabled Avatar addon");
    }

    public static String getAuthor() {
        return author;
    }

    public static Logger getLog() {
        return log;
    }

    public static String getVersion() {
        return version;
    }
}