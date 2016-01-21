/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import minebot.MineBot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionFall extends ActionPlaceOrBreak {
    public ActionFall(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{new BlockPos(end.getX(), end.getY() + 1, end.getZ()), end, new BlockPos(end.getX(), end.getY() + 2, end.getZ())}, new BlockPos[]{new BlockPos(end.getX(), end.getY() - 1, end.getZ())});
    }
    @Override
    protected double calculateCost() {
        if (!canWalkOn(positionsToPlace[0])) {
            return 10000;
        }
        return WALK_ONE_BLOCK_COST + FALL_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak();
    }
    @Override
    protected boolean tick0() {//basically just hold down W until we are where we want to be
        MineBot.lookAtBlock(new BlockPos(to.getX(), to.getY() + 1, to.getZ()), false);
        MineBot.forward = true;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.equals(to)) {
            System.out.println("Done falling to " + to);
            MineBot.forward = false;
            return true;
        }
        return false;
    }
}
