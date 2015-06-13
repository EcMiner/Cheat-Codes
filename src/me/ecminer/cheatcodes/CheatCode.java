package me.ecminer.cheatcodes;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CheatCode {

	private static Map<String, CheatCode> cheatCodes = new HashMap<String, CheatCode>();

	public static boolean isCheatCode(String cheatCode) {
		return cheatCodes.containsKey(cheatCode.toLowerCase());
	}

	public static CheatCode getCheatCode(String cheatCode) {
		return cheatCodes.get(cheatCode.toLowerCase());
	}

	private final String cheatCode;
	private final String[] commands;
	private final long cooldownTime;

	public CheatCode(String cheatCode, String[] commands, long cooldownTime) {
		this.cheatCode = cheatCode;
		this.commands = commands;
		this.cooldownTime = cooldownTime;
		cheatCodes.put(cheatCode.toLowerCase(), this);
	}

	public String getCheatCode() {
		return cheatCode;
	}

	public String[] getCommands() {
		return commands;
	}

	public long getCooldownTime() {
		return cooldownTime;
	}

	public void execute(Player player) {
		for (String command : commands) {
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
		}
	}

}
