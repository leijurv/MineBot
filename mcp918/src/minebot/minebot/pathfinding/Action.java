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
    public static final double WALK_ONE_BLOCK_COST = 5;
    public static final double JUMP_ONE_BLOCK_COST = 5;
    public static final double FALL_ONE_BLOCK_COST = 1;
    public static final double PLACE_ONE_BLOCK_COST = 25;
    public final BlockPos from;
    public final BlockPos to;
    private Double cost;
    public boolean finished = false;
    protected Action(BlockPos from, BlockPos to) {
        this.from = from;
        this.to = to;
        this.cost = null;
    }
    /**
     * Get the cost. It's cached
     *
     * @return
     */
    public double cost() {
        if (cost == null) {
            cost = calculateCost();
        }
        return cost;
    }
    /**
     * Do the cost calculation of how hard this action would be
     *
     * @return
     */
    protected abstract double calculateCost();
    /**
     * What action would get you from the blockpos "from" to the blockpos "to"
     *
     * @param from
     * @param to
     * @return
     */
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
    public static boolean isWater(Block b) {
        return b.equals(Block.getBlockById(8)) || b.equals(Block.getBlockById(9));
    }
    /**
     * Can I walk through this block
     *
     * @param pos
     * @return
     */
    public static boolean canWalkThrough(BlockPos pos) {//fix this. this assumes that air is the only block with no collisions, while actually there are others (e.g. torches)
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock();
        return block.isPassable(Minecraft.theMinecraft.theWorld, pos);
    }
    /**
     * Can I walk on this block without anything weird happening like me falling
     * through
     *
     * @param pos
     * @return
     */
    public static boolean canWalkOn(BlockPos pos) {//eh
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock();
        if (isWater(block)) {
            return isWater(Minecraft.theMinecraft.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())).getBlock());
        }
        return block.isBlockNormalCube();
    }
    /**
     * Tick this action
     *
     * @return is it done
     */
    public abstract boolean tick();
}
