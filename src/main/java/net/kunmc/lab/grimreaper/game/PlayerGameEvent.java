package net.kunmc.lab.grimreaper.game;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static net.kunmc.lab.grimreaper.gameprocess.GameProcess.isKillTargetPlayer;

public class PlayerGameEvent implements Listener {
    /**
     * ログイン時(途中参加が可能になる)
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isKillTargetPlayer(event.getPlayer()))
            return;

        GameLogic.instance.onPlayerRestart(event.getPlayer());
    }

    /**
     * プレイヤーがリスポーンしたとき
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        if (!isKillTargetPlayer(event.getPlayer()))
            return;

        GameLogic.instance.onPlayerRestart(event.getPlayer());
    }

    /**
     * スペクテイター、クリエが解除された時
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (!isKillTargetPlayer(event.getPlayer()))
            return;

        GameLogic.instance.onPlayerRestart(event.getPlayer());
    }

    /**
     * ログアウト時にPlayer情報を削除しないと情報が残って再ログイン後にバグる
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerGameQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (GameController.players.containsKey(id))
            GameController.players.remove(id);
    }
}
