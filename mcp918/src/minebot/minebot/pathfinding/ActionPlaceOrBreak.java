/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public abstract class ActionPlaceOrBreak extends Action {
    public final BlockPos[] positionsToBreak;
    public final BlockPos[] positionsToPlace;
    public final Block[] blocksToBreak;
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
    public double getTotalHardnessOfBlocksToBreak() {
        double sum = 0;
        for (int i = 0; i < blocksToBreak.length; i++) {
            sum += blocksToBreak[i].getBlockHardness(Minecraft.theMinecraft.theWorld, positionsToBreak[i]);
        }
        return sum;
    }
    public String toString() {
        return this.getClass() + " place " + Arrays.asList(blocksToPlace) + " break " + Arrays.asList(blocksToBreak) + " cost " + cost() + " break cost " + getTotalHardnessOfBlocksToBreak();
    }
}
