/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.Random;
import minebot.pathfinding.goals.GoalXZ;
import minebot.util.Manager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 *
 * @author leijurv
 */
public class LookManager extends Manager{
    static final float MAX_YAW_CHANGE_PER_TICK = 360 / 20;
    static final float MAX_PITCH_CHANGE_PER_TICK = 360 / 20;
    static float previousYaw = 0;
    static float previousPitch = 0;
    static float desiredNextYaw = 0;
    static float desiredNextPitch = 0;
    /**
     * The desired yaw, as set by whatever action is happening. Remember to also
     * set lookingYaw to true if you really want the yaw to change
     *
     */
    static float desiredYaw;
    /**
     * The desired pitch, as set by whatever action is happening. Remember to
     * also set lookingPitch to true if you really want the yaw to change
     *
     */
    static float desiredPitch;
    /**
     * Set to true if the action wants the player's yaw to be moved towards
     * desiredYaw
     */
    static boolean lookingYaw = false;
    /**
     * Set to true if the action wants the player's pitch to be moved towards
     * desiredPitch
     */
    static boolean lookingPitch = false;
    public static void frame(float partialTicks) {
        //System.out.println("Part: " + partialTicks);
        if (Minecraft.theMinecraft == null || Minecraft.theMinecraft.thePlayer == null) {
            return;
        }
        if (lookingPitch) {
            Minecraft.theMinecraft.thePlayer.rotationPitch = (desiredNextPitch - previousPitch) * partialTicks + previousPitch;
        }
        if (lookingYaw) {
            Minecraft.theMinecraft.thePlayer.rotationYaw = (desiredNextYaw - previousYaw) * partialTicks + previousYaw;
        }
    }
    /**
     * Called by our code in order to look in the direction of the center of a
     * block
     *
     * @param p the position to look at
     * @param alsoDoPitch whether to set desired pitch or just yaw
     * @return is the actual player yaw (and actual player pitch, if alsoDoPitch
     * is true) within ANGLE_THRESHOLD (currently 7°) of looking straight at
     * this block?
     */
    public static boolean lookAtBlock(BlockPos p, boolean alsoDoPitch) {
        Block b = Minecraft.theMinecraft.theWorld.getBlockState(p).getBlock();
        double xDiff = (b.getBlockBoundsMinX() + b.getBlockBoundsMaxX()) / 2;
        double yolo = (b.getBlockBoundsMinY() + b.getBlockBoundsMaxY()) / 2;
        double zDiff = (b.getBlockBoundsMinZ() + b.getBlockBoundsMaxZ()) / 2;
        /*System.out.println("min X: " + b.getBlockBoundsMinX());
         System.out.println("max X: " + b.getBlockBoundsMaxX());
         System.out.println("xdiff: " + xDiff);
         System.out.println("min Y: " + b.getBlockBoundsMinY());
         System.out.println("max Y: " + b.getBlockBoundsMaxY());
         System.out.println("ydiff: " + yolo);
         System.out.println("min Z: " + b.getBlockBoundsMinZ());
         System.out.println("max Z: " + b.getBlockBoundsMaxZ());
         System.out.println("zdiff: " + zDiff);*/
        double x = p.getX() + xDiff;
        double y = p.getY() + yolo;
        double z = p.getZ() + zDiff;
        //System.out.println("Trying to look at " + p + " actually looking at " + whatAreYouLookingAt() + " xyz is " + x + "," + y + "," + z);
        return lookAtCoords(x, y, z, alsoDoPitch);
    }
    /**
     * The threshold for how close it tries to get to looking straight at things
     */
    public static final float ANGLE_THRESHOLD = 7;
    public static boolean couldIReach(BlockPos pos) {
        float[] pitchAndYaw = pitchAndYaw(pos);
        float yaw = pitchAndYaw[0];
        float pitch = pitchAndYaw[1];
        double blockReachDistance = (double) Minecraft.theMinecraft.playerController.getBlockReachDistance();
        Vec3 vec3 = Minecraft.theMinecraft.thePlayer.getPositionEyes(1.0F);
        Vec3 vec31 = getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        MovingObjectPosition blah = Minecraft.theMinecraft.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
        System.out.println(blah);
        return blah != null && blah.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && blah.getBlockPos().equals(pos);
    }
    public static Vec3 getVectorForRotation(float pitch, float yaw) {//shamelessly copied from Entity.java
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }
    public static GoalXZ fromAngleAndDirection(double distance) {
        double theta = ((double) Minecraft.theMinecraft.thePlayer.rotationYaw) * Math.PI / 180D;
        double x = Minecraft.theMinecraft.thePlayer.posX - Math.sin(theta) * distance;
        double z = Minecraft.theMinecraft.thePlayer.posZ + Math.cos(theta) * distance;
        return new GoalXZ((int) x, (int) z);
    }
    static double SPEED = 1000;
    public static float[] getRandom() {
        long now = (long) Math.ceil(((double) System.currentTimeMillis()) / SPEED);
        now *= SPEED;
        long prev = now - (long) SPEED;
        float frac = (System.currentTimeMillis() - prev) / ((float) SPEED);
        Random prevR = new Random(prev);
        Random nowR = new Random(now);
        float prevFirst = prevR.nextFloat() * 10 - 5;
        float prevSecond = prevR.nextFloat() * 10 - 5;
        float nowFirst = nowR.nextFloat() * 10 - 5;
        float nowSecond = nowR.nextFloat() * 10 - 5;
        float first = prevFirst + frac * (nowFirst - prevFirst);
        float second = prevSecond + frac * (nowSecond - prevSecond);
        return new float[]{first, second};
    }
    public static float[] pitchAndYaw(BlockPos p) {
        Block b = Minecraft.theMinecraft.theWorld.getBlockState(p).getBlock();
        double xDiff = (b.getBlockBoundsMinX() + b.getBlockBoundsMaxX()) / 2;
        double yolo = (b.getBlockBoundsMinY() + b.getBlockBoundsMaxY()) / 2;
        double zDiff = (b.getBlockBoundsMinZ() + b.getBlockBoundsMaxZ()) / 2;
        double x = p.getX() + xDiff;
        double y = p.getY() + yolo;
        double z = p.getZ() + zDiff;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        double yDiff = (thePlayer.posY + 1.62) - y;
        double yaw = Math.atan2(thePlayer.posX - x, -thePlayer.posZ + z);
        double dist = Math.sqrt((thePlayer.posX - x) * (thePlayer.posX - x) + (-thePlayer.posZ + z) * (-thePlayer.posZ + z));
        double pitch = Math.atan2(yDiff, dist);
        return new float[]{(float) (yaw * 180 / Math.PI), (float) (pitch * 180 / Math.PI)};
    }
    /**
     * Look at coordinates
     *
     * @param x
     * @param y
     * @param z
     * @param alsoDoPitch also adjust the pitch? if false, y is ignored
     * @return is the actual player yaw (and actual player pitch, if alsoDoPitch
     * is true) within ANGLE_THRESHOLD (currently 7°) of looking straight at
     * these coordinates?
     */
    public static boolean lookAtCoords(double x, double y, double z, boolean alsoDoPitch) {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        double yDiff = (thePlayer.posY + 1.62) - y;
        double yaw = Math.atan2(thePlayer.posX - x, -thePlayer.posZ + z);
        double dist = Math.sqrt((thePlayer.posX - x) * (thePlayer.posX - x) + (-thePlayer.posZ + z) * (-thePlayer.posZ + z));
        double pitch = Math.atan2(yDiff, dist);
        desiredYaw = (float) (yaw * 180 / Math.PI);
        lookingYaw = true;
        float yawDist = Math.abs(desiredYaw - thePlayer.rotationYaw);
        boolean withinRange = yawDist < ANGLE_THRESHOLD || yawDist > 360 - ANGLE_THRESHOLD;
        if (alsoDoPitch) {
            lookingPitch = true;
            desiredPitch = (float) (pitch * 180 / Math.PI);
            float pitchDist = Math.abs(desiredPitch - thePlayer.rotationPitch);
            withinRange = withinRange && (pitchDist < ANGLE_THRESHOLD || pitchDist > 360 - ANGLE_THRESHOLD);
        }
        return withinRange;
    }
    @Override
    public void onTickPre() {
        if (lookingYaw) {
            Minecraft.theMinecraft.thePlayer.rotationYaw = desiredNextYaw;
        }
        if (lookingPitch) {
            Minecraft.theMinecraft.thePlayer.rotationPitch = desiredNextPitch;
        }
        lookingYaw = false;
        lookingPitch = false;
    }
    public static void beSketchy() {
        desiredYaw += (System.currentTimeMillis() % 15000) / 15000F * 360F;
    }
    @Override
    public void onTickPost() {
        desiredYaw += getRandom()[0];
        desiredPitch += getRandom()[1];
        if (desiredPitch > 90) {
            desiredPitch = 90;
        }
        if (desiredPitch < -90) {
            desiredPitch = -90;
        }
        if (lookingYaw) {
            previousYaw = Minecraft.theMinecraft.thePlayer.rotationYaw;
            desiredYaw += 360;
            desiredYaw %= 360;
            float yawDistance = Minecraft.theMinecraft.thePlayer.rotationYaw - desiredYaw;
            if (yawDistance > 180) {
                yawDistance -= 360;
            } else if (yawDistance < -180) {
                yawDistance += 360;
            }
            if (Math.abs(yawDistance) > MAX_YAW_CHANGE_PER_TICK) {
                yawDistance = Math.signum(yawDistance) * MAX_YAW_CHANGE_PER_TICK;
            }
            desiredNextYaw = Minecraft.theMinecraft.thePlayer.rotationYaw - yawDistance;
        }
        if (lookingPitch) {
            previousPitch = Minecraft.theMinecraft.thePlayer.rotationPitch;
            desiredPitch += 360;
            desiredPitch %= 360;
            float pitchDistance = Minecraft.theMinecraft.thePlayer.rotationPitch - desiredPitch;
            if (pitchDistance > 180) {
                pitchDistance -= 360;
            } else if (pitchDistance < -180) {
                pitchDistance += 360;
            }
            if (Math.abs(pitchDistance) > MAX_PITCH_CHANGE_PER_TICK) {
                pitchDistance = Math.signum(pitchDistance) * MAX_PITCH_CHANGE_PER_TICK;
            }
            desiredNextPitch = Minecraft.theMinecraft.thePlayer.rotationPitch - pitchDistance;
        }
    }

    @Override
    protected void onTick() {}

    @Override
    protected void onCancel() {}

    @Override
    protected void onStart() {}
    
    @Override
    protected boolean onEnabled(boolean enabled) {
        return true;
    }
}
