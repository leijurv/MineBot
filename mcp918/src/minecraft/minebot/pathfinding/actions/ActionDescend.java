/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.MineBot;
import minebot.util.Out;
import minebot.util.ToolSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionDescend extends ActionPlaceOrBreak {
    public ActionDescend(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{end.up(2), end.up(), end}, new BlockPos[]{end.down()});
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        if (!canWalkOn(positionsToPlace[0])) {
            return Action.COST_INF;
        }
        Block tmp1 = Minecraft.theMinecraft.theWorld.getBlockState(to).getBlock();
        if (tmp1 instanceof BlockLadder || tmp1 instanceof BlockVine) {
            return Action.COST_INF;
        }
        return WALK_ONE_BLOCK_COST + FALL_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak(ts);
    }
    @Override
    protected boolean tick0() {//basically just hold down W until we are where we want to be
        MineBot.moveTowardsBlock(to);
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.equals(to)) {
            Out.log("Done falling to " + to);
            MineBot.clearMovement();
            return true;
        }
        return false;
    }
}
