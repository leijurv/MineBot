/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.io.IOException;
import minebot.pathfinding.Action;
import minebot.pathfinding.GoalBlock;
import minebot.pathfinding.Path;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import minebot.pathfinding.PathFinder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 *
 * @author leijurv
 */
public class MineBot {
    static boolean looking = false;
    static boolean lookingPitch = false;
    public static void main(String[] args) throws IOException, InterruptedException {
        String s = Autorun.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(5) + "../../autorun/runmc.command";
        if (s.contains("jar")) {
            Autorun.start(args);
            return;
        }
        Autorun.runprocess("/usr/local/bin/ant jar");
        Autorun.runprocess("java -Djava.library.path=jars/versions/1.8.8/1.8.8-natives/ -jar dist/MineBot.jar");
    }
    /**
     * Called by minecraft.java
     */
    public static void onTick() {
        /////////*System.out.println(isLeftClick + "," + pressTime);*/
        if (Minecraft.theMinecraft.theWorld == null || Minecraft.theMinecraft.thePlayer == null) {
            return;
        }
        if (Minecraft.theMinecraft.currentScreen != null) {
            wasScreen = true;
        } else {
            if (isLeftClick) {
                pressTime = 5;
            }
            if (wasScreen) {
                wasScreen = false;
                pressTime = -10;
            }
        }
        looking = false;
        lookingPitch = false;
        if (currentPath != null) {
            //System.out.println("On a path");
            if (currentPath.tick()) {
                if (currentPath != null && currentPath.failed) {
                    clear();
                    GuiScreen.sendChatMessage("Recalculating because path failed", true);
                    findPathInNewThread();
                } else {
                    clear();
                }
                currentPath = null;
                System.out.println("Path done");
            }
        }
        if (looking) {
            float yawDistance = Minecraft.theMinecraft.thePlayer.rotationYaw - desiredYaw;
            //System.out.println();
            //System.out.println();
            //System.out.println(yawDistance);
            if (yawDistance > 180) {
                yawDistance = yawDistance - 360;
            } else {
                if (yawDistance < -180) {
                    yawDistance = yawDistance + 360;
                }
            }
            //System.out.println(yawDistance);
            if (Math.abs(yawDistance) > (360 / 20)) {
                yawDistance = Math.signum(yawDistance) * 360 / 20;
            }
            //System.out.println(yawDistance);
            Minecraft.theMinecraft.thePlayer.rotationYaw -= yawDistance;
        }
        if (lookingPitch) {
            //System.out.println();
            float pitchDistance = Minecraft.theMinecraft.thePlayer.rotationPitch - desiredPitch;
            //System.out.println(pitchDistance);
            if (pitchDistance > 180) {
                pitchDistance = pitchDistance - 360;
            } else {
                if (pitchDistance < -180) {
                    pitchDistance = pitchDistance + 360;
                }
            }
            //System.out.println(pitchDistance);
            if (Math.abs(pitchDistance) > (360 / 20)) {
                pitchDistance = Math.signum(pitchDistance) * 360 / 20;
            }
            //System.out.println(pitchDistance);
            Minecraft.theMinecraft.thePlayer.rotationPitch -= pitchDistance;
            //System.out.println();
            //System.out.println();
        }
    }
    public static boolean wasScreen = false;
    static Path currentPath = null;
    static BlockPos goal = null;
    public static int pressTime = 0;
    public static boolean isLeftClick = false;
    public static boolean jumping = false;
    public static boolean forward = false;
    public static boolean backward = false;
    public static boolean left = false;
    public static boolean right = false;
    public static boolean sneak = false;
    /**
     * Do not question the logic. Called by Minecraft.java
     *
     * @return
     */
    public static boolean getIsPressed() {
        return isLeftClick && Minecraft.theMinecraft.currentScreen == null && pressTime >= -1;
    }
    /**
     * Do not question the logic. Called by Minecraft.java
     *
     * @return
     */
    public static boolean isPressed() {
        if (pressTime <= 0) {
            return false;
        } else {
            --pressTime;
            return true;
        }
    }
    /**
     * Called by our code
     */
    public static void letGoOfLeftClick() {
        pressTime = 0;
        isLeftClick = false;
    }
    public static void clear() {
        currentPath = null;
        letGoOfLeftClick();
        jumping = false;
        forward = false;
        left = false;
        right = false;
        backward = false;
        sneak = false;
    }
    /**
     * Called by GuiScreen.java
     *
     * @param message
     * @return
     */
    public static String therewasachatmessage(String message) {
        Minecraft mc = Minecraft.theMinecraft;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        System.out.println("MSG: " + message);
        String text = message;
        if (text.charAt(0) == '/') {
            text = text.substring(1);
        }
        if (text.equals("look")) {
            lookAtBlock(new BlockPos(0, 0, 0), true);
            return null;
        }
        if (text.equals("cancel")) {
            clear();
            return "unset";
        }
        if (text.equals("st")) {
            System.out.println(theWorld.getBlockState(playerFeet).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 1, thePlayer.posZ)).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 2, thePlayer.posZ)).getBlock());
        }
        if (text.equals("lac")) {
            BlockPos pos = closestBlock();
            lookAtBlock(pos, true);
            return pos.toString();
        }
        if (text.equals("setgoal")) {
            goal = playerFeet;
            return "Set goal to " + playerFeet;
        }
        if (text.startsWith("path")) {
            //boolean stone = message.contains("stone");
            findPathInNewThread();
            return null;
        }
        if (text.startsWith("hardness")) {
            BlockPos bp = MineBot.whatAreYouLookingAt();
            return bp == null ? "0" : (1 / theWorld.getBlockState(bp).getBlock().getPlayerRelativeBlockHardness(Minecraft.theMinecraft.thePlayer, Minecraft.theMinecraft.theWorld, MineBot.whatAreYouLookingAt())) + "";
        }
        if (text.startsWith("info")) {
            BlockPos bp = MineBot.whatAreYouLookingAt();
            Block block = theWorld.getBlockState(bp).getBlock();
            return block + " can walk on: " + Action.canWalkOn(bp) + " can walk through: " + Action.canWalkThrough(bp) + " is full block: " + block.isFullBlock() + " is full cube: " + block.isFullCube();
        }
        return message;
    }
    public static void findPathInNewThread() {
        new Thread() {
            @Override
            public void run() {
                findPath();
            }
        }.start();
    }
    public static void findPath() {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        GuiScreen.sendChatMessage("Starting to search for path from " + playerFeet + " to " + goal, true);
        PathFinder pf = new PathFinder(playerFeet, new GoalBlock(goal));
        Path path = pf.calculatePath();
        if (path == null) {
            GuiScreen.sendChatMessage("Unable to find path from " + playerFeet + " to " + goal, true);
            return;
        }
        GuiScreen.sendChatMessage("Finished finding a path from " + playerFeet + " to " + goal, true);
        /* if (stone) {
         path.showPathInStone();
         return;
         }*/
        currentPath = path;
    }
    /**
     * Give a block that's sorta close to the player, at foot level
     *
     * @return
     */
    public static BlockPos closestBlock() {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        Block air = Block.getBlockById(0);
        for (int x = (int) (thePlayer.posX - 5); x <= thePlayer.posX + 5; x++) {
            for (int z = (int) (thePlayer.posZ - 5); z <= thePlayer.posZ + 5; z++) {
                BlockPos pos = new BlockPos(x, thePlayer.posY, z);
                Block b = theWorld.getBlockState(pos).getBlock();
                if (!b.equals(air)) {
                    return pos;
                }
            }
        }
        return null;
    }
    static float desiredYaw;
    static float desiredPitch;
    /**
     * Called by our code
     *
     * @param p
     * @param alsoDoPitch
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
    public static boolean lookAtCoords(double x, double y, double z, boolean alsoDoPitch) {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        double yDiff = (thePlayer.posY + 1.62) - y;
        double yaw = Math.atan2(thePlayer.posX - x, -thePlayer.posZ + z);
        double dist = Math.sqrt((thePlayer.posX - x) * (thePlayer.posX - x) + (-thePlayer.posZ + z) * (-thePlayer.posZ + z));
        double pitch = Math.atan2(yDiff, dist);
        desiredYaw = (float) (yaw * 180 / Math.PI);
        looking = true;
        float yawDist = Math.abs(desiredYaw - thePlayer.rotationYaw);
        boolean withinRange = yawDist < 7 || yawDist > 360 - 7;
        if (alsoDoPitch) {
            lookingPitch = true;
            desiredPitch = (float) (pitch * 180 / Math.PI);
            float pitchDist = Math.abs(desiredPitch - thePlayer.rotationPitch);
            withinRange = withinRange && (pitchDist < 7 || pitchDist > 360 - 7);
        }
        return withinRange;
    }
    /**
     * What block is the player looking at
     *
     * @return
     */
    public static BlockPos whatAreYouLookingAt() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return mc.objectMouseOver.getBlockPos();
        }
        return null;
    }
}
