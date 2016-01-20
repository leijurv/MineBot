/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author leijurv
 */
public class MineBot {
    public static String therewasachatmessage(String message) {
        Minecraft mc = Minecraft.theMinecraft;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        System.out.println("MSG: " + message);
        if (message.equals("look")) {
            lookAtBlock(new BlockPos(0, 0, 0));
            return null;
        }
        if (message.equals("st")) {
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ)).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 1, thePlayer.posZ)).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 2, thePlayer.posZ)).getBlock());
        }
        if (message.equals("lac")) {
            Block air = Block.getBlockById(0);
            for (int x = (int) (thePlayer.posX - 5); x <= thePlayer.posX + 5; x++) {
                for (int z = (int) (thePlayer.posZ - 5); z <= thePlayer.posZ + 5; z++) {
                    BlockPos pos = new BlockPos(x, thePlayer.posY, z);
                    Block b = theWorld.getBlockState(pos).getBlock();
                    if (!b.equals(air)) {
                        lookAtBlock(pos);
                        return "" + b;
                    }
                }
            }
        }
        return message;
    }
    public static boolean shouldIBeGoingForward() {
        return false;
    }
    public static void lookAtBlock(BlockPos p) {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        Block b = Minecraft.theMinecraft.theWorld.getBlockState(p).getBlock();
        System.out.println("min X: " + b.getBlockBoundsMinX());
        System.out.println("max X: " + b.getBlockBoundsMaxX());
        System.out.println("min Y: " + b.getBlockBoundsMinY());
        System.out.println("max Y: " + b.getBlockBoundsMaxY());
        System.out.println("min Z: " + b.getBlockBoundsMinZ());
        System.out.println("max Z: " + b.getBlockBoundsMaxZ());
        double x = p.getX() + 0.5;
        double z = p.getZ() + 0.5;
        double y = p.getY() + 0.5;
        double yDiff = (thePlayer.posY + 1.62) - y;
        double yaw = Math.atan2(thePlayer.posX - x, -thePlayer.posZ + z);
        double dist = Math.sqrt((thePlayer.posX - x) * (thePlayer.posX - x) + (-thePlayer.posZ + z) * (-thePlayer.posZ + z));
        double pitch = Math.atan2(yDiff, dist);
        thePlayer.rotationYaw = (float) (yaw * 180 / Math.PI);
        thePlayer.rotationPitch = (float) (pitch * 180 / Math.PI);
    }
}
