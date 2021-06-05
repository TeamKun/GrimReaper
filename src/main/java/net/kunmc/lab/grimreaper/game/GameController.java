package net.kunmc.lab.grimreaper.game;

import net.kunmc.lab.grimreaper.Config;
import net.kunmc.lab.grimreaper.gameprocess.Frustum;
import org.bukkit.entity.Player;

import java.util.*;

public class GameController {
    // 動作中のモード保持
    public static GameMode runningMode;
    // 死神の対象保持
    public static List<Player> GrimReapers = new ArrayList<Player>();
    // 視錐台計算オブジェクト
    public static Frustum frustum;
    // ゲーム内で管理するPlayerの状態保持
    public static final Map<UUID, PlayerState> players = new HashMap<>();
    // ゲーム内の時間を管理
    public static long gameTimerTick = 0L;

    public static void controller(GameMode runningMode) {
        // モードを設定
        GameController.runningMode = runningMode;
        // タイマー初期化
        gameTimerTick = 0L;

        switch (runningMode) {
            case MODE_ASSIGN:
                GameController.createFrustum();
            case MODE_RANDOM:
                GameController.createFrustum();
                break;
            case MODE_NEUTRAL:
                frustum = null;
                break;
        }
    }

    public static enum GameMode {
        // ゲーム外の状態
        MODE_NEUTRAL,
        // 死神指定
        MODE_ASSIGN,
        // 死神ランダム指定
        MODE_RANDOM
    }

    public static void createFrustum() {
        // TODO: nearPlaneは要調整
        frustum = new Frustum (Config.fov,
                Config.aspectRatio,
                '3',
                Config.farClipDistance);
    }

    public static void incrementTimer(long incrementTime) {
        gameTimerTick += incrementTime;
    }
}
