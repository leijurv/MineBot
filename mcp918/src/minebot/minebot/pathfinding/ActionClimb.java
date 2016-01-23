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
public class ActionClimb extends ActionPlaceOrBreak {
    public ActionClimb(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{end, new BlockPos(start.getX(), start.getY() + 2, start.getZ()), new BlockPos(end.getX(), end.getY() + 1, end.getZ())}, new BlockPos[]{new BlockPos(end.getX(), end.getY() - 1, end.getZ())});
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        if (!canWalkOn(positionsToPlace[0])) {
            return 10000;
        }
        return JUMP_ONE_BLOCK_COST + WALK_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak();
    }
    @Override
    protected boolean tick0() {//basically just hold down W and space until we are where we want to be
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        double flatDistToNext = Math.abs((to.getX() + 0.5D) - thePlayer.posX) + Math.abs((to.getZ() + 0.5D) - thePlayer.posZ);
        boolean pointingInCorrectDirection = MineBot.moveTowardsBlock(to);
        MineBot.jumping = flatDistToNext < 1 && pointingInCorrectDirection;
        //once we are pointing the right way and moving, start jumping
        //this is slightly more efficient because otherwise we might start jumping before moving, and fall down without moving onto the block we want to jump onto
        //also wait until we are close enough, because we might jump and hit our head on an adjacent block
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.equals(to)) {
            System.out.println("Done climbing to " + to);
            MineBot.clearMovement();
            return true;
        }
        return false;
    }
}
