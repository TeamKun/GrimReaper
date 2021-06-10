package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.gameprocess.Frustum;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.kunmc.lab.grimreaper.game.GameController.players;

public class GrimReaperKillTask extends BukkitRunnable {

    @Override
    public void run() {
        if (GameController.runningMode == GameController.GameMode.MODE_NEUTRAL) {
            return;
        }
        GameController.grimReapers.forEach(gr -> {
            // 死神の人がログアウト時には処理を飛ばす
            if (players.values().contains(gr)){
                players.values().stream()
                        .filter(GrimReaperKillTask::isKillTargetPlayer)
                        .map(e -> players.computeIfAbsent(e.getPlayerProfile().getId(), p -> e))
                        .forEach(p -> {
                            if (shouldBeKilled(p.getPlayer(), gr)) {
                                killPlayer(p.getPlayer(), gr);
                            }
                        });
            }
       });
    }

    public static boolean shouldBeKilled(Player target, Player grimReaper) {
        Frustum grimReaperFrustum = GameController.frustum.clone().getFieldOfView(target.getEyeLocation());
        return grimReaperFrustum.isInSight(target, grimReaper);
    }

    private static boolean isKillTargetPlayer(Player player) {
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            return false;
        if (GameController.grimReapers.contains(player))
            return false;
        return player.isValid();
    }

    private void killPlayer(Player player, Player grimReaper) {
        // ゲームモードがクリエイティブorスぺクテイターの場合はkillしない
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        // 死神同士でkillはしない、ここのロジック以前にフィルタされるはずだが念のため入れておく
        if (GameController.grimReapers.contains(player)){
            return;
        }
        player.damage(1000);
        Bukkit.broadcastMessage(String.format("%sは死神%sに殺された", player.getName(), grimReaper.getName()));
    }
}
