/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.MineBot;
import minebot.movement.MovementManager;
import minebot.util.Out;
import minebot.util.ToolSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionDescendThree extends ActionPlaceOrBreak {
    public ActionDescendThree(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{end.up(4), end.up(3), end.up(2), end.up(), end}, new BlockPos[]{end.down()});
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        if (!canWalkOn(positionsToPlace[0])) {
            return COST_INF;
        }
        if (getTotalHardnessOfBlocksToBreak(ts) != 0) {
            return COST_INF;
        }
        return WALK_ONE_BLOCK_COST + FALL_THREE_BLOCK_COST;
    }
    @Override
    protected boolean tick0() {//basically just hold down W until we are where we want to be
        MovementManager.moveTowardsBlock(to);
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.equals(to)) {
            Out.log("Done falling to " + to);
            MovementManager.clearMovement();
            return true;
        }
        return false;
    }
}
