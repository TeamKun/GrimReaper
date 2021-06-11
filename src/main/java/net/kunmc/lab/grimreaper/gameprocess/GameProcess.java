package net.kunmc.lab.grimreaper.gameprocess;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.common.DecolationConst;
import net.kunmc.lab.grimreaper.common.MessageConst;
import net.kunmc.lab.grimreaper.game.GameController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameProcess implements Listener {
    public static boolean notCreativeOrSpectatorPlayer(Player player) {
        if (player.getGameMode().equals(GameMode.CREATIVE) || player.getGameMode().equals(GameMode.SPECTATOR))
            return false;
        return player.isValid();
    }

    /**
     * senderとargsを使うのはassign-modeのみ
     * @param first_flag
     * @param mode
     * @param sender
     * @param args
     */
    public static void updateGrimReaper(boolean first_flag, GameController.GameMode mode, CommandSender sender, String[] args) {
       if (mode == GameController.GameMode.MODE_ASSIGN) {
            GameController.grimReapers = Arrays.stream(args)
                    .flatMap(arg -> Bukkit.selectEntities(sender, arg).stream())
                    .filter(Player.class::isInstance)
                    .map(Player.class::cast)
                    .collect(Collectors.toList());
        } else {
            // RANDOM_MODE
            List<Player> grimReapers = GameController.players.values().stream()
                    .filter(GameProcess::notCreativeOrSpectatorPlayer)
                    .collect(Collectors.toList());
            Collections.shuffle(grimReapers);

            int subListMax = Math.min(Config.grimReaperNum, grimReapers.size());
            GameController.grimReapers = grimReapers.subList(0, subListMax);
        }

        Bukkit.broadcastMessage(MessageConst.MSG_LINE);
        if (first_flag && mode == GameController.GameMode.MODE_ASSIGN) {
            Bukkit.broadcastMessage("死神指定モードを開始しました");
        } else if (first_flag && mode == GameController.GameMode.MODE_RANDOM) {
            Bukkit.broadcastMessage("ランダムモードを開始しました");
        } else {
            Bukkit.broadcastMessage("死神が更新されました");
        }
        for (Player gr : GameController.grimReapers) {
            Bukkit.broadcastMessage(DecolationConst.RED + gr.getName());
        }
        Bukkit.broadcastMessage(DecolationConst.RED + "を見ると死亡します");
        if (mode == GameController.GameMode.MODE_RANDOM)
            Bukkit.broadcastMessage(Integer.toString(Config.grimReaperUpdateTickInterval / 20) + "秒ごとに死神は変わります");
        Bukkit.broadcastMessage(MessageConst.MSG_LINE);
    }
}