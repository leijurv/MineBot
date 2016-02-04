/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.util.ToolSet;
import minebot.MineBot;
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
        if (getTotalHardnessOfBlocksToBreak(ts) != 0) {
            return 1000000;
        }
        if (isLiquid(from) || isLiquid(from.down())) {//can't pillar on water or in water
            return 1000000;
        }
        return JUMP_ONE_BLOCK_COST + PLACE_ONE_BLOCK_COST;//dont ever break the block above you, so multiply by a million
    }
    int numTicks = 0;
    @Override
    protected boolean tick0() {
        if (!MineBot.lookAtBlock(positionsToPlace[0], true)) {
            return false;
        }
        numTicks++;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        MineBot.jumping = thePlayer.posY < to.getY(); //if our Y coordinate is above our goal, stop jumping
        //otherwise jump
        if (numTicks > 40) {
            double diffX = thePlayer.posX - (to.getX() + 0.5);
            double diffZ = thePlayer.posZ - (to.getZ() + 0.5);
            double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
            if (dist > 0.17) {//why 0.17? because it seemed like a good number, that's why
                MineBot.forward = true;//if it's been more than forty ticks of trying to jump and we aren't done yet, go forward, maybe we are stuck
            }
        }
        boolean blockIsThere = canWalkOn(from);
        if (!blockIsThere) {
            System.out.println("Block not there yet");
            switchtothrowaway();
            Minecraft.theMinecraft.rightClickMouse();//constantly right click
        }
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.equals(to) && blockIsThere) {//if we are at our goal and the block below us is placed
            System.out.println("Done pillaring to " + to);
            MineBot.jumping = false;//stop jumping
            return true;//we are done
        }
        return false;
    }
}
