/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.Arrays;
import minebot.MineBot;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public abstract class ActionPlaceOrBreak extends Action {
    public static final double HARDNESS_MULTIPLIER = 50;
    public final BlockPos[] positionsToBreak;//the positions that need to be broken before this action can ensue
    public final BlockPos[] positionsToPlace;//the positions where we need to place a block before this aciton can ensue
    public final Block[] blocksToBreak;//the blocks at those positions
    public final Block[] blocksToPlace;
    public ActionPlaceOrBreak(BlockPos start, BlockPos end, BlockPos[] toBreak, BlockPos[] toPlace) {
        super(start, end);
        this.positionsToBreak = toBreak;
        this.positionsToPlace = toPlace;
        blocksToBreak = new Block[positionsToBreak.length];
        blocksToPlace = new Block[positionsToPlace.length];
        for (int i = 0; i < blocksToBreak.length; i++) {
            blocksToBreak[i] = Minecraft.theMinecraft.theWorld.getBlockState(positionsToBreak[i]).getBlock();
        }
        for (int i = 0; i < blocksToPlace.length; i++) {
            blocksToPlace[i] = Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[i]).getBlock();
        }
    }
    public double getTotalHardnessOfBlocksToBreak() {//of all the blocks we need to break before starting this action, what's the sum of how hard they are (phrasing)
        double sum = 0;
        for (int i = 0; i < blocksToBreak.length; i++) {
            sum += 1 / (blocksToBreak[i].getPlayerRelativeBlockHardness(Minecraft.theMinecraft.thePlayer, Minecraft.theMinecraft.theWorld, positionsToBreak[i]));
            System.out.println(blocksToBreak[i].getPlayerRelativeBlockHardness(Minecraft.theMinecraft.thePlayer, Minecraft.theMinecraft.theWorld, positionsToBreak[i]));
            //sum += blocksToBreak[i].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[i]);
        }
        return sum;
    }
    @Override
    public String toString() {
        return this.getClass() + " place " + Arrays.asList(blocksToPlace) + " break " + Arrays.asList(blocksToBreak) + " cost " + cost() + " break cost " + getTotalHardnessOfBlocksToBreak();
    }
    @Override
    public boolean tick() {
        //breaking first
        for (int i = 0; i < blocksToBreak.length; i++) {
            if (!canWalkThrough(Minecraft.theMinecraft.theWorld.getBlockState(positionsToBreak[i]).getBlock())) {
                //System.out.println("Breaking " + blocksToBreak[i] + " at " + positionsToBreak[i]);
                MineBot.lookAtBlock(positionsToBreak[i], true);//look at the block we are breaking
                MineBot.isLeftClick = true;//hold down left click
                if (canWalkThrough(Minecraft.theMinecraft.theWorld.getBlockState(positionsToBreak[i]).getBlock())) {
                    MineBot.letGoOfLeftClick();
                    System.out.println("Done breaking " + blocksToBreak[i] + " at " + positionsToBreak[i]);
                }
                return false;
            }
        }
        MineBot.letGoOfLeftClick();//sometimes it keeps on left clicking so we need this here (yes it scares me too)
        for (int i = 0; i < blocksToPlace.length; i++) {
            if (!canWalkOn(Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[i]).getBlock())) {
                MineBot.lookAtBlock(positionsToPlace[i], true);
                //System.out.println("CANT DO IT. CANT WALK ON " + blocksToPlace[i] + " AT " + positionsToPlace[i]);
                //one of the blocks that needs to be there isn't there
                //so basically someone mined out our path from under us
            }
        }
        return tick0();
    }
    protected abstract boolean tick0();
}
