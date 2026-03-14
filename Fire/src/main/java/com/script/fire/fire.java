package com.script.fire;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class fire extends JavaPlugin implements Listener {
    private Economy econ;
    private Map<Material, Double> conversionRates;
    private String lobbyWorldName;
    private long clearIntervalTicks;
    private String bypassPermission; // Permission node to bypass clearing

    // Record positions of blocks placed by players (world -> set of locations)
    private final Map<World, Set<Location>> placedBlocks = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        // First check if the Vault plugin exists
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault plugin not found, plugin will be disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Attempt to connect to Vault economy after 400 ticks (20 seconds)
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (setupEconomy()) {
                getLogger().info("Vault economy connected successfully, provider: " + econ.getName());
                // Register event listeners, etc.
                Bukkit.getPluginManager().registerEvents(this, this);
                startClearTask();
                getLogger().info("Experience economy plugin has been enabled!");
            } else {
                getLogger().severe("Unable to connect to Vault economy provider, plugin will be disabled!");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }, 400L);
    }

    @Override
    public void onDisable() {
        placedBlocks.clear();
        getLogger().info("Experience economy plugin has been disabled.");
    }

    private void loadConfig() {
        reloadConfig();
        conversionRates = new HashMap<>();

        lobbyWorldName = getConfig().getString("lobby-world", "lobby");
        clearIntervalTicks = getConfig().getLong("clear-interval-ticks", 20 * 60);
        bypassPermission = getConfig().getString("clear-bypass-permission", "expeconomy.bypass.clear");
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    private void startClearTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World lobby = Bukkit.getWorld(lobbyWorldName);
                if (lobby == null) return;
                for (Player p : lobby.getPlayers()) {
                    // Skip if player has bypass permission
                    if (p.hasPermission(bypassPermission)) continue;
                    // Non-admin (OP) and not in creative/spectator mode
                    if (!p.isOp() && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                        double balance = econ.getBalance(p);
                        if (balance > 0) {
                            econ.withdrawPlayer(p, balance);
                            p.setLevel(0);
                        }
                    }
                }
            }
        }.runTaskTimer(this, clearIntervalTicks, clearIntervalTicks);
    }

    // ---------- Sync Level ----------
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.setLevel(0);
    }

    // ---------- Clear lobby immediately on teleport ----------
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player p = event.getPlayer();
        if (event.getTo().getWorld().getName().equalsIgnoreCase(lobbyWorldName)) {
            if (p.hasPermission(bypassPermission)) return;
            if (!p.isOp() && p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) {
                double balance = econ.getBalance(p);
                if (balance > 0) {
                    econ.withdrawPlayer(p, balance);
                    p.setLevel(0);
                }
            }
        }
    }

    // ---------- Clear placed blocks record when world unloads ----------
    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        placedBlocks.remove(event.getWorld());
    }

    // ---------- Block Protection: Record Player Placement ----------
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        placedBlocks.computeIfAbsent(block.getWorld(), w -> ConcurrentHashMap.newKeySet()).add(block.getLocation());
    }

    // ---------- Block Protection: Prevent Breaking Non-Player Placed Blocks ----------
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        World world = block.getWorld();
        Set<Location> blocks = placedBlocks.get(world);
        if (blocks == null || !blocks.contains(block.getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot break natural terrain!");
        }
    }

    // ---------- Explosion Protection: Explosions only destroy player-placed blocks ----------
    @EventHandler
    public void onBlockExplode(org.bukkit.event.block.BlockExplodeEvent event) {
        filterExplodedBlocks(event.blockList(), event.getBlock().getWorld());
    }

    @EventHandler
    public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
        filterExplodedBlocks(event.blockList(), event.getLocation().getWorld());
    }

    private void filterExplodedBlocks(List<Block> blockList, World world) {
        Set<Location> placed = placedBlocks.getOrDefault(world, Collections.emptySet());
        blockList.removeIf(block -> !placed.contains(block.getLocation()));
    }

    // ---------- Server-wide Fire Ban ----------
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        // Prevent any entity from burning (including players, mobs, items, etc.)
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        // Prevent blocks from burning
        event.setCancelled(true);
    }
}
