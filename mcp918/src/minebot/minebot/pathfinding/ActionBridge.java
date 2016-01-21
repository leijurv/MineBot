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
public class ActionBridge extends ActionPlaceOrBreak {
    public ActionBridge(BlockPos from, BlockPos to) {
        super(from, to, new BlockPos[]{to, new BlockPos(to.getX(), to.getY() + 1, to.getZ())}, new BlockPos[]{new BlockPos(to.getX(), to.getY() - 1, to.getZ())});
    }
    @Override
    protected double calculateCost() {
        if (canWalkOn(blocksToPlace[0])) {//this is a walk, not a bridge
            if (canWalkThrough(blocksToBreak[0]) && canWalkThrough(blocksToBreak[1])) {
                return WALK_ONE_BLOCK_COST;
            }
            //double hardness1 = blocksToBreak[0].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[0]);
            //double hardness2 = blocksToBreak[1].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[1]);
            //System.out.println("Can't walk through " + blocksToBreak[0] + " (hardness" + hardness1 + ") or " + blocksToBreak[1] + " (hardness " + hardness2 + ")");
            return WALK_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak() * 10;
        } else {//this is a bridge, so we need to place a block
            //System.out.println("Can't walk on " + Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        }
        return 1000;
    }
    @Override
    protected boolean tick0() {
        MineBot.lookAtBlock(new BlockPos(to.getX(), to.getY() + 1, to.getZ()), false);//look at where we are walking
        MineBot.forward = true;//we are going forward
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.equals(to)) {//if we are there
            System.out.println("Done walking to " + to);
            MineBot.forward = false;//stop walking forwards
            return true;//and we are done
        }
        System.out.println("Trying to get to " + to + " currently at " + whereAmI);
        return false;//not there yet
    }
}
