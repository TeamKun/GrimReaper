package net.kunmc.lab.grimreaper.game;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.kunmc.lab.grimreaper.gameprocess.GameProcess.isKillTargetPlayer;

public class PlayerGameEvent implements Listener {
    /**
     * ログイン時に爆破モード実行中の場合プレイヤーを爆破対象リストに追加する(途中参加が可能になる)
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isKillTargetPlayer(event.getPlayer()))
            return;

        GameLogic.instance.onPlayerRestart(event.getPlayer(), Player::teleport);
    }

    /**
     * プレイヤーがリスポーンしたとき
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        if (!isKillTargetPlayer(event.getPlayer()))
            return;

        GameLogic.instance.onPlayerRestart(event.getPlayer(), Player::teleport);
    }

    /**
     * スペクテイター、クリエが解除された時
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (!isKillTargetPlayer(event.getPlayer()))
            return;

        GameLogic.instance.onPlayerRestart(event.getPlayer(), Player::teleport);
    }
}
