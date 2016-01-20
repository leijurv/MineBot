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
    public final double WALK_ONE_BLOCK_COST = 1;
    public final double JUMP_ONE_BLOCK_COST = 1;
    public final double FALL_ONE_BLOCK_COST = 0.5;
    public final BlockPos from;
    public final BlockPos to;
    private Double cost;
    protected Action(BlockPos from, BlockPos to) {
        this.from = from;
        this.to = to;
        this.cost = null;
    }
    public double cost() {
        if (cost == null) {
            cost = calculateCost();
        }
        return cost;
    }
    protected abstract double calculateCost();
    public static Action getAction(BlockPos from, BlockPos to) {
        int xDiff = to.getX() - from.getX();
        int yDiff = to.getY() - from.getY();
        int zDiff = to.getZ() - from.getZ();
        if (yDiff == 0) {
            return new ActionBridge(from, to);
        }
        if (yDiff == 1) {
            if (xDiff == 0 && zDiff == 0) {
                return new ActionPillar(from, to);
            }
            if (Math.abs(xDiff) + Math.abs(zDiff) == 1) {
                return new ActionClimb(from, to);
            }
        }
        if (yDiff == -1 && Math.abs(xDiff) + Math.abs(zDiff) == 1) {
            return new ActionFall(from, to);
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
