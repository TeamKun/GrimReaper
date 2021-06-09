package net.kunmc.lab.grimreaper.game;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static net.kunmc.lab.grimreaper.game.GameController.players;

/**
 * 毎回実行するタスク
 */
public class EveryTimeTask extends BukkitRunnable {
    @Override
    public void run() {
        // Playerをゲームに追加
        Bukkit.getOnlinePlayers().stream()
                .map(e -> players.computeIfAbsent(e.getPlayerProfile().getId(), p -> e));
    }
}
