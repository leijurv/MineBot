/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.util.ToolSet;
import net.minecraft.block.Block;
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
    public static final double WALK_ONE_IN_WATER_COST = WALK_ONE_BLOCK_COST * 2;
    public static final double JUMP_ONE_BLOCK_COST = 5;
    /**
     * Doesn't include walking forwards, just the falling
     */
    public static final double FALL_ONE_BLOCK_COST = 1;
    /**
     * It doesn't actually take ten ticks to place a block, this cost is so high
     * because we want to generally conserve blocks which might be limited
     */
    public static final double PLACE_ONE_BLOCK_COST = 20;
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
        if (cost < 1) {
            System.out.println("Bad cost " + this + " " + cost);
        }
        return cost;
    }
    /**
     * Do the cost calculation of how hard this action would be
     *
     * @return
     */
    protected final double calculateCost() {
        return calculateCost(new ToolSet());
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
    public static boolean avoidBreaking(BlockPos pos) {
        return isLiquid(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())) || isLiquid(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())) || isLiquid(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())) || isLiquid(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)) || isLiquid(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)) || isLiquid(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ()));
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
