package net.kunmc.lab.grimreaper.gameprocess;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.game.GameController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;

public class GameProcess implements Listener {
    /**
     * Game で処理するPlayerのチェック
     *   CREATEIVE or SPECTATOR or 死神 or プレイヤーが死亡しているなら処理しない
     */
    public static boolean isKillTargetPlayer(Player player) {
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            return false;
        if (GameController.GrimReapers.contains(player))
            return false;
        return player.isValid();
    }

    public static boolean notCreativeOrSpectatorPlayer(Player player) {
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            return false;
        return player.isValid();
    }

    /**
     * kill判定
     */
    public static boolean shouldBeKilled(Player target, Player grimReaper) {
        Frustum grimReaperFrustum = GameController.frustum.clone().getFieldOfView(target.getEyeLocation());
        return grimReaperFrustum.isInSight(target, grimReaper);
    }

    /**
     * kill処理
     */
    public static void killPlayer(Player player) {
        // ゲームモードがクリエイティブorスぺクテイターの場合はkillしない
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        // 死神同士でkillはしない、ここのロジック以前にフィルタされるはずだが念のため入れておく
        if (GameController.GrimReapers.contains(player)){
            return;
        }
        player.damage(1000);
    }

    /**
     * 死神更新処理
     */
    public static void updateGrimReaper() {
        List<Player> grimReapers = Bukkit.getOnlinePlayers().stream()
                .filter(GameProcess::notCreativeOrSpectatorPlayer)
                .collect(Collectors.toList());
        Collections.shuffle(grimReapers);

        GameController.GrimReapers = grimReapers.subList(0, Config.grimReaperNum);
    }
}