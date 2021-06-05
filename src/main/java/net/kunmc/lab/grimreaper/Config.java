package net.kunmc.lab.grimreaper;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Config {

    // 処理判定のスパン
    public static int procInterval;

    // 死亡判定するための設定
    public static int fov; // 視野角
    public static double aspectRatio;
    public static double farClipDistance; // See: https://logicalbeat.jp/blog/815/

    // 死神関連の変数
    public static int grimReaperNum;
    public static int grimReaperUpdateInterval;

    public static void loadConfig(boolean isReload) {

        GrimReaper plugin = GrimReaper.getPlugin();

        plugin.saveDefaultConfig();

        if (isReload) {
            plugin.reloadConfig();
        }

        FileConfiguration config = plugin.getConfig();

        procInterval = config.getInt("procInterval");
        fov = config.getInt("fov");
        aspectRatio = config.getDouble("aspectRatioWide") / config.getDouble("aspectRatioHeight");
        farClipDistance = config.getDouble("farClipDistance");
        grimReaperNum = config.getInt("grimReaperNum");
        grimReaperUpdateInterval = config.getInt("grimReaperUpdateInterval");
    }
}