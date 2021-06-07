package net.kunmc.lab.grimreaper.command;

public class CommandConst {
    // メインコマンド
    public final static String MAIN_COMMAND = "grm";

    // スタート系
    public final static String COMMAND_ASSIGN_MODE_ON = "assign-mode";
    public final static String COMMAND_RANDOM_MODE_ON = "random-mode";

    // 終了系
    public final static String COMMAND_MODE_OFF = "mode-off";

    // コンフィグ管理系
    public final static String COMMAND_CONFIG = "config";
    public final static String COMMAND_CONFIG_RELOAD = "reload";
    public final static String COMMAND_CONFIG_SET = "set";
    public final static String COMMAND_CONFIG_SHOW = "show";

    // コンフィグ設定対象
    public final static String COMMAND_CONFIG_KILL_TICK_INTERVAL = "tick";
    public final static String COMMAND_CONFIG_F0V = "fov";
    public final static String COMMAND_CONFIG_ASPECT_RATIO_WIDE = "aspectRatioWide";
    public final static String COMMAND_CONFIG_ASPECT_RATIO_HEIGHT = "aspectRatioHeight";
    public final static String COMMAND_CONFIG_FAR_CLIP_DISTANCE = "farClipDistance";
    public final static String COMMAND_CONFIG_GRIM_REAPER_NUM = "grimReaperNum";
    public final static String COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL = "grimReaperUpdateTick";
}
