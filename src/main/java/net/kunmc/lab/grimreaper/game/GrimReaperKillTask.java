package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.gameprocess.Frustum;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.kunmc.lab.grimreaper.game.GameController.players;

public class GrimReaperKillTask extends BukkitRunnable {

    @Override
    public void run() {
        if (GameController.runningMode == GameController.GameMode.MODE_NEUTRAL) {
            return;
        }
         /*
          playerのログイン・ログアウトによって変に判定が残ってしまう（GameControllerに古い情報のPlayer情報が残る）ので
          毎回リセットする
         */

        // 単なるPlayerはオンライン情報を全て取得すれば良い
        players.clear();
        players = Bukkit.getOnlinePlayers().stream().collect(
                Collectors.toMap(e -> e.getPlayerProfile().getId(), e -> e));

        /*
         * 死神がログアウト・ログインする時に前の情報が残り、新規の情報がGameControllerのリストに入らない
         * 対応として以下を行う
         *   ログイン中のユーザに死神がいない場合、前の死神の情報をそのまま持つ
         *      死神の死亡判定時に存在しない死神は除くようにする必要がある
         *   ログイン中のユーザに死神がいる場合、ログイン中の情報を持つ
         */
        List<Player> grimReapers = new ArrayList<Player>();
        for(Player grimReaper: GameController.grimReapers){
            if (!players.containsKey(grimReaper.getUniqueId())){
                grimReapers.add(grimReaper);
            } else {
                grimReapers.add(players.get(grimReaper.getUniqueId()));
            }
        }
        GameController.grimReapers.clear();
        GameController.grimReapers = grimReapers;

        // 全Playerの発光を全部取る
        for (Player player : GameController.players.values()) {
            player.setGlowing(false);
        }
        // 死神を発光させる
        for (Player grimReaper : GameController.grimReapers) {
            grimReaper.setGlowing(true);
        }

        // kill処理
        GameController.grimReapers.forEach(gr -> {
            // 死神の人がログアウト時 or 死亡時には処理を飛ばす
            if (players.values().contains(gr) && gr.isValid()){
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
        Bukkit.broadcastMessage(String.format("%sは死神%sを見て死亡した", player.getName(), grimReaper.getName()));
    }
}
