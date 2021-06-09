package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.gameprocess.GameProcess;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class GrimReaperUpdateTask extends BukkitRunnable {

    @Override
    public void run() {
        if (GameController.runningMode != GameController.GameMode.MODE_RANDOM) {
            return;
        }
        GameProcess.updateGrimReaper(false, GameController.GameMode.MODE_RANDOM, null, null);
    }
}