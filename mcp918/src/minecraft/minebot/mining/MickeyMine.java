/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.mining;

import java.util.ArrayList;
import minebot.LookManager;
import minebot.MineBot;
import minebot.pathfinding.actions.Action;
import minebot.pathfinding.goals.GoalBlock;
import minebot.pathfinding.goals.GoalTwoBlocks;
import minebot.pathfinding.goals.GoalYLevel;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.world.chunk.Chunk;

/**
 *
 * @author galdara
 */
public class MickeyMine {
    static ArrayList<Block> goalBlocks = null;
    static boolean isGoingToMine = false;
    static boolean isMining = false;
    //static boolean seesBlock = false;
    static EnumFacing miningFacing = EnumFacing.EAST;
    static ArrayList<Tuple<Integer, Integer>> diamondChunks = new ArrayList<Tuple<Integer, Integer>>();
    //static BlockPos currentlyMining = null;
    static ArrayList<BlockPos> hasBeenMined = new ArrayList<BlockPos>();
    static ArrayList<BlockPos> needsToBeMined = new ArrayList<BlockPos>();
    static ArrayList<BlockPos> priorityNeedsToBeMined = new ArrayList<BlockPos>();
    static ArrayList<Tuple<Integer, Integer>> chunkHasDiamonds = new ArrayList<Tuple<Integer, Integer>>();
    static BlockPos branchPosition = null;
    static final String[] ores = {"diamond", "iron", "coal", "gold", "redstone", "emerald", "lit_redstone"};
    static final boolean[] enabled = {true, true, false, true, true, true, true};
    static boolean mightNeedToGoBackToPath = false;
    public static void clear() {
        isGoingToMine = false;
        isMining = false;
        needsToBeMined.clear();
        priorityNeedsToBeMined.clear();
        branchPosition = null;
        mightNeedToGoBackToPath = false;
    }
    public static void notifyFullness(String item, boolean isFull) {
        if (item.equals("stone")) {
            return;
        }
        boolean up = false;
        for (int i = 0; i < ores.length; i++) {
            if (ores[i].endsWith(item)) {
                if (enabled[i] == isFull) {
                    GuiScreen.sendChatMessage((isFull ? "is full" : "not full") + " of " + item + " so therefore " + ores[i], true);
                    enabled[i] = !isFull;
                    up = true;
                }
            }
        }
        if (up) {
            calculateGoal();
        }
    }
    public static void toggleOre(String ore) {
        String lower = ore.toLowerCase();
        if (lower.trim().length() == 0) {
            for (int i = 0; i < ores.length; i++) {
                GuiScreen.sendChatMessage(ores[i] + ": " + enabled[i], true);
            }
            return;
        }
        boolean m = false;
        for (int i = 0; i < ores.length; i++) {
            if (!ores[i].contains(lower)) {
                GuiScreen.sendChatMessage(ores[i] + ": " + enabled[i], true);
                continue;
            }
            m = true;
            enabled[i] = !enabled[i];
            GuiScreen.sendChatMessage(ores[i] + ": " + enabled[i] + " (I toggled this one just now)", true);
        }
        if (m) {
            goalBlocks = new ArrayList<Block>();
            calculateGoal();
        }
    }
    public static void calculateGoal() {
        goalBlocks = new ArrayList<Block>();
        for (int i = 0; i < ores.length; i++) {
            if (!enabled[i]) {
                continue;
            }
            String oreName = "minecraft:" + ores[i] + "_ore";
            Block block = Block.getBlockFromName(oreName);
            if (block == null) {
                GuiScreen.sendChatMessage(oreName + " doesn't exist bb", true);
                throw new NullPointerException(oreName + " doesn't exist bb");
            }
            goalBlocks.add(block);
        }
    }
    public static void doMine() {
        if (goalBlocks == null) {
            calculateGoal();
        }
        MineBot.clearMovement();
        System.out.println("Goal blocks: " + goalBlocks);
        System.out.println("priority: " + priorityNeedsToBeMined);
        System.out.println("needs to be mined: " + needsToBeMined);
        updatePriorityBlocksMined();
        updateBlocksMined();
        if (priorityNeedsToBeMined.isEmpty() && needsToBeMined.isEmpty()) {
            doBranchMine();
        } else if (!priorityNeedsToBeMined.isEmpty()) {
            doPriorityMine();
        } else {
            doNormalMine();
        }
    }
    public static Boolean torch() {
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        for (int i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                continue;
            }
            if (item.getItem().equals(Item.getByNameOrId("minecraft:torch"))) {
                p.inventory.currentItem = i;
                return true;
            }
        }
        int torchPosition = 0;
        for (int i = 9; i < inv.length; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                continue;
            }
            if (item.getItem().equals(Item.getByNameOrId("minecraft:torch"))) {
                torchPosition = i;
            }
        }
        if (torchPosition == 0) {
            return false;
        }
        int blankHotbarSpot = -1;
        for (int i = 0; i < 9; i++) {
            if (inv[i] == null) {
                blankHotbarSpot = i;
            }
        }
        if (blankHotbarSpot == -1) {
            return false;
        }
        if (Minecraft.theMinecraft.currentScreen == null) {
            MineBot.openInventory();
            return null;
        }
        if (Minecraft.theMinecraft.currentScreen instanceof GuiInventory) {
            GuiContainer contain = (GuiContainer) Minecraft.theMinecraft.currentScreen;
            contain.leftClick(torchPosition);
            contain.leftClick(blankHotbarSpot + 36);
            Minecraft.theMinecraft.thePlayer.closeScreen();
            return null;
        }
        return false;//we are in a gui other than inv
    }
    public static void doBranchMine() {
        if (branchPosition == null) {
            branchPosition = Minecraft.theMinecraft.thePlayer.getPosition0();
        }
        if (!branchPosition.equals(Minecraft.theMinecraft.thePlayer.getPosition0())) {
            GuiScreen.sendChatMessage("Should be at branch position " + branchPosition + " " + Minecraft.theMinecraft.thePlayer.getPosition0(), true);
            mightNeedToGoBackToPath = true;
        } else {
            Boolean b = torch();
            if (b == null || b) {
                if (LookManager.lookAtBlock(branchPosition.down(), true)) {
                    if (b != null && b) {
                        Minecraft.theMinecraft.rightClickMouse();
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
        int i;
        for (i = 0; i < 6 || diamondChunks.contains(tupleFromBlockPos(branchPosition.offset(miningFacing, i))); i++) {
            addNormalBlock(branchPosition.offset(miningFacing, i).up(), true);
            addNormalBlock(branchPosition.offset(miningFacing, i), true);
            System.out.println("branche" + i);
        }
        i--;
        GuiScreen.sendChatMessage("Branch distance " + i, true);
        BlockPos futureBranchPosition = branchPosition.offset(miningFacing, i);
        System.out.println("player reach: " + Minecraft.theMinecraft.playerController.getBlockReachDistance());
        for (int j = 1; j <= Math.ceil(Minecraft.theMinecraft.playerController.getBlockReachDistance()); j++) {
            addNormalBlock(futureBranchPosition.offset(miningFacing.rotateY(), j).up(), false);
        }
        for (int j = 1; j <= Math.ceil(Minecraft.theMinecraft.playerController.getBlockReachDistance()); j++) {
            addNormalBlock(futureBranchPosition.offset(miningFacing.rotateYCCW(), j).up(), false);
        }
        branchPosition = futureBranchPosition;
    }
    public static void doPriorityMine() {
        BlockPos toMine = priorityNeedsToBeMined.get(0);
        MineBot.goal = new GoalTwoBlocks(toMine);
        if (MineBot.currentPath == null && !MineBot.isPathFinding()) {
            MineBot.findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0(), false);
        } else {
            addNearby();
        }
    }
    public static void addNearby() {
        BlockPos playerFeet = Minecraft.theMinecraft.thePlayer.getPosition0();
        for (int x = playerFeet.getX() - 2; x <= playerFeet.getX() + 2; x++) {
            for (int y = playerFeet.getY() - 1; y <= playerFeet.getY() + 2; y++) {
                for (int z = playerFeet.getZ() - 2; z <= playerFeet.getZ() + 2; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (isGoalBlock(pos)) {
                        if (LookManager.couldIReach(pos)) {
                            addPriorityBlock(pos);
                        }
                    }
                }
            }
        }
    }
    public static void doNormalMine() {
        if (mightNeedToGoBackToPath) {
            MineBot.goal = new GoalBlock(branchPosition);
            if (MineBot.currentPath == null && !MineBot.isPathFinding()) {
                MineBot.findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0(), false);
                GuiScreen.sendChatMessage("Pathing back to branch", true);
            }
            if (Minecraft.theMinecraft.thePlayer.getPosition0().equals(branchPosition)) {
                mightNeedToGoBackToPath = false;
                GuiScreen.sendChatMessage("I'm back", true);
            }
            return;
        }
        addNearby();
        BlockPos toMine = needsToBeMined.get(0);
        if (LookManager.lookAtBlock(toMine, true)) {
            if (Action.avoidBreaking(toMine)) {
                miningFacing = miningFacing.rotateY();
                GuiScreen.sendChatMessage("Since I need to avoid breaking " + toMine + ", I'm rotating to " + miningFacing, true);
                needsToBeMined.clear();
                //.priorityNeedsToBeMined.clear();
            } else {
                MineBot.switchToBestTool();
                MineBot.isLeftClick = true;
                System.out.println("Looking");
                if (Minecraft.theMinecraft.thePlayer.getPosition0().equals(branchPosition)) {
                    System.out.println("IN position");
                    if (MineBot.whatAreYouLookingAt() == null) {
                        System.out.println("Can't see, going");
                        MineBot.forward = true;
                    }
                } else {
                    System.out.println("Going to position");
                    MineBot.moveTowardsBlock(branchPosition, false);
                    if (Minecraft.theMinecraft.thePlayer.getPosition0().getY() != branchPosition.getY()) {
                        GuiScreen.sendChatMessage("wrong Y coordinate", true);
                        mightNeedToGoBackToPath = true;
                    }
                }
            }
        }
    }
    public static void updateBlocksMined() {
        ArrayList<BlockPos> shouldBeRemoved = new ArrayList<BlockPos>();
        for (BlockPos isMined : needsToBeMined) {
            Block block = net.minecraft.client.Minecraft.theMinecraft.theWorld.getBlockState(isMined).getBlock();
            if (block.equals(Block.getBlockById(0)) || block.equals(Block.getBlockFromName("minecraft:torch"))) {
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
        boolean wasEmpty = priorityNeedsToBeMined.isEmpty();
        ArrayList<BlockPos> shouldBeRemoved = new ArrayList<BlockPos>();
        for (BlockPos isMined : priorityNeedsToBeMined) {
            Block block = net.minecraft.client.Minecraft.theMinecraft.theWorld.getBlockState(isMined).getBlock();
            if (block.equals(Block.getBlockById(0)) || block.equals(Block.getBlockFromName("minecraft:torch"))) {
                hasBeenMined.add(isMined);
                shouldBeRemoved.add(isMined);
                updateBlocks(isMined);
            }
        }
        for (BlockPos needsRemoval : shouldBeRemoved) {
            priorityNeedsToBeMined.remove(needsRemoval);
        }
        if (priorityNeedsToBeMined.isEmpty() && !wasEmpty) {
            mightNeedToGoBackToPath = true;
            if (!chunkHasDiamonds.isEmpty()) {
                for (Tuple<Integer, Integer> shouldAdd : chunkHasDiamonds) {
                    if (!diamondChunks.contains(shouldAdd)) {
                        diamondChunks.add(shouldAdd);
                    }
                }
                chunkHasDiamonds.clear();
            }
        }
    }
    public static void updateBlocks(BlockPos blockPos) {
        for (int i = 0; i < 4; i++) {
            System.out.println(blockPos.offset(miningFacing));
        }
        addPriorityBlock(blockPos.north());
        addPriorityBlock(blockPos.south());
        addPriorityBlock(blockPos.east());
        addPriorityBlock(blockPos.west());
        addPriorityBlock(blockPos.up());
        addPriorityBlock(blockPos.down());
    }
    public static boolean addNormalBlock(BlockPos blockPos, boolean mainBranch) {
        if (!needsToBeMined.contains(blockPos)) {
            if (Action.avoidBreaking(blockPos) && mainBranch) {//who gives a crap if a side branch will hit lava? lol
                GuiScreen.sendChatMessage("Uh oh, lava nearby", true);
                miningFacing = miningFacing.rotateY();
                return false;
            }
            needsToBeMined.add(blockPos);
            return true;
        }
        return false;
    }
    public static boolean addPriorityBlock(BlockPos blockPos) {
        if (!priorityNeedsToBeMined.contains(blockPos) && isGoalBlock(blockPos)) {
            if (Action.avoidBreaking(blockPos)) {
                GuiScreen.sendChatMessage("Can't break " + Minecraft.theMinecraft.theWorld.getBlockState(blockPos).getBlock() + " at " + blockPos + " because it's near lava", true);
                return false;
            }
            priorityNeedsToBeMined.add(blockPos);
            if (Minecraft.theMinecraft.theWorld.getBlockState(blockPos).getBlock().equals(Block.getBlockFromName("minecraft:diamond_ore"))) {
                chunkHasDiamonds.add(tupleFromBlockPos(blockPos));
            }
            return true;
        }
        return false;
    }
    public static void tick() {
        System.out.println("mickey" + isGoingToMine + " " + isMining);
        if (!isGoingToMine && !isMining) {
            if (MineBot.currentPath == null) {
                MineBot.goal = new GoalYLevel(6);
                MineBot.findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0(), true);
                isGoingToMine = true;
            }
        }
        if (isGoingToMine && Minecraft.theMinecraft.thePlayer.getPosition0().getY() == 6) {
            isGoingToMine = false;
            isMining = true;
        }
        if (isMining) {
            doMine();
        }
        System.out.println("mickey done");
    }
    public static boolean isGoalBlock(BlockPos blockPos) {
        return isGoalBlock(Minecraft.theMinecraft.theWorld.getBlockState(blockPos).getBlock());
    }
    public static boolean isGoalBlock(Block block) {
        return goalBlocks.contains(block);
    }
    public static boolean isNull(Object object) {
        try {
            object.toString();
            return false;
        } catch (NullPointerException ex) {
            return true;
        }
    }
    public static Chunk chunkFromTuple(Tuple<Integer, Integer> tuple) {
        return Minecraft.theMinecraft.theWorld.getChunkFromChunkCoords(tuple.getFirst(), tuple.getSecond());
    }
    public static Tuple<Integer, Integer> tupleFromChunk(Chunk chunk) {
        return new Tuple(chunk.xPosition, chunk.zPosition);
    }
    public static Tuple<Integer, Integer> tupleFromBlockPos(BlockPos blockPos) {
        return tupleFromChunk(Minecraft.theMinecraft.theWorld.getChunkFromBlockCoords(blockPos));
    }
}
