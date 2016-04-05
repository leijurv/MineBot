/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import java.util.Objects;
import minebot.ui.LookManager;
import minebot.MineBot;
import minebot.movement.MovementManager;
import minebot.util.Out;
import minebot.util.ToolSet;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

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
            against[i] = placementLocation.north();
            i++;
        }
        if (!placementLocation.south().equals(from)) {
            against[i] = placementLocation.south();
            i++;
        }
        if (!placementLocation.east().equals(from)) {
            against[i] = placementLocation.east();
            i++;
        }
        if (!placementLocation.west().equals(from)) {
            against[i] = placementLocation.west();
            i++;
        }
        //TODO: add ability to place against .down() as well as the cardinal directions
        //useful for when you are starting a staircase without anything to place against
    }
    @Override
    protected double calculateCost(ToolSet ts) {
        if (!canWalkOn(positionsToPlace[0])) {
            if (!MineBot.isAir(positionsToPlace[0]) && !isWater(positionsToPlace[0])) {
                return COST_INF;
            }
            for (BlockPos against1 : against) {
                if (Minecraft.theMinecraft.theWorld.getBlockState(against1).getBlock().isBlockNormalCube()) {
                    return JUMP_ONE_BLOCK_COST + WALK_ONE_BLOCK_COST + PLACE_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak(ts);
                }
            }
            return COST_INF;
        }
        if (Minecraft.theMinecraft.theWorld.getBlockState(from.up(3)).getBlock() instanceof BlockFalling) {//it would fall on us and possibly suffocate us
            return COST_INF;
        }
        return WALK_ONE_BLOCK_COST / 2 + Math.max(JUMP_ONE_BLOCK_COST, WALK_ONE_BLOCK_COST / 2) + getTotalHardnessOfBlocksToBreak(ts);//we walk half the block to get to the edge, then we walk the other half while simultaneously jumping (math.max because of how it's in parallel)
    }
    int ticksWithoutPlacement = 0;
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
                    EnumFacing side = Minecraft.theMinecraft.objectMouseOver.sideHit;
                    if (Objects.equals(MineBot.whatAreYouLookingAt(), against[i]) && MineBot.whatAreYouLookingAt().offset(side).equals(positionsToPlace[0])) {
                        ticksWithoutPlacement++;
                        MovementManager.sneak = true;
                        if (Minecraft.theMinecraft.thePlayer.isSneaking()) {
                            Minecraft.theMinecraft.rightClickMouse();
                        }
                        if (ticksWithoutPlacement > 20) {
                            MovementManager.backward = true;//we might be standing in the way, move back
                        }
                    }
                    Out.log("Trying to look at " + against[i] + ", actually looking at" + MineBot.whatAreYouLookingAt());
                    return false;
                }
            }
            Out.gui("This is impossible", Out.Mode.Standard);
            return false;
        }
        double flatDistToNext = Math.abs(to.getX() - from.getX()) * Math.abs((to.getX() + 0.5D) - thePlayer.posX) + Math.abs(to.getZ() - from.getZ()) * Math.abs((to.getZ() + 0.5D) - thePlayer.posZ);
        boolean pointingInCorrectDirection = MovementManager.moveTowardsBlock(to);
        MovementManager.jumping = flatDistToNext < 1.2 && pointingInCorrectDirection;
        //once we are pointing the right way and moving, start jumping
        //this is slightly more efficient because otherwise we might start jumping before moving, and fall down without moving onto the block we want to jump onto
        //also wait until we are close enough, because we might jump and hit our head on an adjacent block
        return Minecraft.theMinecraft.thePlayer.getPosition0().equals(to);
    }
}
