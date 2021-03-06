package net.kunmc.lab.grimreaper.command;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.common.DecolationConst;
import net.kunmc.lab.grimreaper.common.MessageConst;
import net.kunmc.lab.grimreaper.game.GameController;
import net.kunmc.lab.grimreaper.game.TaskManager;
import net.kunmc.lab.grimreaper.gameprocess.GameProcess;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CommandController implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<String>();
        if (args.length == 1) {
            completions.add(CommandConst.COMMAND_ASSIGN_MODE_ON);
            completions.add(CommandConst.COMMAND_RANDOM_MODE_ON);
            completions.add(CommandConst.COMMAND_CONFIG);
            completions.add(CommandConst.COMMAND_MODE_OFF);
            completions = completions.stream().filter(e -> e.startsWith(args[0])).collect(Collectors.toList());
        } else if (args.length > 1 && args[0].equals(CommandConst.COMMAND_ASSIGN_MODE_ON)) {
            completions.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).filter(e -> e.startsWith(args[args.length-1])).collect(Collectors.toList()));
        } else if (args.length == 2 && args[0].equals(CommandConst.COMMAND_CONFIG)) {
            completions.add(CommandConst.COMMAND_CONFIG_SHOW);
            completions.add(CommandConst.COMMAND_CONFIG_SET);
            completions.add(CommandConst.COMMAND_CONFIG_RELOAD);
            completions = completions.stream().filter(e -> e.startsWith(args[1])).collect(Collectors.toList());
        } else if (args.length == 3 && args[1].equals(CommandConst.COMMAND_CONFIG_SET)){
            completions.add(CommandConst.COMMAND_CONFIG_KILL_TICK_INTERVAL);
            completions.add(CommandConst.COMMAND_CONFIG_F0V);
            completions.add(CommandConst.COMMAND_CONFIG_ASPECT_RATIO_WIDE);
            completions.add(CommandConst.COMMAND_CONFIG_ASPECT_RATIO_HEIGHT);
            completions.add(CommandConst.COMMAND_CONFIG_FAR_CLIP_DISTANCE);
            completions.add(CommandConst.COMMAND_CONFIG_GRIM_REAPER_NUM);
            completions.add(CommandConst.COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL);
            completions = completions.stream().filter(e -> e.startsWith(args[2])).collect(Collectors.toList());
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
            sender.sendMessage("???????????????????????????????????????????????????????????????");
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
                sender.sendMessage(DecolationConst.RED + "???????????????????????????");
                sendUsage(sender);
        }
        return true;
    }

    private static void runModeAssign(CommandSender sender, String[] args) {
        if (GameController.runningMode.equals(GameController.GameMode.MODE_ASSIGN)) {
            sender.sendMessage("????????????????????????");
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(DecolationConst.RED + MessageConst.ERROR_MSG_LACK_ARGS);
            return;
        }

        // ???????????????
        Set set = new HashSet();
        for (String arg : args) {
            if (Bukkit.selectEntities(sender, arg).isEmpty()) {
                sender.sendMessage(DecolationConst.RED + MessageConst.ERROR_MSG_PLAYER_NOT_FOUND);
                return;
            }
            if(!set.add(arg)) {
                sender.sendMessage(DecolationConst.RED + MessageConst.ERROR_MSG_DUPLICATE_PLAYER);
                return;
            }
        }
        GameController.controller(GameController.GameMode.MODE_ASSIGN);
        TaskManager.runKillGrimReaperTask();

        GameProcess.updateGrimReaper(true, GameController.GameMode.MODE_ASSIGN, sender, args);
    }


    private static void runModeRandom(CommandSender sender, String[] args) {
        ArrayList<Player> onlinePlayers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.size() < Config.grimReaperNum){
            sender.sendMessage(DecolationConst.RED + MessageConst.ERROR_MSG_GRIM_REAPER_EXCEED_PLAYER);
            return;
        }

        if (GameController.runningMode.equals(GameController.GameMode.MODE_RANDOM)) {
            sender.sendMessage("????????????????????????");
            return;
        }

        GameController.controller(GameController.GameMode.MODE_RANDOM);
        TaskManager.runKillGrimReaperTask();
        TaskManager.runUpdateGrimReaperTask(true);
    }

    private static void runModeOff() {
        GameController.controller(GameController.GameMode.MODE_NEUTRAL);
        Bukkit.broadcastMessage(DecolationConst.YELLOW + "??????????????????????????????????????????");
    }

    private static void runConfig(CommandSender sender, String[] args) {
        if (args.length == 0){
            sendUsage(sender);
        }
        if (args.length == 1 && args[0].equals(CommandConst.COMMAND_CONFIG_SHOW)) {
            sender.sendMessage(DecolationConst.GREEN + "???????????????:");
            sender.sendMessage(String.format("  %s: %d", CommandConst.COMMAND_CONFIG_KILL_TICK_INTERVAL, Config.killProcessTickInterval));
            sender.sendMessage(String.format("  %s: %s", CommandConst.COMMAND_CONFIG_F0V, Config.fov));
            sender.sendMessage(String.format("  %s: %.1f", CommandConst.COMMAND_CONFIG_ASPECT_RATIO_WIDE, Config.aspectRatioWide));
            sender.sendMessage(String.format("  %s: %.1f", CommandConst.COMMAND_CONFIG_ASPECT_RATIO_HEIGHT, Config.aspectRatioHeight));
            sender.sendMessage(String.format("  %s: %.1f", CommandConst.COMMAND_CONFIG_FAR_CLIP_DISTANCE, Config.farClipDistance));
            sender.sendMessage(String.format("  %s: %d", CommandConst.COMMAND_CONFIG_GRIM_REAPER_NUM, Config.grimReaperNum));
            sender.sendMessage(String.format("  %s: %d", CommandConst.COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL, Config.grimReaperUpdateTickInterval));
        } else if (args.length == 1 && args[0].equals(CommandConst.COMMAND_CONFIG_RELOAD)) {
            Config.loadConfig(true);
            sender.sendMessage(DecolationConst.GREEN + "??????????????????????????????????????????????????????");
            TaskManager.runKillGrimReaperTask();
            TaskManager.runUpdateGrimReaperTask(false);
        } else if (args.length == 3 && args[0].equals(CommandConst.COMMAND_CONFIG_SET)) {
            switch (args[1]){
                case CommandConst.COMMAND_CONFIG_KILL_TICK_INTERVAL:
                    int killTickInterval = Integer.parseInt(args[2]);
                    if (killTickInterval < 10) {
                        sender.sendMessage(DecolationConst.RED + "tick?????????10???????????????????????????");
                        return;
                    }
                    Config.killProcessTickInterval = Integer.parseInt(args[2]);
                    TaskManager.runKillGrimReaperTask();
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
                        sender.sendMessage(DecolationConst.RED + "????????????Player???????????????????????????");
                        return;
                    }
                    Config.grimReaperNum = num;
                    break;
                case CommandConst.COMMAND_CONFIG_GRIM_REAPER_RANDOM_UODATE_TICK_INTERVAL:
                    Config.grimReaperUpdateTickInterval = Integer.parseInt(args[2]);
                    TaskManager.runUpdateGrimReaperTask(false);
                    break;
                default:
                    sender.sendMessage(DecolationConst.RED + "????????????????????????????????????");
                    return;
            }
            sender.sendMessage(String.format("%s%s?????????%s?????????????????????", DecolationConst.GREEN, args[1], args[2]));
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
        sender.sendMessage(String.format("%s????????????player???????????????????????????(??????????????????)", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_RANDOM_MODE_ON));
        sender.sendMessage(String.format("%s???????????????player???????????????????????????", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_MODE_OFF));
        sender.sendMessage(String.format("%s???????????????????????????????????????", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_CONFIG));
        sender.sendMessage(String.format("%sConfig???Usage?????????", descPrefix));
    }

    private static void sendConfigUsage(CommandSender sender) {
        String usagePrefix = String.format("  /%s %s ", CommandConst.MAIN_COMMAND, CommandConst.COMMAND_CONFIG);
        String descPrefix = "    ";
        sender.sendMessage(DecolationConst.GREEN + "Usage:");
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_CONFIG_SHOW));
        sender.sendMessage(String.format("%sConfig?????????????????????", descPrefix));
        sender.sendMessage(String.format("%s%s <name> <value>"
                ,usagePrefix, CommandConst.COMMAND_CONFIG_SET));
        sender.sendMessage(String.format("%sname?????????value?????????", descPrefix));
        sender.sendMessage(String.format("%s%s"
                ,usagePrefix, CommandConst.COMMAND_CONFIG_RELOAD));
        sender.sendMessage(String.format("%sConfig????????????", descPrefix));
    }
}
