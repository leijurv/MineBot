/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import minebot.pathfinding.GoalBlock;
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
    static BlockPos goal = null;
    public static String therewasachatmessage(String message) {
        Minecraft mc = Minecraft.theMinecraft;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        System.out.println("MSG: " + message);
        if (message.equals("look")) {
            lookAtBlock(new BlockPos(0, 0, 0));
            return null;
        }
        if (message.equals("st")) {
            System.out.println(theWorld.getBlockState(playerFeet).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 1, thePlayer.posZ)).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 2, thePlayer.posZ)).getBlock());
        }
        if (message.equals("lac")) {
            BlockPos pos = closestBlock();
            lookAtBlock(pos);
            return "" + pos;
        }
        if (message.equals("setgoal")) {
            goal = playerFeet;
            return "Set goal to " + playerFeet;
        }
        if (message.equals("path")) {
            new Thread() {
                public void run() {
                    PathFinder pf = new PathFinder(playerFeet, new GoalBlock(goal));
                    pf.calculatePath();
                    GuiScreen.sendChatMessage("Finished finding a path from " + playerFeet + " to " + goal, true);
                }
            }.start();
            return "Starting to search for path from " + playerFeet + " to " + goal;
        }
        return message;
    }
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
    public static boolean shouldIBeGoingForward() {
        return false;
    }
    public static void lookAtBlock(BlockPos p) {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
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
        double z = p.getZ() + zDiff;
        double y = p.getY() + yolo;
        double yDiff = (thePlayer.posY + 1.62) - y;
        double yaw = Math.atan2(thePlayer.posX - x, -thePlayer.posZ + z);
        double dist = Math.sqrt((thePlayer.posX - x) * (thePlayer.posX - x) + (-thePlayer.posZ + z) * (-thePlayer.posZ + z));
        double pitch = Math.atan2(yDiff, dist);
        thePlayer.rotationYaw = (float) (yaw * 180 / Math.PI);
        thePlayer.rotationPitch = (float) (pitch * 180 / Math.PI);
    }
    public static BlockPos whatAreYouLookingAt() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return mc.objectMouseOver.getBlockPos();
        }
        return null;
    }
}
