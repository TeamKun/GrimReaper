package net.kunmc.lab.grimreaper;

import net.kunmc.lab.grimreaper.command.CommandConst;
import net.kunmc.lab.grimreaper.command.CommandController;
import net.kunmc.lab.grimreaper.common.MessageConst;
import net.kunmc.lab.grimreaper.game.GameLogic;
import net.kunmc.lab.grimreaper.game.PlayerGameEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class GrimReaper extends JavaPlugin {

    private static GrimReaper plugin;

    public static GrimReaper getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info(MessageConst.PLUGIN_NAME + " start");
        Config.loadConfig(false);

        // イベントクラス読み込み
        getServer().getPluginManager().registerEvents(new PlayerGameEvent(), this);

        // メインロジック実行
        GameLogic.instance.mainLogic();

        getCommand(CommandConst.MAIN_COMMAND).setExecutor(new CommandController());
    }

    @Override
    public void onDisable() {
        getLogger().info(MessageConst.PLUGIN_NAME + " end");
    }
}
