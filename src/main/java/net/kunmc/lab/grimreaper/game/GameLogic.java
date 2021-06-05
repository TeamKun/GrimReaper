package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.GrimReaper;
import net.kunmc.lab.grimreaper.gameprocess.GameProcess;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiConsumer;

import static net.kunmc.lab.grimreaper.game.GameController.players;
import static net.kunmc.lab.grimreaper.game.GameController.runningMode;

public class GameLogic {
    public static final GameLogic instance = new GameLogic();

    public void mainLogic() {
        long taskTimerPeriod = 10L;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (runningMode == GameController.GameMode.MODE_NEUTRAL)
                    return;

                // Kill処理
                if (GameController.gameTimerTick % Config.procInterval == 0) {
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
                if (GameController.gameTimerTick % Config.grimReaperUpdateInterval == 0) {
                    GameProcess.updateGrimReaper();
                }

                /*
                Main Logic記載
                 */
                GameController.incrementTimer(taskTimerPeriod);
            }
        }.runTaskTimer(GrimReaper.getPlugin(), 0L, taskTimerPeriod);
    }

    public void onPlayerRestart(Player player, BiConsumer<Player, Location> teleportFunc) {
        PlayerState state = GameController.players.computeIfAbsent(player.getPlayerProfile().getId(), p -> new PlayerState(player));

        //state.clearTime();

        //if (runningMode == Const.GameLogicMode.MODE_BE_OUT) {
        //    boolean isSeenByNobody = ModeController.killerList.stream().noneMatch(killer -> shouldBeKilled(killer, player));
        //    if (isSeenByNobody) {
        //        ModeController.killerList.stream().findFirst().ifPresent(killer -> {
        //            RayTraceResult result = killer.rayTraceBlocks(Config.farClip);
        //            if (result != null)
        //                teleportFunc.accept(player, result.getHitPosition().toLocation(killer.getWorld()));
        //        });
        //    }
        //}
    }

}
