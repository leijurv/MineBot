/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.ui.LookManager;
import minebot.MineBot;
import minebot.movement.MovementManager;
import minebot.util.Out;
import minebot.util.ToolSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionPillar extends ActionPlaceOrBreak {
    public ActionPillar(BlockPos start) {
        super(start, start.up(), new BlockPos[]{start.up(2)}, new BlockPos[]{start});
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        Block fromDown = Minecraft.theMinecraft.theWorld.getBlockState(from).getBlock();
        boolean ladder = fromDown instanceof BlockLadder || fromDown instanceof BlockVine;
        if (!ladder) {
            Block d = Minecraft.theMinecraft.theWorld.getBlockState(from.down()).getBlock();
            if (d instanceof BlockLadder || d instanceof BlockVine) {
                return COST_INF;
            }
        }
        if ((!MineBot.hasThrowaway && !ladder) || !MineBot.allowVerticalMotion) {
            return COST_INF;
        }
        if (fromDown instanceof BlockVine) {
            if (getAgainst(from) == null) {
                return COST_INF;
            }
        }
        double hardness = getTotalHardnessOfBlocksToBreak(ts);
        if (hardness != 0) {
            Block tmp = Minecraft.theMinecraft.theWorld.getBlockState(from.up(2)).getBlock();
            if (tmp instanceof BlockLadder || tmp instanceof BlockVine) {
                hardness = 0;
            } else if (!canWalkOn(from.up(3)) || canWalkThrough(from.up(3)) || Minecraft.theMinecraft.theWorld.getBlockState(from.up(3)).getBlock() instanceof BlockFalling) {//if the block above where we want to break is not a full block, don't do it
                return COST_INF;
            }
        }
        if (isLiquid(from) || isLiquid(from.down())) {//can't pillar on water or in water
            return COST_INF;
        }
        if (ladder) {
            return LADDER_UP_ONE_COST + hardness;
        } else {
            return JUMP_ONE_BLOCK_COST + PLACE_ONE_BLOCK_COST + hardness;
        }
    }
    int numTicks = 0;
    @Override
    protected boolean tick0() {
        if (!switchtothrowaway(true)) {//get ready to place a throwaway block
            return false;
        }
        return tick1();
    }
    public BlockPos getAgainst(BlockPos vine) {
        if (Minecraft.theMinecraft.theWorld.getBlockState(vine.north()).getBlock().isBlockNormalCube()) {
            return vine.north();
        }
        if (Minecraft.theMinecraft.theWorld.getBlockState(vine.south()).getBlock().isBlockNormalCube()) {
            return vine.south();
        }
        if (Minecraft.theMinecraft.theWorld.getBlockState(vine.east()).getBlock().isBlockNormalCube()) {
            return vine.east();
        }
        if (Minecraft.theMinecraft.theWorld.getBlockState(vine.west()).getBlock().isBlockNormalCube()) {
            return vine.west();
        }
        return null;
    }
    public boolean tick1() {
        IBlockState fromDown = Minecraft.theMinecraft.theWorld.getBlockState(from);
        boolean ladder = fromDown.getBlock() instanceof BlockLadder || fromDown.getBlock() instanceof BlockVine;
        boolean vine = fromDown.getBlock() instanceof BlockVine;
        if (!ladder && !LookManager.lookAtBlock(positionsToPlace[0], true)) {
            return false;
        }
        numTicks++;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        boolean blockIsThere = canWalkOn(from) || ladder;
        if (ladder) {
            BlockPos against = vine ? getAgainst(from) : from.offset(fromDown.getValue(BlockLadder.FACING).getOpposite());
            if (against == null) {
                Out.gui("Unable to climb vines", Out.Mode.Standard);
                return false;
            }
            if (thePlayer.getPosition0().equals(against.up()) || thePlayer.getPosition0().equals(to)) {
                return true;
            }
            /*if (thePlayer.getPosition0().getX() != from.getX() || thePlayer.getPosition0().getZ() != from.getZ()) {
             MineBot.moveTowardsBlock(from);
             }*/
            MovementManager.moveTowardsBlock(against);
            return false;
        } else {
            MovementManager.jumping = thePlayer.posY < to.getY(); //if our Y coordinate is above our goal, stop jumping
            MovementManager.sneak = true;
            //otherwise jump
            if (numTicks > 40) {
                double diffX = thePlayer.posX - (to.getX() + 0.5);
                double diffZ = thePlayer.posZ - (to.getZ() + 0.5);
                double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
                if (dist > 0.17) {//why 0.17? because it seemed like a good number, that's why
                    MovementManager.forward = true;//if it's been more than forty ticks of trying to jump and we aren't done yet, go forward, maybe we are stuck
                }
            }
            if (!blockIsThere) {
                Out.log("Block not there yet");
                if (!MineBot.isAir(from)) {
                    MovementManager.isLeftClick = true;
                    blockIsThere = false;
                } else if (Minecraft.theMinecraft.thePlayer.isSneaking()) {
                    Minecraft.theMinecraft.rightClickMouse();//constantly right click
                }
            }
        }
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.equals(to) && blockIsThere) {//if we are at our goal and the block below us is placed
            Out.log("Done pillaring to " + to);
            MovementManager.jumping = false;//stop jumping
            return true;//we are done
        }
        return false;
    }
}
