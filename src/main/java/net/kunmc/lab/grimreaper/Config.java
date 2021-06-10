package net.kunmc.lab.grimreaper;

import net.kunmc.lab.grimreaper.command.CommandConst;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    // 判定処理のスパン
    public static int killProcessTickInterval;

    // 死亡判定するための設定
    public static int fov; // 視野角
    public static double aspectRatioWide;
    public static double aspectRatioHeight;
    public static double aspectRatio;
    public static double farClipDistance; // See: https://logicalbeat.jp/blog/815/

    // 死神関連の変数
    public static int grimReaperNum;
    public static int grimReaperUpdateTickInterval;

    public static void loadConfig(boolean isReload) {

        GrimReaper plugin = GrimReaper.getPlugin();

        plugin.saveDefaultConfig();

        if (isReload) {
            plugin.reloadConfig();
        }

        FileConfiguration config = plugin.getConfig();

        killProcessTickInterval = config.getInt(CommandConst.COMMAND_CONFIG_KILL_TICK_INTERVAL);
        fov = config.getInt(CommandConst.COMMAND_CONFIG_F0V);
        aspectRatioWide = config.getDouble(CommandConst.COMMAND_CONFIG_ASPECT_RATIO_WIDE);
        aspectRatioHeight = config.getDouble(CommandConst.COMMAND_CONFIG_ASPECT_RATIO_HEIGHT);
        aspectRatio = aspectRatioWide / aspectRatioWide;
        farClipDistance = config.getDouble(CommandConst.COMMAND_CONFIG_FAR_CLIP_DISTANCE);
        grimReaperNum = config.getInt(CommandConst.COMMAND_CONFIG_GRIM_REAPER_NUM);
        grimReaperUpdateTickInterval = config.getInt(CommandConst.COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL);
    }
}