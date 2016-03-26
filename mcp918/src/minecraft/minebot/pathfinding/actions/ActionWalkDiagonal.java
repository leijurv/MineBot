/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.MineBot;
import minebot.movement.MovementManager;
import minebot.util.ToolSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author leijurv
 */
public class ActionWalkDiagonal extends ActionPlaceOrBreak {
    public ActionWalkDiagonal(BlockPos start, EnumFacing dir1, EnumFacing dir2) {
        super(start, start.offset(dir1).offset(dir2), new BlockPos[]{start.offset(dir1), start.offset(dir1).up(), start.offset(dir2), start.offset(dir2).up(), start.offset(dir1).offset(dir2), start.offset(dir1).offset(dir2).up()}, new BlockPos[]{start.offset(dir1).down(), start.offset(dir2).down(), start.offset(dir1).offset(dir2).down()});
    }
    @Override
    protected boolean tick0() {
        MovementManager.moveTowardsBlock(to);
        return to.equals(Minecraft.theMinecraft.thePlayer.getPosition0());
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        if (!MineBot.allowDiagonal) {
            return COST_INF;
        }
        if (getTotalHardnessOfBlocksToBreak(ts) != 0) {
            return COST_INF;
        }
        for (BlockPos pl : positionsToPlace) {
            if (!canWalkOn(pl)) {
                return COST_INF;
            }
        }
        return Math.sqrt(2) * (isWater(from) || isWater(to) ? WALK_ONE_IN_WATER_COST : WALK_ONE_BLOCK_COST);
    }
}
