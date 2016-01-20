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
public class ActionPillar extends ActionPlaceOrBreak {
    public ActionPillar(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{new BlockPos(end.getX(), end.getY() + 1, end.getZ())}, new BlockPos[]{start});
    }
    @Override
    protected double calculateCost() {
        return JUMP_ONE_BLOCK_COST + PLACE_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak() * 100000;
    }
    @Override
    protected boolean tick0() {
        MineBot.lookAtBlock(new BlockPos(from.getX(), from.getY() - 1, from.getZ()), true);
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        if (thePlayer.posY >= to.getY()) {//if our Y coordinate is above our goal, stop jumping
            MineBot.jumping = false;
        } else {
            MineBot.jumping = true;//otherwise jump
        }
        Minecraft.theMinecraft.rightClickMouse();//constantly right click
        BlockPos whereAmI = new BlockPos((int) thePlayer.posX, (int) thePlayer.posY, (int) thePlayer.posZ);
        if (whereAmI.equals(to) && canWalkOn(Minecraft.theMinecraft.theWorld.getBlockState(from).getBlock())) {//if we are at our goal and the block below us is placed
            System.out.println("Done pillaring to " + to);
            MineBot.jumping = false;//stop jumping
            return true;//we are done
        }
        return false;
    }
}
