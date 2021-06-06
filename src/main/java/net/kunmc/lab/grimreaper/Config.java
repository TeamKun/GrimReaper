package net.kunmc.lab.grimreaper;

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

        killProcessTickInterval = config.getInt("tick");
        fov = config.getInt("fov");
        aspectRatioWide = config.getDouble("aspectRatioWide");
        aspectRatioHeight = config.getDouble("aspectRatioHeight");
        aspectRatio = aspectRatioWide / aspectRatioWide;
        farClipDistance = config.getDouble("farClipDistance");
        grimReaperNum = config.getInt("grimReaperNum");
        grimReaperUpdateTickInterval = config.getInt("grimReaperUpdateTick");
    }
}