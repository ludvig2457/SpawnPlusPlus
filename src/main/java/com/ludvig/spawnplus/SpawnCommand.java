package com.ludvig.spawnplus;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class SpawnCommand implements CommandExecutor, TabCompleter {

    private static final String COMBAT_TAG_KEY = "pvpplugin_combat_until";

    private final SpawnPlusPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public SpawnCommand(SpawnPlusPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("spawnplus.admin")) {
                sender.sendMessage(Text.prefix("&cYou don't have permission to reload Spawn++."));
                return true;
            }
            plugin.reloadAll();
            sender.sendMessage(Text.prefix("&aSpawn++ configuration reloaded."));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Text.prefix("&cOnly players can teleport to spawn."));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
            if (!player.hasPermission("spawnplus.admin")) {
                player.sendMessage(Text.prefix("&cYou don't have permission to set the spawn."));
                return true;
            }
            Location location = player.getLocation();
            player.getWorld().setSpawnLocation(location);
            player.sendMessage(Text.prefix("&aSpawn point set for world &f" + player.getWorld().getName() + "&a."));
            return true;
        }

        long combatRemaining = combatRemaining(player);
        if (plugin.getConfig().getBoolean("settings.spawn.block-in-combat", true) && combatRemaining > 0L) {
            player.sendMessage(Text.prefix("&cYou cannot teleport to spawn while in combat for &f"
                    + Text.duration(combatRemaining) + "&c."));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8F, 1.0F);
            return true;
        }

        int cooldownSeconds = Math.max(0, plugin.getConfig().getInt("settings.spawn.cooldown-seconds", 10));
        long now = System.currentTimeMillis();
        long until = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        if (until > now && !player.hasPermission("spawnplus.bypass")) {
            player.sendMessage(Text.prefix("&cPlease wait &f" + Text.duration(until - now) + " &cbefore using /spawn."));
            return true;
        }

        Location spawn = player.getWorld().getSpawnLocation();
        SchedulerCompat.teleport(player, spawn);
        player.setFallDistance(0.0F);
        player.playSound(spawn, Sound.ENTITY_ENDERMAN_TELEPORT, 0.8F, 1.3F);
        player.sendMessage(Text.prefix("&aTeleported to spawn."));

        if (cooldownSeconds > 0 && !player.hasPermission("spawnplus.bypass")) {
            cooldowns.put(player.getUniqueId(), now + cooldownSeconds * 1000L);
        }
        return true;
    }

    private long combatRemaining(Player player) {
        for (MetadataValue value : player.getMetadata(COMBAT_TAG_KEY)) {
            return Math.max(0L, value.asLong() - System.currentTimeMillis());
        }
        return 0L;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission("spawnplus.admin")) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            return List.of("set", "reload").stream()
                    .filter(value -> value.startsWith(prefix))
                    .toList();
        }
        return List.of();
    }
}
