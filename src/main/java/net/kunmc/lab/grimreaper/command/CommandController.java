package net.kunmc.lab.grimreaper.command;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.common.DecolationConst;
import net.kunmc.lab.grimreaper.common.MessageUtil;
import net.kunmc.lab.grimreaper.game.GameController;
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

public class CommandController implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions.add(CommandConst.COMMAND_ASSIGN_MODE_ON);
            completions.add(CommandConst.COMMAND_RANDOM_MODE_ON);
            completions.add(CommandConst.COMMAND_CONFIG);
            completions.add(CommandConst.COMMAND_CONFIG_RELOAD);
            completions.add(CommandConst.COMMAND_CONFIG_SET);
        } else if (args.length > 2 && args[0].equals(CommandConst.COMMAND_ASSIGN_MODE_ON)) {
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        } else if (args.length == 2 && args[0].equals(CommandConst.COMMAND_CONFIG_SET)) {
            // TODO: 最終的に設定可能なパラメータを入れる
        }
        return completions;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String commandName = command.getName();

        if (commandName.equals(CommandConst.COMMAND_CONFIG)) {
            if (!GameController.runningMode.equals(GameController.GameMode.MODE_NEUTRAL)) {
                sender.sendMessage(DecolationConst.RED + "gr-offコマンドで実行中のモードを終了してからリロードしてください");
            } else {
                Config.loadConfig(true);
                sender.sendMessage(DecolationConst.GREEN + "コンフィグファイルをリロードしました");
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("このコマンドはゲーム内からのみ実行できます");
            return true;
        }

        switch (commandName) {
            case CommandConst.COMMAND_ASSIGN_MODE_ON:
                runModeAssign(sender, args);
                break;
            case CommandConst.COMMAND_RANDOM_MODE_ON:
                runModeRandom(sender, args);
                break;
            case CommandConst.COMMAND_MODE_OFF:
                runModeOff();
                break;
        }
        return true;
    }

    private static void runModeAssign(CommandSender sender, String[] args) {
        /*
         * ex1. gr-assing player1
         * ex2. gr-assing player1 player2
         */

        if (args.length < 1) {
            sender.sendMessage(DecolationConst.RED + MessageUtil.ERROR_MSG_LACK_ARGS);
            return;
        }

        for (String arg : args) {
            if (Bukkit.selectEntities(sender, arg).isEmpty()) {
                sender.sendMessage(DecolationConst.RED + MessageUtil.ERROR_MSG_PLAYER_NOT_FOUND);
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

        Bukkit.broadcastMessage(DecolationConst.RED + MessageUtil.MSG_LINE);
        Bukkit.broadcastMessage(DecolationConst.RED + "死神プラグイン指定モードを開始しました");
        for (String arg : args) {
            Bukkit.broadcastMessage(DecolationConst.RED + arg);
        }
        Bukkit.broadcastMessage(DecolationConst.RED + "を見ないでください");
        Bukkit.broadcastMessage(DecolationConst.RED + MessageUtil.MSG_LINE);
    }


    private static void runModeRandom(CommandSender sender, String[] args) {
        /*
         * ex1. gr-random
         */
        ArrayList<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.size() < Config.grimReaperNum){
            sender.sendMessage(DecolationConst.RED + "死神の数をオンラインプレイヤーの数以下にして下さい");
            return;
        }

        if (GameController.runningMode.equals(GameController.GameMode.MODE_RANDOM)) {
            sender.sendMessage("すでに実行中です");
            return;
        }

        GameController.GrimReapers = Arrays.stream(args)
                .flatMap(arg -> Bukkit.selectEntities(sender, arg).stream())
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());

        GameController.controller(GameController.GameMode.MODE_RANDOM);

        Bukkit.broadcastMessage(DecolationConst.RED + MessageUtil.MSG_LINE);
        Bukkit.broadcastMessage(DecolationConst.RED + "死神プラグインランダムモードを開始しました");
        Bukkit.broadcastMessage(DecolationConst.RED + Config.procInterval + "ごとに死神が変わります");
        for (String arg : args) {
            Bukkit.broadcastMessage(DecolationConst.RED + arg);
        }
        Bukkit.broadcastMessage(DecolationConst.RED + "を見ないでください");
        Bukkit.broadcastMessage(DecolationConst.RED + Config.procInterval + "ごとに死神は変わります");
        Bukkit.broadcastMessage(DecolationConst.RED + MessageUtil.MSG_LINE);
    }

    private static void runModeOff() {
        GameController.controller(GameController.GameMode.MODE_NEUTRAL);
        Bukkit.broadcastMessage(DecolationConst.YELLOW + "実行中のモードが終了しました");
    }
}