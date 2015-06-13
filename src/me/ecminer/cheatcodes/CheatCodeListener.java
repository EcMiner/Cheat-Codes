package me.ecminer.cheatcodes;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CheatCodeListener implements Listener {

	private final Main plugin;

	public CheatCodeListener(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		plugin.loadPlayerCooldowns(evt.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		Main.removeAllFromUser(evt.getPlayer());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent evt) {
		Player player = (Player) evt.getWhoClicked();
		if (evt.getInventory().getTitle().equals(ChatColor.GREEN + ChatColor.BOLD.toString() + "Cheats")) {
			evt.setCancelled(true);
			int slot = evt.getRawSlot();
			Inventory inv = evt.getInventory();
			if ((slot >= 0 && slot <= evt.getInventory().getSize() - 1) && evt.getCurrentItem() != null) {
				if (slot == 10 || slot == 11 || slot == 12 || slot == 18 || slot == 19 || slot == 20 || slot == 21 || slot == 28 || slot == 29 || slot == 30 || slot == 38) {
					addKey(inv, ChatColor.stripColor(evt.getCurrentItem().getItemMeta().getDisplayName()));
				} else if (slot == 15) {
					if (inv.getItem(1).getItemMeta().getDisplayName().length() > 4) {
						String cheatCode = ChatColor.stripColor(inv.getItem(1).getItemMeta().getDisplayName());
						if (CheatCode.isCheatCode(cheatCode)) {
							CheatCode code = CheatCode.getCheatCode(cheatCode);
							if (Main.hasCooldown(player, code)) {
								CheatCodeCooldown cooldown = Main.getCooldown(player, code);
								if (!cooldown.isExpired()) {
									player.sendMessage(ChatColor.RED + "You still have a " + (new DecimalFormat("##.#").format(cooldown.getTimeLeft())) + " second cooldown!");
									return;
								} else {
									Main.removeCooldown(player, code, true);
								}
							}
							code.execute(player);
							player.sendMessage(ChatColor.GREEN + "The cheatcode '" + cheatCode + "' has been accepted!");
							if (code.getCooldownTime() > 0) {
								Main.addCooldown(new CheatCodeCooldown(player, code, System.currentTimeMillis() + code.getCooldownTime()), true);
							}
						} else {
							player.sendMessage(ChatColor.RED + "The cheatcode '" + cheatCode + "' is incorrect!");
						}
						player.closeInventory();
					} else {
						player.sendMessage(ChatColor.RED + "You haven't entered a cheatcode!");
					}
				} else if (slot == 25) {
					addKey(inv, "");
				} else if (slot == 33) {
					player.closeInventory();
				}
			}
		}
	}

	private void addKey(Inventory inv, String key) {
		ItemStack item = inv.getItem(1);
		if (key == "") {
			if (item.getItemMeta().getDisplayName().length() > 4) {
				String code = item.getItemMeta().getDisplayName().substring(0, item.getItemMeta().getDisplayName().length() - 1);
				Main.setDisplayName(item, code);
				Main.setDisplayName(inv.getItem(2), code);
				Main.setDisplayName(inv.getItem(3), code);
			}
		} else {
			String code = item.getItemMeta().getDisplayName() + key;
			Main.setDisplayName(item, code);
			Main.setDisplayName(inv.getItem(2), code);
			Main.setDisplayName(inv.getItem(3), code);
		}
	}

}
