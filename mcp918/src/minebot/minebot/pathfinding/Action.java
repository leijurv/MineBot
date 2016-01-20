/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public abstract class Action {
    public final BlockPos from;
    public final BlockPos to;
    private Integer cost;
    protected Action(BlockPos from, BlockPos to) {
        this.from = from;
        this.to = to;
        this.cost = null;
    }
    public int cost() {
        if (cost == null) {
            cost = calculateCost();
        }
        return cost;
    }
    protected abstract int calculateCost();
    public static Action getAction(BlockPos to, BlockPos from) {
        System.out.println("Getting cost from " + from + " to " + to);
        int xDiff = to.getX() - from.getX();
        int yDiff = to.getY() - from.getY();
        int zDiff = to.getZ() - from.getZ();
        if (yDiff == 0) {
            return new ActionBridge(from, to);
        }
        if (yDiff == 1) {
            if (xDiff == 0 && zDiff == 0) {
                //pillar
            }
            if (Math.abs(xDiff) + Math.abs(zDiff) == 1) {
                //climb
            }
        }
        if (yDiff == -1 && Math.abs(xDiff) + Math.abs(zDiff) == 1) {
            //fall
        }
        return null;
    }
    public static boolean canWalkThrough(Block block) {
        return block.equals(Block.getBlockById(0));
    }
    public static boolean canWalkOn(Block block) {
        return block.isBlockNormalCube();
    }
}
