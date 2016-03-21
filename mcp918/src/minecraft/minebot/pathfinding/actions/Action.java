/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.pathfinding.PathFinder;
import minebot.util.Out;
import minebot.util.ToolSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public abstract class Action {
    //These costs are measured roughly in ticks btw
    public static final double WALK_ONE_BLOCK_COST = 20 / 4.317;
    public static final double WALK_ONE_IN_WATER_COST = 20 / 2.2;
    public static final double JUMP_ONE_BLOCK_COST = 5;
    public static final double LADDER_UP_ONE_COST = 20 / 2.35;
    public static final double LADDER_DOWN_ONE_COST = 20 / 3;
    /**
     * Doesn't include walking forwards, just the falling
     *
     * Based on a sketchy formula from minecraftwiki
     *
     * d(t) = 3.92 × (99 - 49.50×(0.98^t+1) - t)
     *
     * Solved in mathematica
     */
    public static final double FALL_ONE_BLOCK_COST = 5.11354;
    public static final double FALL_TWO_BLOCK_COST = 7.28283;
    public static final double FALL_THREE_BLOCK_COST = 8.96862;
    /**
     * It doesn't actually take ten ticks to place a block, this cost is so high
     * because we want to generally conserve blocks which might be limited
     */
    public static final double PLACE_ONE_BLOCK_COST = 20;
    /**
     * Add this to the cost of breaking any block. The cost of breaking any
     * block is calculated as the number of ticks that block takes to break with
     * the tools you have. You add this because there's always a little overhead
     * (e.g. looking at the block)
     */
    public static final double BREAK_ONE_BLOCK_ADD = 4;
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
     * @param ts
     * @return
     */
    public double cost(ToolSet ts) {
        if (cost == null) {
            cost = calculateCost0(ts == null ? new ToolSet() : ts);
        }
        if (cost < 1) {
            Out.log("Bad cost " + this + " " + cost);
        }
        return cost;
    }
    /**
     * Do the cost calculation of how hard this action would be
     *
     * @return
     */
    private final double calculateCost() {
        return calculateCost0(new ToolSet());
    }
    private double calculateCost0(ToolSet ts) {
        Block fromDown = Minecraft.theMinecraft.theWorld.getBlockState(from.down()).getBlock();
        if (fromDown instanceof BlockLadder) {
            if (!(this instanceof ActionPillar) && !(this instanceof ActionBridge)) {
                return PathFinder.COST_INF;
            }
        }
        return calculateCost(ts);
    }
    protected abstract double calculateCost(ToolSet ts);
    static Block waterFlowing = Block.getBlockById(8);
    static Block waterStill = Block.getBlockById(9);
    static Block lavaFlowing = Block.getBlockById(10);
    static Block lavaStill = Block.getBlockById(11);
    /**
     * Is this block water? Includes both still and flowing
     *
     * @param b
     * @return
     */
    public static boolean isWater(Block b) {
        return waterFlowing.equals(b) || waterStill.equals(b);
    }
    public static boolean isWater(BlockPos bp) {
        return isWater(Minecraft.theMinecraft.theWorld.getBlockState(bp).getBlock());
    }
    public static boolean isLiquid(Block b) {
        return b instanceof BlockLiquid;
        //return b != null && (waterFlowing.equals(b) || waterStill.equals(b) || lavaFlowing.equals(b) || lavaStill.equals(b));
    }
    public static boolean isFlowing(BlockPos pos) {
        IBlockState state = Minecraft.theMinecraft.theWorld.getBlockState(pos);
        Block b = state.getBlock();
        Material m = b.getMaterial();
        if (b instanceof BlockLiquid) {
            BlockLiquid bl = (BlockLiquid) b;
            if (BlockLiquid.getFlowDirection(Minecraft.theMinecraft.theWorld, pos, m) != -1000.0D) {
                return true;
            }
        }
        return false;
    }
    public static boolean isLava(Block b) {
        return lavaFlowing.equals(b) || lavaStill.equals(b);
    }
    public static boolean isLiquid(BlockPos p) {
        return isLiquid(Minecraft.theMinecraft.theWorld.getBlockState(p).getBlock());
    }
    public static boolean isIce(Block b) {
        return Block.getBlockFromName("minecraft:ice").equals(b);
    }
    public static boolean isIce(BlockPos b) {
        return isIce(Minecraft.theMinecraft.theWorld.getBlockState(b).getBlock());
    }
    public static boolean avoidBreaking(BlockPos pos) {
        return isIce(pos) || isLiquid(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())) || isLiquid(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())) || isLiquid(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())) || isLiquid(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)) || isLiquid(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)) || isLiquid(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
    }
    /**
     * Can I walk through this block? e.g. air, saplings, torches, etc
     *
     * @param pos
     * @return
     */
    public static boolean canWalkThrough(BlockPos pos) {
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock();
        boolean liquid = isLiquid(pos);
        if (liquid && isFlowing(pos)) {
            return false;
        }
        return block.isPassable(Minecraft.theMinecraft.theWorld, pos) && !isLiquid(pos.up());
    }
    /**
     * Can I walk on this block without anything weird happening like me falling
     * through? Includes water because we know that we automatically jump on
     * lava
     *
     * @param pos
     * @return
     */
    public static boolean canWalkOn(BlockPos pos) {
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock();
        if (block instanceof BlockLadder) {
            return true;
        }
        if (isWater(block)) {
            return isWater(Minecraft.theMinecraft.theWorld.getBlockState(pos.up()).getBlock());
        }
        return block.isBlockNormalCube() && !isLava(block);
    }
    /**
     * Tick this action
     *
     * @return is it done
     */
    public abstract boolean tick();
}
