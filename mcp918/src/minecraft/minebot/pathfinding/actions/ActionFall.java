/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.MineBot;
import minebot.movement.MovementManager;
import minebot.util.ToolSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionFall extends ActionPlaceOrBreak {
    public ActionFall(BlockPos start) {
        super(start, start.down(), new BlockPos[]{start.down()}, new BlockPos[0]);
    }
    int numTicks = 0;
    @Override
    protected boolean tick0() {
        numTicks++;
        if (numTicks > 10) {
            MovementManager.moveTowardsBlock(to);
        }
        return Minecraft.theMinecraft.thePlayer.getPosition0().equals(to);
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        if (!MineBot.allowVerticalMotion || !canWalkOn(to.down())) {
            return COST_INF;
        }
        Block td = Minecraft.theMinecraft.theWorld.getBlockState(to).getBlock();
        boolean ladder = td instanceof BlockLadder || td instanceof BlockVine;
        if (ladder) {
            return LADDER_DOWN_ONE_COST;
        } else {
            return FALL_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak(ts);
        }
    }
}
