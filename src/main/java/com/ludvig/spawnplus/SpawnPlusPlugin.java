package com.ludvig.spawnplus;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnPlusPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        register("spawn", new SpawnCommand(this));
        getLogger().info("Spawn++ enabled.");
    }

    public void reloadAll() {
        reloadConfig();
        getLogger().info("Spawn++ configuration reloaded.");
    }

    private void register(String name, Object executor) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().warning("Command '" + name + "' is missing from plugin.yml");
            return;
        }
        command.setExecutor((CommandExecutor) executor);
        if (executor instanceof TabCompleter completer) {
            command.setTabCompleter(completer);
        }
    }
}
