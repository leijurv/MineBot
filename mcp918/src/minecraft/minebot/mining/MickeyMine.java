/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.mining;

import java.util.ArrayList;
import minebot.MineBot;
import minebot.pathfinding.GoalBlock;
import minebot.pathfinding.GoalYLevel;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;

/**
 *
 * @author galdara
 */
public class MickeyMine {
    static ArrayList<Block> goalBlocks;
    static boolean isGoingToMine = false;
    static boolean isMining = false;
    static boolean seesBlock = false;
    static EnumFacing miningFacing = EnumFacing.EAST;
    ArrayList<Chunk> foundChunks = new ArrayList<Chunk>();
    static BlockPos currentlyMining = null;
    static ArrayList<BlockPos> hasBeenMined = new ArrayList<BlockPos>();
    static ArrayList<BlockPos> needsToBeMined = new ArrayList<BlockPos>();
    static ArrayList<BlockPos> priorityNeedsToBeMined = new ArrayList<BlockPos>();
    static Boolean branching = null;
    static BlockPos branchPosition = null;
    public MickeyMine(ArrayList<Block> goalBlocks) {
        this.goalBlocks = goalBlocks;
    }
    public static void doMine() {
        updatePriorityBlocksMined();
        updateBlocksMined();
        if (priorityNeedsToBeMined.isEmpty() && needsToBeMined.isEmpty()) {
            doBranchMine();
        } else if (!priorityNeedsToBeMined.isEmpty()) {
        }
//        if(branching == null) {
//                MineBot.lookAtBlock(Minecraft.theMinecraft.thePlayer.getPosition0().offset(miningFacing).up(), seesBlock);
//
//        } else if(Boolean.TRUE.equals(branching)) {
//
//        } else if(Boolean.FALSE.equals(branching)) {
//
//        } else {
//            throw new IllegalStateException("Branching must be null, true, or false.");
//        }
//        MineBot.lookAtBlock(Minecraft.theMinecraft.thePlayer.getPosition0().offset(miningFacing).up(), seesBlock);
//        if (Minecraft.theMinecraft.thePlayer.getPosition0().offset(miningFacing).up().equals(MineBot.whatAreYouLookingAt())) {
//
//        }
        Minecraft.theMinecraft.theWorld.getChunkFromChunkCoords(Minecraft.theMinecraft.thePlayer.chunkCoordX, Minecraft.theMinecraft.thePlayer.chunkCoordZ);
    }
    public static void doBranchMine() {
        if (branchPosition == null) {
            branchPosition = Minecraft.theMinecraft.thePlayer.getPosition0();
        }
        for (int i = 0; i < 6; i++) {
            addNormalBlock(branchPosition.offset(miningFacing, i).up());
            addNormalBlock(branchPosition.offset(miningFacing, i));
        }
        for (int i = 0; i < (double) Minecraft.theMinecraft.playerController.getBlockReachDistance(); i++) {
            addNormalBlock(branchPosition.offset(miningFacing.rotateY()).up());
        }
        for (int i = 0; i < (double) Minecraft.theMinecraft.playerController.getBlockReachDistance(); i++) {
            addNormalBlock(branchPosition.offset(miningFacing.rotateYCCW()).up());
        }
        branchPosition = branchPosition.offset(miningFacing, 6);
    }
    public static void doPriorityMine() {
        BlockPos toMine = priorityNeedsToBeMined.get(0);
        MineBot.goal = new GoalBlock(toMine);
        if (MineBot.currentPath == null && !MineBot.isPathFinding()) {
            MineBot.findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0());
        }
    }
    public static void doNormalMine() {
        BlockPos toMine = needsToBeMined.get(0);
        if (MineBot.lookAtBlock(toMine, true)) {
            MineBot.switchToBestTool();
            MineBot.isLeftClick = true;
            MineBot.forward = true;
        }
    }
    public static void updateBlocksMined() {
        ArrayList<BlockPos> shouldBeRemoved = new ArrayList<BlockPos>();
        for (BlockPos isMined : needsToBeMined) {
            if (net.minecraft.client.Minecraft.theMinecraft.theWorld.getBlockState(isMined).getBlock().equals(Block.getBlockById(0))) {
                hasBeenMined.add(isMined);
                shouldBeRemoved.add(isMined);
                updateBlocks(isMined);
            }
        }
        for (BlockPos needsRemoval : shouldBeRemoved) {
            needsToBeMined.remove(needsRemoval);
        }
    }
    public static void updatePriorityBlocksMined() {
        ArrayList<BlockPos> shouldBeRemoved = new ArrayList<BlockPos>();
        for (BlockPos isMined : priorityNeedsToBeMined) {
            if (net.minecraft.client.Minecraft.theMinecraft.theWorld.getBlockState(isMined).getBlock().equals(Block.getBlockById(0))) {
                hasBeenMined.add(isMined);
                shouldBeRemoved.add(isMined);
                updateBlocks(isMined);
            }
        }
        for (BlockPos needsRemoval : shouldBeRemoved) {
            priorityNeedsToBeMined.remove(needsRemoval);
        }
    }
    public static void updateBlocks(BlockPos blockPos) {
        if (isGoalBlock(blockPos.north())) {
            addPriorityBlock(blockPos.north());
        }
        if (isGoalBlock(blockPos.south())) {
            addPriorityBlock(blockPos.south());
        }
        if (isGoalBlock(blockPos.east())) {
            addPriorityBlock(blockPos.east());
        }
        if (isGoalBlock(blockPos.west())) {
            addPriorityBlock(blockPos.west());
        }
        if (isGoalBlock(blockPos.up())) {
            addPriorityBlock(blockPos.up());
        }
        if (isGoalBlock(blockPos.down())) {
            addPriorityBlock(blockPos.down());
        }
    }
    public static boolean addNormalBlock(BlockPos blockPos) {
        if (!needsToBeMined.contains(blockPos)) {
            needsToBeMined.add(blockPos);
            return true;
        }
        return false;
    }
    public static boolean addPriorityBlock(BlockPos blockPos) {
        if (!priorityNeedsToBeMined.contains(blockPos)) {
            priorityNeedsToBeMined.add(blockPos);
            return true;
        }
        return false;
    }
    public static void tick() {
        if (!isGoingToMine && !isMining) {
            if (MineBot.currentPath == null) {
                MineBot.goal = new GoalYLevel(6);
                MineBot.findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0());
                isGoingToMine = true;
            }
        }
        if (isGoingToMine && Minecraft.theMinecraft.thePlayer.getPosition0().getY() <= 6) {
            isGoingToMine = false;
            isMining = true;
        }
        if (isMining) {
            doMine();
        }
    }
    public static boolean isGoalBlock(BlockPos blockPos) {
        return isGoalBlock(Minecraft.theMinecraft.theWorld.getBlockState(blockPos).getBlock());
    }
    public static boolean isGoalBlock(Block block) {
        return goalBlocks.contains(block);
    }
    public static void buildOreVein(ArrayList<BlockPos> vein, BlockPos foundBlock) {
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
    public static boolean isNull(Object object) {
        try {
            object.toString();
            return false;
        } catch (NullPointerException ex) {
            return true;
        }
    }
}
