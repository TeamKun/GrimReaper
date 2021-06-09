package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.GrimReaper;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {

    private static BukkitTask killGrimReaperTask;
    private static BukkitTask updateGrimReaperTask;
    private static BukkitTask everyTimeTask;

    /**
     * 死神によるKillタスク
     */
    public static void runKillGrimReaperTask() {
        if (killGrimReaperTask != null) {
            killGrimReaperTask.cancel();
        }
        killGrimReaperTask = new GrimReaperKillTask().runTaskTimer(GrimReaper.getPlugin(), 0, Config.killProcessTickInterval);
    }

    /**
     * 死神更新タスク
     */
    public static void runUpdateGrimReaperTask() {
        if (updateGrimReaperTask != null) {
            updateGrimReaperTask.cancel();
        }
        updateGrimReaperTask = new GrimReaperUpdateTask().runTaskTimer(GrimReaper.getPlugin(), 0, Config.grimReaperUpdateTickInterval);
    }

    /**
     * ゲーム参加しているユーザを更新するタスク
     */
    public static void everyTimeTask() {
        everyTimeTask = new EveryTimeTask().runTaskTimer(GrimReaper.getPlugin(), 0, 1);
    }
}