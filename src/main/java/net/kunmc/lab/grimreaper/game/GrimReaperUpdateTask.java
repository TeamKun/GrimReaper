package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.gameprocess.GameProcess;
import org.bukkit.scheduler.BukkitRunnable;

public class GrimReaperUpdateTask extends BukkitRunnable {

    private boolean first_flag;
    GrimReaperUpdateTask(boolean first_flag){
        this.first_flag = first_flag;
    }

    @Override
    public void run() {
        if (GameController.runningMode != GameController.GameMode.MODE_RANDOM) {
            return;
        }
        GameProcess.updateGrimReaper(first_flag, GameController.GameMode.MODE_RANDOM, null, null);
        if (first_flag){
            first_flag = false;
        }
    }
}
