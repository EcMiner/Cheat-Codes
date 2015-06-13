package me.ecminer.cheatcodes;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static final ItemStack enter = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 5), ChatColor.GREEN + ChatColor.BOLD.toString() + "Enter");
	public static final ItemStack close = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 14), ChatColor.RED + ChatColor.BOLD.toString() + "Close");
	public static final ItemStack back = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 15), ChatColor.DARK_AQUA + "⌫");
	public static final ItemStack dash = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "-");
	public static final ItemStack key_0 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "0");
	public static final ItemStack key_1 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "1");
	public static final ItemStack key_2 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "2");
	public static final ItemStack key_3 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "3");
	public static final ItemStack key_4 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "4");
	public static final ItemStack key_5 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "5");
	public static final ItemStack key_6 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "6");
	public static final ItemStack key_7 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "7");
	public static final ItemStack key_8 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "8");
	public static final ItemStack key_9 = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 4), ChatColor.WHITE + ChatColor.BOLD.toString() + "9");

	private static final Map<UUID, Map<CheatCode, CheatCodeCooldown>> cooldowns = new HashMap<UUID, Map<CheatCode, CheatCodeCooldown>>();

	public static void addCooldown(CheatCodeCooldown cooldown, boolean save) {
		if (!cooldowns.containsKey(cooldown.getPlayerId()))
			cooldowns.put(cooldown.getPlayerId(), new HashMap<CheatCode, CheatCodeCooldown>());
		cooldowns.get(cooldown.getPlayerId()).put(cooldown.getCheatCode(), cooldown);
		if (save) {
			cooldownConfig.set(cooldown.getPlayerId().toString() + "." + cooldown.getCheatCode().getCheatCode(), cooldown.getEndTime());
			saveCooldownConfig();
		}
	}

	public static boolean hasCooldown(Player player, CheatCode cheatCode) {
		return hasCooldown(player.getUniqueId(), cheatCode);
	}

	public static boolean hasCooldown(UUID playerId, CheatCode cheatCode) {
		return cooldowns.containsKey(playerId) && cooldowns.get(playerId).containsKey(cheatCode);
	}

	public static CheatCodeCooldown getCooldown(Player player, CheatCode cheatCode) {
		return getCooldown(player.getUniqueId(), cheatCode);
	}

	public static CheatCodeCooldown getCooldown(UUID playerId, CheatCode cheatCode) {
		return cooldowns.containsKey(playerId) ? cooldowns.get(playerId).get(cheatCode) : null;
	}

	public static void removeCooldown(Player player, CheatCode cheatCode, boolean save) {
		removeCooldown(player.getUniqueId(), cheatCode, save);
	}

	public static void removeCooldown(UUID playerId, CheatCode cheatCode, boolean save) {
		if (cooldowns.containsKey(playerId)) {
			cooldowns.get(playerId).remove(cheatCode);
			if (save) {
				cooldownConfig.set(playerId.toString() + "." + cheatCode.getCheatCode(), null);
				saveCooldownConfig();
			}
		}
	}

	public static void removeAllFromUser(Player player) {
		removeAllFromUser(player.getUniqueId());
	}

	public static void removeAllFromUser(UUID playerId) {
		cooldowns.remove(playerId);
	}

	public static ItemStack setDisplayName(ItemStack item, String displayName) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(displayName);
		item.setItemMeta(meta);
		return item;
	}

	private Pattern pattern = Pattern.compile("^[0-9_-]+$");
	private static File cooldownFile;
	private static FileConfiguration cooldownConfig;

	public void onEnable() {
		saveDefaultConfig();
		cooldownFile = new File(getDataFolder(), "cooldowns.yml");
		cooldownConfig = YamlConfiguration.loadConfiguration(cooldownFile);
		loadCheatCodes();
		for (Player online : Bukkit.getOnlinePlayers()) {
			loadPlayerCooldowns(online);
		}
		getServer().getPluginManager().registerEvents(new CheatCodeListener(this), this);
	}

	public void onDisable() {
	}

	private static void saveCooldownConfig() {
		try {
			cooldownConfig.save(cooldownFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadPlayerCooldowns(Player player) {
		if (cooldownConfig.isSet(player.getUniqueId().toString())) {
			ConfigurationSection cs = cooldownConfig.getConfigurationSection(player.getUniqueId().toString());
			for (String cheatCode : cs.getKeys(false)) {
				if (CheatCode.isCheatCode(cheatCode)) {
					addCooldown(new CheatCodeCooldown(player, CheatCode.getCheatCode(cheatCode), cs.getLong(cheatCode)), false);
				}
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("cheat")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				openGUI(player);
			} else {
				sender.sendMessage(ChatColor.RED + "This command can only be performed by an in-game player!");
			}
		}
		return super.onCommand(sender, cmd, label, args);
	}

	private void loadCheatCodes() {
		FileConfiguration cfg = getConfig();
		if (cfg.isSet("cheats")) {
			for (String key : getConfig().getConfigurationSection("cheats").getKeys(false)) {
				if (pattern.matcher(key).matches()) {
					if (cfg.isSet("cheats." + key + ".commands"))
						if (cfg.isList("cheats." + key + ".commands")) {
							List<String> commands = cfg.getStringList("cheats." + key);
							new CheatCode(key, (String[]) commands.toArray(new String[commands.size()]), (cfg.isSet("cheats." + key + ".cooldown") ? cfg.getLong("cheats." + key + ".cooldown") : 0));
						} else {
							System.err.println("The cheat code '" + key + "' doesn't have any commands to perform.");
						}
					else
						System.err.println("The cheatcode '" + key + "' can't be loaded because no commands have been set.");
				} else {
					System.err.println("The cheatcode can only contain numbers and dashes! (given: '" + key + "')");
				}
			}
		}
	}

	public void openGUI(Player player) {
		Inventory inv = Bukkit.createInventory(player, 45, ChatColor.GREEN + ChatColor.BOLD.toString() + "Cheats");
		ItemStack screen = setDisplayName(new ItemStack(Material.WOOL, 1, (byte) 11), ChatColor.GRAY + ChatColor.BOLD.toString());
		inv.setItem(1, screen);
		inv.setItem(2, screen);
		inv.setItem(3, screen);

		inv.setItem(10, key_7);
		inv.setItem(11, key_8);
		inv.setItem(12, key_9);
		inv.setItem(18, dash);
		inv.setItem(19, key_4);
		inv.setItem(20, key_5);
		inv.setItem(21, key_6);
		inv.setItem(28, key_1);
		inv.setItem(29, key_2);
		inv.setItem(30, key_3);
		inv.setItem(38, key_0);

		inv.setItem(15, enter);
		inv.setItem(25, back);
		inv.setItem(33, close);
		player.openInventory(inv);
	}
}
