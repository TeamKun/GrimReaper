package net.kunmc.lab.grimreaper.gameprocess;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static org.bukkit.Bukkit.getLogger;

public class Frustum {

    private Vector leftBottomNear;
    private Vector rightBottomNear;
    private Vector leftTopNear;
    private Vector rightTopNear;
    private Vector leftBottomFar;
    private Vector rightBottomFar;
    private Vector leftTopFar;
    private Vector rightTopFar;

    public Frustum(double fov, double aspectRatio, double nearPlane, double farPlane) {

        double yScale = 1.0f / (float) Math.tan(Math.toRadians(fov / 2.0));
        double xScale = yScale * aspectRatio;

        double nearX = nearPlane * xScale;
        double nearY = nearPlane * yScale;

        double farX = farPlane * xScale;
        double farY = farPlane * yScale;

        this.leftBottomNear = new Vector(-nearX, -nearY, nearPlane);
        this.rightBottomNear = new Vector(nearX, -nearY, nearPlane);
        this.leftTopNear = new Vector(-nearX, nearY, nearPlane);
        this.rightTopNear = new Vector(nearX, nearY, nearPlane);
        this.leftBottomFar = new Vector(-farX, -farY, farPlane);
        this.rightBottomFar = new Vector(farX, -farY, farPlane);
        this.leftTopFar = new Vector(-farX, farY, farPlane);
        this.rightTopFar = new Vector(farX, farY, farPlane);
    }

    private Frustum(Vector leftBottomNear,
                    Vector rightBottomNear,
                    Vector leftTopNear,
                    Vector rightTopNear,
                    Vector leftBottomFar,
                    Vector rightBottomFar,
                    Vector leftTopFar,
                    Vector rightTopFar) {

        this.leftBottomNear = leftBottomNear;
        this.rightBottomNear = rightBottomNear;
        this.leftTopNear = leftTopNear;
        this.rightTopNear = rightTopNear;

        this.leftBottomFar = leftBottomFar;
        this.rightBottomFar = rightBottomFar;
        this.leftTopFar = leftTopFar;
        this.rightTopFar = rightTopFar;
    }

    /**
     * このオブジェクトのクローンを作成する
     *
     * @return
     */
    public Frustum clone() {
        return new Frustum(this.leftBottomNear,
                this.rightBottomNear,
                this.leftTopNear,
                this.rightTopNear,
                this.leftBottomFar,
                this.rightBottomFar,
                this.leftTopFar,
                this.rightTopFar);
    }

    /**
     * ターゲットがプレイヤーの視界に含まれるか判定する
     *
     * @param viewer ビュアープレイヤー
     * @param target 　ターゲットプレイヤー
     * @return true:視界内 false:視界外
     */
    public boolean isInSight(Player viewer, Player target) {

        // 遮蔽物の判定
        if (existsShield(viewer, target)) {
            return false;
        }

        /** ターゲットの座標 */
        Vector targetPoint = target.getEyeLocation().toVector();
        //getLogger().info(targetPoint.toString());

        //getLogger().info(leftBottomFar.toString());
        //getLogger().info(rightBottomFar.toString());

        //checkInSight(targetPoint);

        // 視錐体台内外判定
        // 右面の判定
        if (-calcDot(rightBottomFar, rightTopFar, rightBottomNear, targetPoint) < 1) {
            return false;
        }
        // 左面の判定
        if (calcDot(leftBottomFar, leftTopFar, leftBottomNear, targetPoint) < 1) {
            return false;
        }
        // 上面の判定
        if (calcDot(leftTopFar, rightTopFar, leftTopNear, targetPoint) < 1) {
            return false;
        }
        // 下面の判定
        if (-calcDot(leftBottomFar, rightBottomFar, leftBottomNear, targetPoint) < 1) {
            return false;
        }

        // ニアプレーンの判定
        if (-calcDot(leftTopNear, leftBottomNear, rightBottomNear, targetPoint) < 1) {
            return false;
        }
        getLogger().info("BBBBB");
        // ファープレーンの判定
        if (calcDot(leftTopFar, leftBottomFar, rightBottomFar, targetPoint) < 1) {
            return false;
        }
        return true;
    }

    /**
     * プレイヤーの視野の範囲を取得する
     *
     * @param location 　視野を取得したいプレイヤーの位置情報
     * @return
     */
    public Frustum getFieldOfView(Location location) {

        /** プレイヤーの位置ベクトル */
        Vector playerLocation = location.toVector();
        /** プレイヤーのピッチ */
        double pitch = location.getPitch();
        /** プレイヤーのヨー */
        double yaw = location.getYaw();

        return rotatePitch(pitch).rotateYaw(yaw).translate(playerLocation);
    }

    /**
     * 平行移動する
     *
     * @param v
     * @return
     */
    private Frustum translate(Vector v) {
        return new Frustum(
                this.leftBottomNear.clone().add(v),
                this.rightBottomNear.clone().add(v),
                this.leftTopNear.clone().add(v),
                this.rightTopNear.clone().add(v),
                this.leftBottomFar.clone().add(v),
                this.rightBottomFar.clone().add(v),
                this.leftTopFar.clone().add(v),
                this.rightTopFar.clone().add(v));
    }

    /**
     * ヨーの回転を加える
     *
     * @param yaw
     * @return
     */
    private Frustum rotateYaw(double yaw) {
        return new Frustum(
                calcYawRotate(yaw, this.leftBottomNear),
                calcYawRotate(yaw, this.rightBottomNear),
                calcYawRotate(yaw, this.leftTopNear),
                calcYawRotate(yaw, this.rightTopNear),
                calcYawRotate(yaw, this.leftBottomFar),
                calcYawRotate(yaw, this.rightBottomFar),
                calcYawRotate(yaw, this.leftTopFar),
                calcYawRotate(yaw, this.rightTopFar));
    }

    /**
     * ピッチの回転を加える
     *
     * @param pitch
     * @return
     */
    private Frustum rotatePitch(double pitch) {

        return new Frustum(calcMatrix(pitch, this.leftBottomNear),
                calcMatrix(pitch, this.rightBottomNear),
                calcMatrix(pitch, this.leftTopNear),
                calcMatrix(pitch, this.rightTopNear),
                calcMatrix(pitch, this.leftBottomFar),
                calcMatrix(pitch, this.rightBottomFar),
                calcMatrix(pitch, this.leftTopFar),
                calcMatrix(pitch, this.rightTopFar));
    }

    /**
     * ヨー回転を計算する
     *
     * @param
     */
    private Vector calcYawRotate(double yaw, Vector v) {
        double rad = Math.toRadians(yaw);
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        return new Vector((cos * x - sin * z),
                y
                , sin * x + cos * z);
    }

    /**
     * ピッチ回転を計算する
     *
     * @param pitch
     * @param v
     * @return
     */
    private Vector calcMatrix(double pitch, Vector v) {
        double rad = Math.toRadians(pitch);
        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();
        return new Vector(x,
                -sin * z + cos * y,
                sin * y + cos * z);
    }

    /**
     * ２人のプレイヤー間における遮蔽物の有無を判定する
     *
     * @param player
     * @param target
     * @return true:あり　false:なし
     */
    private boolean existsShield(Player player, Player target) {
        /** ワールドオブジェクト */
        World world = player.getWorld();
        /** プレイヤーの座標 */
        Vector playerPoint = player.getEyeLocation().toVector();
        /** ターゲットの座標 */
        Vector targetPoint = target.getEyeLocation().toVector();
        /** プレイヤーとターゲット間の方向ベクトル */
        Vector direction = targetPoint.clone().subtract(playerPoint);
        /** プレイヤーとターゲット間の距離 */
        double distance = playerPoint.distance(targetPoint);

        // ゼロ除算回避
        if (direction.getX() == 0 && direction.getY() == 0 && direction.getZ() == 0) {
            direction.setX(1);
            direction.setY(1);
            direction.setZ(1);
        }

        // 遮蔽物の有無を判定
        if (world.rayTraceBlocks(player.getEyeLocation(), direction.normalize(), distance) != null) {
            return true;
        }
        return false;
    }

    /**
     * 法線ベクトルを算出する
     *
     * @param a
     * @param b
     * @param c
     * @param target
     * @return 法線ベクトル
     */
    private double calcDot(Vector a, Vector b, Vector c, Vector target) {
        Vector newA = a.clone();
        Vector newB = b.clone();
        Vector newC = c.clone();
        Vector newTarget = target.clone();

        Vector u = newA.subtract(newC);
        Vector v = newB.subtract(newC);
        Vector normal = v.crossProduct(u);
        Vector tc = newTarget.subtract(newC);

        return tc.dot(normal);
    }

    /*
     * 各種情報を出力するメソッド、デバック用
     */

    private void checkInSight(Vector targetPoint){
        getLogger().info("====== start ======");
        getLogger().info(leftBottomFar.toString());
        getLogger().info(rightBottomFar.toString());
        getLogger().info(leftTopFar.toString());
        getLogger().info(rightTopFar.toString());
        getLogger().info(targetPoint.toString());
        getLogger().info("====== end ======");
    }
}
