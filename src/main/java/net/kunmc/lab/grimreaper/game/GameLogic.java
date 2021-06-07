package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.GrimReaper;
import net.kunmc.lab.grimreaper.common.DecolationConst;
import net.kunmc.lab.grimreaper.common.MessageUtil;
import net.kunmc.lab.grimreaper.gameprocess.GameProcess;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static net.kunmc.lab.grimreaper.game.GameController.*;
import static org.bukkit.Bukkit.getLogger;

public class GameLogic {
    public static final GameLogic instance = new GameLogic();

    public void mainLogic() {
        // kill処理
        new BukkitRunnable() {
            @Override
            public void run() {
                if (runningMode == GameController.GameMode.MODE_NEUTRAL) {
                    return;
                }
                int killSecInterval = Config.killProcessTickInterval/20 == 0 ? 1 : Config.killProcessTickInterval/20;
                int grimReaperUpdateSecInterval = Config.grimReaperUpdateTickInterval/20 == 0 ? 1 : Config.grimReaperUpdateTickInterval/20;
                if (GameController.timer % grimReaperUpdateSecInterval == 0){
                    updateGrimReaper();
                }
                if (GameController.timer % killSecInterval == 0){
                    killPlayers();
                }
                GameController.timer++;
            }
        }.runTaskTimer(GrimReaper.getPlugin(), 0L, 20L);
    }
    private void killPlayers() {
        GameController.GrimReapers.forEach(gr -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(GameProcess::isKillTargetPlayer)
                    .map(e -> players.computeIfAbsent(e.getPlayerProfile().getId(), p -> new PlayerState(e)))
                    .forEach(p -> {
                        if (GameProcess.shouldBeKilled(p.getPlayer(), gr)) {
                            GameProcess.killPlayer(p.getPlayer());
                        }
                    });
        });
    }

    // 死神更新処理
    private void updateGrimReaper() {
        if (runningMode != GameController.GameMode.MODE_RANDOM)
            return;

        GameProcess.updateGrimReaper();
        Bukkit.broadcastMessage(MessageUtil.MSG_LINE);
        Bukkit.broadcastMessage("死神が更新されました");
        for (Player gr : GameController.GrimReapers) {
            Bukkit.broadcastMessage(DecolationConst.RED + gr.getName());
        }
        Bukkit.broadcastMessage(DecolationConst.RED + "を見ると死にます");
        Bukkit.broadcastMessage(Integer.toString(Config.grimReaperUpdateTickInterval / 20) + "秒ごとに死神は変わります");
        Bukkit.broadcastMessage(MessageUtil.MSG_LINE);
    }

    public void onPlayerRestart(Player player) {
        if (runningMode != GameController.GameMode.MODE_NEUTRAL)
            GameController.players.computeIfAbsent(player.getPlayerProfile().getId(), p -> new PlayerState(player));
    }
}
