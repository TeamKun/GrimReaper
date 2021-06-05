package net.kunmc.lab.grimreaper.common;

public class MessageUtil {
    // System Messages
    public static final String PLUGIN_NAME = "GrimReaperPlugin";
    public static final String START = "スタート";
    public static final String END = "終了";

    // Error Messages
    public static final String ERROR_MSG_LACK_ARGS = "引数が不足しています";
    public static final String ERROR_MSG_PLAYER_NOT_FOUND = "指定されたプレイヤーが見つかりません";

    // Other Messages
    public static final String MSG_LINE = "==============================";

    public static String getStartMessage() {
        return PLUGIN_NAME + START;
    }

    public static String getEndMessage() {
        return PLUGIN_NAME + END;
    }
}
