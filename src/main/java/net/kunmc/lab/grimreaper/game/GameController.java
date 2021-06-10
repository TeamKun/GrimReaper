package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.gameprocess.Frustum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GameController {
    // 動作中のモード保持
    public static GameMode runningMode = GameMode.MODE_NEUTRAL;
    // ゲーム内で管理する全Player保持
    public static Map<UUID, Player> players = new HashMap<>();
    // 死神の対象保持
    public static List<Player> grimReapers = new ArrayList<Player>();
    // 視錐台計算オブジェクト
    public static Frustum frustum;

    public static void controller(GameMode runningMode) {
        // モードを設定
        GameController.runningMode = runningMode;

        switch (runningMode) {
            case MODE_ASSIGN:
                players.clear();
                players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toMap(e -> e.getPlayerProfile().getId(), e -> e));
                grimReapers.clear();
                GameController.createFrustum();
            case MODE_RANDOM:
                players.clear();
                players = Bukkit.getOnlinePlayers().stream().collect(Collectors.toMap(e -> e.getPlayerProfile().getId(), e -> e));
                grimReapers.clear();
                GameController.createFrustum();
                break;
            case MODE_NEUTRAL:
                for (Player player : GameController.players.values()) {
                    player.setGlowing(false);
                }
                players.clear();
                grimReapers.clear();
                frustum = null;
                break;
        }
    }

    public enum GameMode {
        // ゲーム外の状態
        MODE_NEUTRAL,
        // 死神指定
        MODE_ASSIGN,
        // 死神ランダム指定
        MODE_RANDOM
    }

    public static void createFrustum() {
        frustum = new Frustum (Config.fov,
                Config.aspectRatio,
                1.0,
                Config.farClipDistance);
    }
}