/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public abstract class Action {
    public final BlockPos from;
    public final BlockPos to;
    protected Action(BlockPos from, BlockPos to) {
        this.from = from;
        this.to = to;
    }
    public abstract int cost();
    public static Action getAction(BlockPos to, BlockPos from) {
        System.out.println("Getting cost from " + from + " to " + to);
        int xDiff = to.getX() - from.getX();
        int yDiff = to.getY() - from.getY();
        int zDiff = to.getZ() - from.getZ();
        if (yDiff == 0) {
            return new ActionPillar(from, to);
        }
        return null;
    }
    public static boolean canWalkThrough(BlockPos pos) {
        return Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock().equals(Block.getBlockById(0));
    }
    public static boolean canWalkOn(BlockPos pos) {
        return Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock().isBlockNormalCube();
    }
}
