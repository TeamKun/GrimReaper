package net.kunmc.lab.grimreaper;

import net.kunmc.lab.grimreaper.command.CommandConst;
import net.kunmc.lab.grimreaper.command.CommandController;
import net.kunmc.lab.grimreaper.common.MessageUtil;
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
        getLogger().info(MessageUtil.getStartMessage());

        // イベントクラス読み込み
        getServer().getPluginManager().registerEvents(new PlayerGameEvent(), this);

        // メインロジック実行
        GameLogic.instance.mainLogic();

        getCommand(CommandConst.COMMAND_ASSIGN_MODE_ON).setExecutor(new CommandController());
        getCommand(CommandConst.COMMAND_RANDOM_MODE_ON).setExecutor(new CommandController());
        getCommand(CommandConst.COMMAND_CONFIG).setExecutor(new CommandController());
        getCommand(CommandConst.COMMAND_CONFIG_RELOAD).setExecutor(new CommandController());
        getCommand(CommandConst.COMMAND_CONFIG_SET).setExecutor(new CommandController());

        Config.loadConfig(false);
    }

    @Override
    public void onDisable() {
        getLogger().info(MessageUtil.getEndMessage());
    }
}
