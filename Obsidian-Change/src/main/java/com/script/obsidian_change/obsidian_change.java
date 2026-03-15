package com.script.obsidian_change;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class obsidian_change extends JavaPlugin implements Listener {

    private static obsidian_change instance;
    private NamespacedKey itemKey;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, ConvertingPlayer> convertingPlayers = new HashMap<>();

    private static class ConvertingPlayer {
        final Block targetBlock;
        final long startTime;

        ConvertingPlayer(Block targetBlock, long startTime) {
            this.targetBlock = targetBlock;
            this.startTime = startTime;
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        itemKey = new NamespacedKey(this, "obsidian_change_powder");

        // Register event listener
        Bukkit.getPluginManager().registerEvents(this, this);

        // Register custom item
        registerItem();

        // Register command
        if (getCommand("obsidian") != null) {
            getCommand("obsidian").setExecutor(new ObsidianCommand(this));
        }

        getLogger().info("Obsidian Change Powder plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Obsidian Change Powder plugin has been disabled.");
    }

    public static obsidian_change getInstance() {
        return instance;
    }

    /**
     * Register custom item
     */
    private void registerItem() {
        // Create item
        ItemStack item = createObsidianChangePowder();

        // Register recipe (optional, if crafting is needed)
        ShapedRecipe recipe = new ShapedRecipe(itemKey, item);
        // Crafting recipe can be added here if needed
        // For example: recipe.shape("OOO", "ORO", "OOO").setIngredient('O', Material.OBSIDIAN).setIngredient('R', Material.REDSTONE);
        // Bukkit.addRecipe(recipe);
    }

    /**
     * Create Obsidian Change Powder item
     */
    public static ItemStack createObsidianChangePowder() {
        ItemStack item = new ItemStack(Material.REDSTONE, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Obsidian Change Powder");
            meta.setItemModel(new NamespacedKey("minecraft", "redstone")); // Use redstone powder texture
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Check if player has conversion powder
     */
    private boolean hasObsidianChangePowder(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isObsidianChangePowder(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if item is Obsidian Change Powder
     */
    public static boolean isObsidianChangePowder(ItemStack item) {
        if (item == null || item.getType() != Material.REDSTONE || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() &&
               meta.getDisplayName().equals(ChatColor.LIGHT_PURPLE + "Obsidian Change Powder");
    }

    /**
     * Consume one powder
     */
    private void consumeOnePowder(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isObsidianChangePowder(item)) {
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() <= 0) {
                    player.getInventory().remove(item);
                }
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Only handle right-click on block
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Check if held item is conversion powder
        ItemStack item = event.getItem();
        if (!isObsidianChangePowder(item)) {
            return;
        }

        // Cancel default interaction (prevent placing redstone powder)
        event.setCancelled(true);

        // Check if target block is obsidian
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.OBSIDIAN) {
            player.sendMessage(ChatColor.RED + "You must look at obsidian to use!");
            return;
        }

        // Check cooldown
        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(playerId)) {
            long timeLeft = (cooldowns.get(playerId) - now) / 1000;
            if (timeLeft > 0) {
                player.sendMessage(ChatColor.RED + "Skill on cooldown, remaining " + timeLeft + " seconds");
                return;
            }
        }

        // Check if there is already an ongoing conversion
        if (convertingPlayers.containsKey(playerId)) {
            player.sendMessage(ChatColor.RED + "You already have an ongoing conversion!");
            return;
        }

        // Consume one powder
        consumeOnePowder(player);

        // Start conversion
        player.sendMessage(ChatColor.YELLOW + "Starting obsidian conversion, please wait 3 seconds...");

        // Record conversion status
        convertingPlayers.put(playerId, new ConvertingPlayer(clickedBlock, now));

        // Execute conversion after 3 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                ConvertingPlayer cp = convertingPlayers.remove(playerId);
                if (cp == null) return;

                // Check if player is still online
                Player p = Bukkit.getPlayer(playerId);
                if (p == null || !p.isOnline()) return;

                // Check if target block is still obsidian (not destroyed or changed)
                Block block = cp.targetBlock;
                if (block.getType() == Material.OBSIDIAN) {
                    // Convert to wooden planks
                    block.setType(Material.OAK_PLANKS);
                    p.sendMessage(ChatColor.GREEN + "Obsidian successfully converted to wooden planks!");
                } else {
                    p.sendMessage(ChatColor.RED + "Conversion failed: target block has been changed or destroyed.");
                }

                // Set cooldown
                cooldowns.put(playerId, now + 5000); // 5 seconds cooldown
            }
        }.runTaskLater(this, 60L); // 3 seconds = 60 ticks (20 ticks/second)
    }

    /**
     * Command executor inner class
     */
    private static class ObsidianCommand implements org.bukkit.command.CommandExecutor {
        private final obsidian_change plugin;

        public ObsidianCommand(obsidian_change plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            if (!(sender instanceof Player) && args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Console must specify player name: /obsidian give <player> obsidian_change_powder [amount]");
                return true;
            }

            if (args.length < 2 || !args[0].equalsIgnoreCase("give")) {
                sender.sendMessage(ChatColor.RED + "Usage: /obsidian give [player] obsidian_change_powder [amount]");
                return true;
            }

            String targetName;
            String itemType;
            int amount = 1;

            if (args.length >= 3) {
                // /obsidian give <player> <item> [amount]
                targetName = args[1];
                itemType = args[2];
                if (args.length >= 4) {
                    try {
                        amount = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Amount must be an integer!");
                        return true;
                    }
                }
            } else {
                // /obsidian give <item> [amount] (give to self)
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Console must specify player name!");
                    return true;
                }
                targetName = sender.getName();
                itemType = args[1];
                if (args.length >= 3) {
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Amount must be an integer!");
                        return true;
                    }
                }
            }

            // Check item type
            if (!itemType.equalsIgnoreCase("obsidian_change_powder")) {
                sender.sendMessage(ChatColor.RED + "Unknown item: " + itemType);
                return true;
            }

            // Get target player
            Player target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player " + targetName + " is not online!");
                return true;
            }

            // Give item
            ItemStack item = createObsidianChangePowder();
            item.setAmount(Math.min(amount, 64));
            target.getInventory().addItem(item);

            sender.sendMessage(ChatColor.GREEN + "Given " + targetName + " " + amount + " Obsidian Change Powder");
            if (!sender.equals(target)) {
                target.sendMessage(ChatColor.GREEN + "You have received " + amount + " Obsidian Change Powder");
            }

            return true;
        }
    }
}
