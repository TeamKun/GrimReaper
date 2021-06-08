package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.gameprocess.Frustum;
import org.bukkit.entity.Player;

import java.util.*;

public class GameController {
    // 動作中のモード保持
    public static GameMode runningMode = GameMode.MODE_NEUTRAL;
    // 死神の対象保持
    public static List<Player> GrimReapers = new ArrayList<Player>();
    // 視錐台計算オブジェクト
    public static Frustum frustum;
    // ゲーム内で管理するPlayerの状態保持
    public static final Map<UUID, Player> players = new HashMap<>();
    // ゲーム内時間
    public static int timer = 1;

    public static void controller(GameMode runningMode) {
        // モードを設定
        GameController.runningMode = runningMode;

        switch (runningMode) {
            case MODE_ASSIGN:
                GameController.timer=1;
                GameController.createFrustum();
            case MODE_RANDOM:
                GameController.timer=1;
                GameController.createFrustum();
                break;
            case MODE_NEUTRAL:
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