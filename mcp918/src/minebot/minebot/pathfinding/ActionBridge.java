/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import minebot.util.ToolSet;
import java.util.Objects;
import java.util.Random;
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
    protected double calculateCost(ToolSet ts) {
        double WC = isWater(blocksToBreak[0]) || isWater(blocksToBreak[1]) ? WALK_ONE_IN_WATER_COST : WALK_ONE_BLOCK_COST;
        if (canWalkOn(positionsToPlace[0])) {//this is a walk, not a bridge
            if (canWalkThrough(positionsToBreak[0]) && canWalkThrough(positionsToBreak[1])) {
                return WC;
            }
            //double hardness1 = blocksToBreak[0].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[0]);
            //double hardness2 = blocksToBreak[1].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[1]);
            //System.out.println("Can't walk through " + blocksToBreak[0] + " (hardness" + hardness1 + ") or " + blocksToBreak[1] + " (hardness " + hardness2 + ")");
            return WC + getTotalHardnessOfBlocksToBreak(ts);
        } else {//this is a bridge, so we need to place a block
            //return 1000000;
            if (blocksToPlace[0].equals(Block.getBlockById(0)) || (!isWater(blocksToPlace[0]) && blocksToPlace[0].isReplaceable(Minecraft.theMinecraft.theWorld, positionsToPlace[0]))) {
                return WC + PLACE_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak();
            }
            return 100000000;
            //System.out.println("Can't walk on " + Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        }
    }
    boolean wasTheBridgeBlockAlwaysThere = true;//did we have to place a bridge block or was it always there
    Boolean oneInTen = null;//a one in ten chance
    public boolean amIGood() {
        return canWalkThrough(positionsToBreak[0]) && canWalkThrough(positionsToBreak[1]) && canWalkOn(positionsToPlace[0]);
    }
    public int dx() {
        return to.getX() - from.getX();
    }
    public int dz() {
        return to.getZ() - from.getZ();
    }
    @Override
    protected boolean tick0() {
        if (oneInTen == null) {
            oneInTen = new Random().nextInt(10) == 0;
        }
        boolean isTheBridgeBlockThere = canWalkOn(positionsToPlace[0]);
        //System.out.println("is block there: " + isTheBridgeBlockThere + " block " + Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[0]).getBlock());
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (whereAmI.getY() != to.getY()) {
            System.out.println("Wrong Y coordinate");
            if (whereAmI.getY() < to.getY()) {
                MineBot.jumping = true;
            }
            return false;
        }
        if (isTheBridgeBlockThere) {//either the bridge block was there the whole time or we just placed it
            if (oneInTen && wasTheBridgeBlockAlwaysThere) {
                //basically one in every ten blocks we walk forwards normally without sneaking and placing, rotate to look forwards.
                //this way we tend towards looking forwards
                MineBot.forward = MineBot.lookAtBlock(to, false);
            } else {
                MineBot.moveTowardsBlock(to);
            }
            if (wasTheBridgeBlockAlwaysThere) {
                if (MineBot.forward && !MineBot.backward) {
                    thePlayer.setSprinting(true);
                }
            }
            if (whereAmI.equals(to)) {//if we are there
                System.out.println("Done walking to " + to);
                return true;//and we are done
            }
            System.out.println("Trying to get to " + to + " currently at " + whereAmI);
            return false;//not there yet
        } else {
            wasTheBridgeBlockAlwaysThere = false;
            MineBot.sneak = true;
            if (whereAmI.equals(to)) {
                //if we are in the block that we are trying to get to, we are sneaking over air and we need to place a block beneath us against the one we just walked off of
                //System.out.println(from + " " + to + " " + faceX + "," + faceY + "," + faceZ + " " + whereAmI);
                switchtothrowaway();//get ready to place a throwaway block
                double faceX = (to.getX() + from.getX() + 1.0D) * 0.5D;
                double faceY = (to.getY() + from.getY() - 1.0D) * 0.5D;
                double faceZ = (to.getZ() + from.getZ() + 1.0D) * 0.5D;
                //faceX,faceY,faceZ is the middle of the face between from and to
                BlockPos goalLook = new BlockPos(from.getX(), from.getY() - 1, from.getZ());//this is the block we were just standing on, and the one we want to place against
                MineBot.backward = MineBot.lookAtCoords(faceX, faceY, faceZ, true);//if we are in the block, then we are off the edge of the previous looking backward, so we should be moving backward
                if (Objects.equals(MineBot.whatAreYouLookingAt(), goalLook)) {
                    Minecraft.theMinecraft.rightClickMouse();//wait to right click until we are able to place
                    return false;
                }
                System.out.println("Trying to look at " + goalLook + ", actually looking at" + MineBot.whatAreYouLookingAt());
                return false;
            } else {
                System.out.println("Not there yet m9");
                MineBot.moveTowardsBlock(to);//move towards not look at because if we are bridging for a couple blocks in a row, it is faster if we dont spin around and walk forwards then spin around and place backwards for every block
                return false;
            }
        }
    }
}
