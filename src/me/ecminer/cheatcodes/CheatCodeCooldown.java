package me.ecminer.cheatcodes;

import java.util.UUID;

import org.bukkit.entity.Player;

public class CheatCodeCooldown {

	private final CheatCode cheatCode;
	private final long endTime;
	private final UUID playerId;

	public CheatCodeCooldown(Player player, CheatCode cheatCode, long endTime) {
		this(player.getUniqueId(), cheatCode, endTime);
	}

	public CheatCodeCooldown(UUID playerId, CheatCode cheatCode, long endTime) {
		this.playerId = playerId;
		this.cheatCode = cheatCode;
		this.endTime = endTime;
	}

	public CheatCode getCheatCode() {
		return cheatCode;
	}

	public long getEndTime() {
		return endTime;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public boolean isExpired() {
		return endTime - System.currentTimeMillis() <= 0;
	}

	public double getTimeLeft() {
		return (new Long(endTime).doubleValue() - new Long(System.currentTimeMillis()).doubleValue()) / 1000d;
	}

}
