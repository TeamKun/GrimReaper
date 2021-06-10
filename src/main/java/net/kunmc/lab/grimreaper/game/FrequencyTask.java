package net.kunmc.lab.grimreaper.game;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.kunmc.lab.grimreaper.game.GameController.players;

/**
 * 高頻度で実行・更新したい処理用タスク
 *   ログイン・ログアウトに関連したユーザ処理
 */
public class FrequencyTask extends BukkitRunnable {
    @Override
    public void run() {
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
    }
}
