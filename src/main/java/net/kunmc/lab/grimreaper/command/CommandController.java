package net.kunmc.lab.grimreaper.command;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.common.DecolationConst;
import net.kunmc.lab.grimreaper.common.MessageConst;
import net.kunmc.lab.grimreaper.game.GameController;
import net.kunmc.lab.grimreaper.gameprocess.GameProcess;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;

public class CommandController implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions.add(CommandConst.COMMAND_ASSIGN_MODE_ON);
            completions.add(CommandConst.COMMAND_RANDOM_MODE_ON);
            completions.add(CommandConst.COMMAND_CONFIG);
            completions.add(CommandConst.COMMAND_MODE_OFF);
        } else if (args.length > 1 && args[0].equals(CommandConst.COMMAND_ASSIGN_MODE_ON)) {
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        } else if (args.length == 2 && args[0].equals(CommandConst.COMMAND_CONFIG)) {
            completions.add(CommandConst.COMMAND_CONFIG_SHOW);
            completions.add(CommandConst.COMMAND_CONFIG_SET);
            completions.add(CommandConst.COMMAND_CONFIG_RELOAD);
        } else if (args.length == 3 && args[1].equals(CommandConst.COMMAND_CONFIG_SET)){
            completions.add(CommandConst.COMMAND_CONFIG_KILL_TICK_INTERVAL);
            completions.add(CommandConst.COMMAND_CONFIG_F0V);
            completions.add(CommandConst.COMMAND_CONFIG_ASPECT_RATIO_WIDE);
            completions.add(CommandConst.COMMAND_CONFIG_ASPECT_RATIO_HEIGHT);
            completions.add(CommandConst.COMMAND_CONFIG_FAR_CLIP_DISTANCE);
            completions.add(CommandConst.COMMAND_CONFIG_GRIM_REAPER_NUM);
            completions.add(CommandConst.COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL);
        }
        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sendUsage(sender);
            return false;
        }
        String commandName = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはゲーム内からのみ実行できます");
            return false;
        }
        switch (commandName) {
            case CommandConst.COMMAND_ASSIGN_MODE_ON:
                runModeAssign(sender, commandArgs);
                break;
            case CommandConst.COMMAND_RANDOM_MODE_ON:
                runModeRandom(sender, commandArgs);
                break;
            case CommandConst.COMMAND_MODE_OFF:
                runModeOff();
                break;
            case CommandConst.COMMAND_CONFIG:
                runConfig(sender, commandArgs);
                break;
            default:
                sender.sendMessage(DecolationConst.RED + "存在しないコマンド");
                sendUsage(sender);

        }
        return true;
    }

    private static void runModeAssign(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(DecolationConst.RED + MessageConst.ERROR_MSG_LACK_ARGS);
            return;
        }

        for (String arg : args) {
            if (Bukkit.selectEntities(sender, arg).isEmpty()) {
                sender.sendMessage(DecolationConst.RED + MessageConst.ERROR_MSG_PLAYER_NOT_FOUND);
                return;
            }
        }

        if (GameController.runningMode.equals(GameController.GameMode.MODE_ASSIGN)) {
            sender.sendMessage("すでに実行中です");
            return;
        }

        GameController.GrimReapers = Arrays.stream(args)
                .flatMap(arg -> Bukkit.selectEntities(sender, arg).stream())
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());

        GameController.controller(GameController.GameMode.MODE_ASSIGN);

        Bukkit.broadcastMessage(MessageConst.MSG_LINE);
        Bukkit.broadcastMessage("死神指定モードを開始しました");
        for (String arg : args) {
            Bukkit.broadcastMessage(DecolationConst.RED + arg);
        }
        Bukkit.broadcastMessage(DecolationConst.RED + "を見ると死亡します");
        Bukkit.broadcastMessage(MessageConst.MSG_LINE);
    }


    private static void runModeRandom(CommandSender sender, String[] args) {
        /*
         * ex1. gr-random
         */
        ArrayList<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.size() < Config.grimReaperNum){
            sender.sendMessage(DecolationConst.RED + "死神の数をオンラインプレイヤーの数未満にして下さい");
            return;
        }

        if (GameController.runningMode.equals(GameController.GameMode.MODE_RANDOM)) {
            sender.sendMessage("すでに実行中です");
            return;
        }

        GameProcess.updateGrimReaper();

        GameController.controller(GameController.GameMode.MODE_RANDOM);

        Bukkit.broadcastMessage(MessageConst.MSG_LINE);
        Bukkit.broadcastMessage("ランダムモードを開始しました");
        for (Player gr : GameController.GrimReapers) {
            Bukkit.broadcastMessage(DecolationConst.RED + gr.getName());
        }
        Bukkit.broadcastMessage(DecolationConst.RED + "を見ると死亡します");
        Bukkit.broadcastMessage(Integer.toString(Config.grimReaperUpdateTickInterval/20) + "秒ごとに死神は変わります");
        Bukkit.broadcastMessage(MessageConst.MSG_LINE);
    }

    private static void runModeOff() {
        GameController.controller(GameController.GameMode.MODE_NEUTRAL);
        Bukkit.broadcastMessage(DecolationConst.YELLOW + "実行中のモードが終了しました");
    }

    private static void runConfig(CommandSender sender, String[] args) {
        if (args.length == 0){
        }
        if (args.length == 1 && args[0].equals(CommandConst.COMMAND_CONFIG_SHOW)) {
            sender.sendMessage(DecolationConst.GREEN + "設定値一覧:");
            sender.sendMessage(String.format("  %s: %d", CommandConst.COMMAND_CONFIG_KILL_TICK_INTERVAL, Config.killProcessTickInterval));
            sender.sendMessage(String.format("  %s: %s", CommandConst.COMMAND_CONFIG_F0V, Config.fov));
            sender.sendMessage(String.format("  %s: %.1f", CommandConst.COMMAND_CONFIG_ASPECT_RATIO_WIDE, Config.aspectRatioWide));
            sender.sendMessage(String.format("  %s: %.1f", CommandConst.COMMAND_CONFIG_ASPECT_RATIO_HEIGHT, Config.aspectRatioHeight));
            sender.sendMessage(String.format("  %s: %.1f", CommandConst.COMMAND_CONFIG_FAR_CLIP_DISTANCE, Config.farClipDistance));
            sender.sendMessage(String.format("  %s: %d", CommandConst.COMMAND_CONFIG_GRIM_REAPER_NUM, Config.grimReaperNum));
            sender.sendMessage(String.format("  %s: %d", CommandConst.COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL, Config.grimReaperUpdateTickInterval));
        } else if (args.length == 1 && args[0].equals(CommandConst.COMMAND_CONFIG_RELOAD)) {
            if (!GameController.runningMode.equals(GameController.GameMode.MODE_NEUTRAL)) {
                sender.sendMessage(DecolationConst.RED + "mode-offコマンドで実行中のモードを終了してからリロードしてください");
            } else {
                Config.loadConfig(true);
                sender.sendMessage(DecolationConst.GREEN + "コンフィグファイルをリロードしました");
            }
        } else if (args.length == 3 && args[0].equals(CommandConst.COMMAND_CONFIG_SET)) {
            if (!GameController.runningMode.equals(GameController.GameMode.MODE_NEUTRAL)) {
                sender.sendMessage(DecolationConst.RED + "mode-offコマンドで実行中のモードを終了してから設定を更新してください");
                return;
            }
            switch (args[1]){
                case CommandConst.COMMAND_CONFIG_KILL_TICK_INTERVAL:
                    Config.killProcessTickInterval = Integer.parseInt(args[2]);
                    break;
                case CommandConst.COMMAND_CONFIG_F0V:
                    Config.fov = Integer.parseInt(args[2]);
                    break;
                case CommandConst.COMMAND_CONFIG_ASPECT_RATIO_WIDE:
                    Config.aspectRatioWide = Double.parseDouble(args[2]);
                    break;
                case CommandConst.COMMAND_CONFIG_ASPECT_RATIO_HEIGHT:
                    Config.aspectRatioHeight = Double.parseDouble(args[2]);
                    break;
                case CommandConst.COMMAND_CONFIG_FAR_CLIP_DISTANCE:
                    Config.farClipDistance = Double.parseDouble(args[2]);
                    break;
                case CommandConst.COMMAND_CONFIG_GRIM_REAPER_NUM:
                    int num = Integer.parseInt(args[2]);
                    long maxGrimReaperNum = Bukkit.getOnlinePlayers().stream()
                            .filter(GameProcess::notCreativeOrSpectatorPlayer).count();

                    if (num > maxGrimReaperNum ){
                        sender.sendMessage(DecolationConst.RED + "設定値がPlayer人数を超えています");
                        getLogger().info(Integer.toString(GameController.players.size() + GameController.GrimReapers.size()));
                        return;
                    }
                    Config.grimReaperNum = num;
                    break;
                case CommandConst.COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL:
                    Config.grimReaperUpdateTickInterval = Integer.parseInt(args[2]);
                    break;
                default:
                    sender.sendMessage(DecolationConst.RED + "存在しないパラメータです");
                    return;
            }
            sender.sendMessage(String.format("%s%sの値を%sに更新しました", DecolationConst.GREEN, args[1], args[2]));
        }else{
            sendConfigUsage(sender);
            return;
        }
    }
    private static void sendUsage(CommandSender sender) {
        String usagePrefix = String.format("  /%s ", CommandConst.MAIN_COMMAND);
        String descPrefix = "    ";
        sender.sendMessage(DecolationConst.GREEN + "Usage:");
        sender.sendMessage(String.format("%s%s <player1> <player2> ..."
                ,usagePrefix, CommandConst.COMMAND_ASSIGN_MODE_ON));
        sender.sendMessage(String.format("%s指定したplayerを死神にするモード(複数指定可能)", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_RANDOM_MODE_ON));
        sender.sendMessage(String.format("%sランダムにplayerを死神にするモード", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_MODE_OFF));
        sender.sendMessage(String.format("%s現在動いているモードを停止", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_CONFIG));
        sender.sendMessage(String.format("%sConfigのUsageを出力", descPrefix));
    }

    private static void sendConfigUsage(CommandSender sender) {
        String usagePrefix = String.format("  /%s %s ", CommandConst.MAIN_COMMAND, CommandConst.COMMAND_CONFIG);
        String descPrefix = "    ";
        sender.sendMessage(DecolationConst.GREEN + "Usage:");
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_CONFIG_SHOW));
        sender.sendMessage(String.format("%sConfigの設定値を表示", descPrefix));
        sender.sendMessage(String.format("%s%s <name> <value>"
                ,usagePrefix, CommandConst.COMMAND_CONFIG_SET));
        sender.sendMessage(String.format("%snameの値をvalueに更新", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_CONFIG_RELOAD));
        sender.sendMessage(String.format("%sConfigを初期化", descPrefix));
    }
}
