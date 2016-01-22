/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.Objects;
import minebot.MineBot;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionBridge extends ActionPlaceOrBreak {
    public ActionBridge(BlockPos from, BlockPos to) {
        super(from, to, new BlockPos[]{new BlockPos(to.getX(), to.getY() + 1, to.getZ()), to}, new BlockPos[]{new BlockPos(to.getX(), to.getY() - 1, to.getZ())});
    }
    @Override
    protected double calculateCost() {
        if (canWalkOn(positionsToPlace[0])) {//this is a walk, not a bridge
            if (canWalkThrough(positionsToBreak[0]) && canWalkThrough(positionsToBreak[1])) {
                return WALK_ONE_BLOCK_COST;
            }
            //double hardness1 = blocksToBreak[0].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[0]);
            //double hardness2 = blocksToBreak[1].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[1]);
            //System.out.println("Can't walk through " + blocksToBreak[0] + " (hardness" + hardness1 + ") or " + blocksToBreak[1] + " (hardness " + hardness2 + ")");
            return WALK_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak();
        } else {//this is a bridge, so we need to place a block
            //return 1000000;
            if (blocksToPlace[0].equals(Block.getBlockById(0)) || blocksToPlace[0].isReplaceable(Minecraft.theMinecraft.theWorld, positionsToPlace[0])) {
                return WALK_ONE_BLOCK_COST + PLACE_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak();
            }
            return 100000000;
            //System.out.println("Can't walk on " + Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        }
    }
    @Override
    protected boolean tick0() {
        boolean isTheBridgeBlockThere = canWalkOn(positionsToPlace[0]);
        //System.out.println("is block there: " + isTheBridgeBlockThere + " block " + Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (isTheBridgeBlockThere) {//either the bridge block was there the whole time or we just placed it
            MineBot.moveTowardsBlock(to);
            if (whereAmI.equals(to)) {//if we are there
                System.out.println("Done walking to " + to);
                return true;//and we are done
            }
            System.out.println("Trying to get to " + to + " currently at " + whereAmI);
            return false;//not there yet
        } else {
            MineBot.sneak = true;
            double faceX = (to.getX() + from.getX() + 1.0D) * 0.5D;
            double faceY = (to.getY() + from.getY() - 1.0D) * 0.5D;
            double faceZ = (to.getZ() + from.getZ() + 1.0D) * 0.5D;
            //double faceX = to.getX();
            //double faceY = to.getY();
            //double faceZ = to.getZ();
            BlockPos goalLook = new BlockPos(from.getX(), from.getY() - 1, from.getZ());
            if (whereAmI.equals(to)) {
                //System.out.println(from + " " + to + " " + faceX + "," + faceY + "," + faceZ + " " + whereAmI);
                if (Objects.equals(MineBot.whatAreYouLookingAt(), goalLook)) {
                    switchtothrowaway();
                    Minecraft.theMinecraft.rightClickMouse();
                    return false;
                }
                MineBot.backward = MineBot.lookAtCoords(faceX, faceY, faceZ, true);
                System.out.println("Trying to look at " + goalLook + ", actually looking at" + MineBot.whatAreYouLookingAt());
                return false;
            } else {
                System.out.println("Not there yet m9");
                MineBot.moveTowardsBlock(to);
                return false;
            }
        }
    }
}
