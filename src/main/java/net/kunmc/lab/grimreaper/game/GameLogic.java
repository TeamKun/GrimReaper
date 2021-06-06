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
import java.util.stream.Collectors;

import static net.kunmc.lab.grimreaper.game.GameController.players;
import static net.kunmc.lab.grimreaper.game.GameController.runningMode;
import static org.bukkit.Bukkit.getLogger;

public class GameLogic {
    public static final GameLogic instance = new GameLogic();

    public void mainLogic() {
        // kill処理
        new BukkitRunnable() {
            @Override
            public void run() {
                if (runningMode == GameController.GameMode.MODE_NEUTRAL)
                    return;
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
        }.runTaskTimer(GrimReaper.getPlugin(), 0L, Config.killProcessTickInterval);

        // 死神更新処理
        new BukkitRunnable(){
            @Override
            public void run() {
                if (runningMode != GameController.GameMode.MODE_RANDOM)
                    return;

                GameProcess.updateGrimReaper();
                Bukkit.broadcastMessage(DecolationConst.RED + MessageUtil.MSG_LINE);
                Bukkit.broadcastMessage(DecolationConst.RED + "死神が更新されました");
                for (Player gr : GameController.GrimReapers) {
                    Bukkit.broadcastMessage(DecolationConst.RED + gr.getName());
                }
                Bukkit.broadcastMessage(DecolationConst.RED + "を見ると死にます");
                Bukkit.broadcastMessage(DecolationConst.RED + Integer.toString(Config.killProcessTickInterval * 20) + "秒ごとに死神は変わります");
                Bukkit.broadcastMessage(DecolationConst.RED + MessageUtil.MSG_LINE);

            }
        }.runTaskTimer(GrimReaper.getPlugin(), 0L, Config.grimReaperUpdateTickInterval);
    }

    public void onPlayerRestart(Player player) {
        if (runningMode != GameController.GameMode.MODE_NEUTRAL)
            GameController.players.computeIfAbsent(player.getPlayerProfile().getId(), p -> new PlayerState(player));
    }
}
