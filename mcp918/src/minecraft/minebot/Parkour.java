/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import minebot.pathfinding.actions.Action;
import minebot.util.Out;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author leijurv
 */
public class Parkour {
    public static boolean preemptivejump() {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos playerFeet = thePlayer.getPosition0();
        EnumFacing dir = thePlayer.getHorizontalFacing();
        for (int height = 0; height < 3; height++) {
            BlockPos bp = playerFeet.offset(dir, 1).up(height);
            if (!Minecraft.theMinecraft.theWorld.getBlockState(bp).getBlock().equals(Block.getBlockById(0))) {
                return Action.canWalkOn(playerFeet.offset(dir, 1));
            }
        }
        for (int height = 0; height < 3; height++) {
            BlockPos bp = playerFeet.offset(dir, 2).up(height);
            if (!Minecraft.theMinecraft.theWorld.getBlockState(bp).getBlock().equals(Block.getBlockById(0))) {
                return Action.canWalkOn(playerFeet.offset(dir, 2));
            }
        }
        return Action.canWalkOn(playerFeet.offset(dir, 3));
    }
    public static void parkour() {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos playerFeet = thePlayer.getPosition0();
        BlockPos down = playerFeet.down();
        BlockPos prev = down.offset(thePlayer.getHorizontalFacing().getOpposite());
        boolean onAir = Minecraft.theMinecraft.theWorld.getBlockState(down).getBlock().equals(Block.getBlockById(0));
        boolean jumpAnyway = preemptivejump();
        if (onAir || jumpAnyway) {
            if ((thePlayer.isSprinting() && !Minecraft.theMinecraft.theWorld.getBlockState(prev).getBlock().equals(Block.getBlockById(0))) || !Minecraft.theMinecraft.theWorld.getBlockState(playerFeet.offset(thePlayer.getHorizontalFacing())).getBlock().equals(Block.getBlockById(0))) {
                double distX = Math.abs(thePlayer.posX - (prev.getX() + 0.5));
                distX *= Math.abs(prev.getX() - down.getX());
                double distZ = Math.abs(thePlayer.posZ - (prev.getZ() + 0.5));
                distZ *= Math.abs(prev.getZ() - down.getZ());
                double dist = distX + distZ;
                thePlayer.rotationYaw = Math.round(thePlayer.rotationYaw / 90) * 90;
                if (dist > 0.7) {
                    MovementManager.jumping = true;
                    Out.gui("Parkour jumping!!!", Out.Mode.Standard);
                }
            }
        }
    }
}
