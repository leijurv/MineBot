/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import java.util.Objects;
import minebot.LookManager;
import minebot.MineBot;
import minebot.pathfinding.PathFinder;
import minebot.util.ToolSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionClimb extends ActionPlaceOrBreak {
    BlockPos[] against = new BlockPos[3];
    public ActionClimb(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{end, start.up(2), end.up()}, new BlockPos[]{end.down()});
        BlockPos placementLocation = positionsToPlace[0];//end.down()
        int i = 0;
        if (!placementLocation.north().equals(from)) {
            against[i++] = placementLocation.north();
        }
        if (!placementLocation.south().equals(from)) {
            against[i++] = placementLocation.south();
        }
        if (!placementLocation.east().equals(from)) {
            against[i++] = placementLocation.east();
        }
        if (!placementLocation.west().equals(from)) {
            against[i++] = placementLocation.west();
        }
        //TODO: add ability to place against .down() as well as the cardinal directions
        //useful for when you are starting a staircase without anything to place against
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        if (!canWalkOn(positionsToPlace[0])) {
            for (int i = 0; i < against.length; i++) {
                if (Minecraft.theMinecraft.theWorld.getBlockState(against[i]).getBlock().isBlockNormalCube()) {
                    return JUMP_ONE_BLOCK_COST + WALK_ONE_BLOCK_COST + PLACE_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak(ts);
                }
            }
            return PathFinder.COST_INF;
        }
        return JUMP_ONE_BLOCK_COST + WALK_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak(ts);
    }
    @Override
    protected boolean tick0() {//basically just hold down W and space until we are where we want to be
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        if (!canWalkOn(positionsToPlace[0])) {
            for (int i = 0; i < against.length; i++) {
                if (Minecraft.theMinecraft.theWorld.getBlockState(against[i]).getBlock().isBlockNormalCube()) {
                    if (!switchtothrowaway(true)) {//get ready to place a throwaway block
                        return false;
                    }
                    double faceX = (to.getX() + against[i].getX() + 1.0D) * 0.5D;
                    double faceY = (to.getY() + against[i].getY()) * 0.5D;
                    double faceZ = (to.getZ() + against[i].getZ() + 1.0D) * 0.5D;
                    LookManager.lookAtCoords(faceX, faceY, faceZ, true);
                    if (Objects.equals(MineBot.whatAreYouLookingAt(), against[i])) {
                        Minecraft.theMinecraft.rightClickMouse();//todo: check if we are standing in the way and preventing this block from being placed
                    }
                    System.out.println("Trying to look at " + against[i] + ", actually looking at" + MineBot.whatAreYouLookingAt());
                    return false;
                }
            }
            GuiScreen.sendChatMessage("This is impossible", true);
            return false;
        }
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
