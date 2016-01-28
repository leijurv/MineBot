/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.mining;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import minebot.MineBot;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

/**
 *
 * @author galdara
 */
public class MickeyMine {
    ArrayList<Block> goalBlocks;
    static boolean isGoingToMine = false;
    static boolean isMining = false;
    static boolean seesBlock = false;
    static int cardinalDirection = 0;
    ArrayList<Chunk> foundChunks = new ArrayList<Chunk>();
    static BlockPos topBlock = null;
    static BlockPos bottomBlock = null;
    static Queue<BlockPos> needsToBeMined = new ConcurrentLinkedQueue<BlockPos>();
    static Deque<BlockPos> priorityNeedsToBeMined = new LinkedList<BlockPos>();
    public MickeyMine(ArrayList<Block> goalBlocks) {
        this.goalBlocks = goalBlocks;
    }
    public static void doMine() {
        MineBot.lookAtBlock(Minecraft.theMinecraft.thePlayer.getPosition().add(1, 1, 0), seesBlock);
        if (Minecraft.theMinecraft.thePlayer.getPosition().add(1, 1, 0).equals(MineBot.whatAreYouLookingAt())) {
        }
        Minecraft.theMinecraft.theWorld.getChunkFromChunkCoords(Minecraft.theMinecraft.thePlayer.chunkCoordX, Minecraft.theMinecraft.thePlayer.chunkCoordZ);
    }
    public static void tick() {
        if (!isGoingToMine && !isMining) {
            MineBot.getToY(6);
        }
        if (isGoingToMine && Minecraft.theMinecraft.thePlayer.getPosition().getY() == 6) {
            isGoingToMine = false;
            isMining = true;
        }
        if (isMining) {
            doMine();
        }
    }
    public boolean isGoalBlock(BlockPos blockPos) {
        return isGoalBlock(Minecraft.theMinecraft.theWorld.getBlockState(blockPos).getBlock());
    }
    public boolean isGoalBlock(Block block) {
        return goalBlocks.contains(block);
    }
    public void buildOreVein(ArrayList<BlockPos> vein, BlockPos foundBlock) {
        int startLength = vein.size();
        if (!vein.contains(foundBlock.north()) && isGoalBlock(foundBlock.north())) {
            vein.add(foundBlock.north());
        }
        if (!vein.contains(foundBlock.south()) && isGoalBlock(foundBlock.north())) {
            vein.add(foundBlock.south());
            buildOreVein(vein, foundBlock.south());
        }
        if (!vein.contains(foundBlock.east()) && isGoalBlock(foundBlock.north())) {
            vein.add(foundBlock.east());
            buildOreVein(vein, foundBlock.east());
        }
        if (!vein.contains(foundBlock.west()) && isGoalBlock(foundBlock.north())) {
            vein.add(foundBlock.west());
            buildOreVein(vein, foundBlock.west());
        }
        if (!vein.contains(foundBlock.up()) && isGoalBlock(foundBlock.north())) {
            vein.add(foundBlock.up());
            buildOreVein(vein, foundBlock.up());
        }
        if (!vein.contains(foundBlock.down()) && isGoalBlock(foundBlock.north())) {
            vein.add(foundBlock.down());
            buildOreVein(vein, foundBlock.down());
        }
    }
}
