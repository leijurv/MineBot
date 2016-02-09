/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import static minebot.MineBot.tickNumber;
import minebot.pathfinding.goals.GoalComposite;
import minebot.pathfinding.goals.GoalGetToBlock;
import minebot.util.ChatCommand;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.inventory.Slot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author avecowa
 */
public class AnotherStealer {

    public static ArrayList<BlockPos> alreadyStolenFrom = new ArrayList<BlockPos>();
    public static boolean chestStuff = false;
    public static boolean stuff = false;
    public static BlockPos current = null;
    public static boolean dropitem = false;
    private static final Block CHEST = Block.getBlockFromName("chest");
    public static boolean holdShift = false;

    public static void onTick() {
        //try{
        if (MineBot.isThereAnythingInProgress || MineBot.currentPath != null) {
            System.out.println(MineBot.currentPath);
            return;
        }
        if (stuff) {
            stuff = false;
            ArrayList<BlockPos> chests = (ArrayList<BlockPos>) (Memory.getMemory(Block.getBlockFromName("chest")).knownPositions.clone());
            chests.removeAll(alreadyStolenFrom);
            if (chests.isEmpty()) {
                return;
            }
            BlockPos[] goals = GoalGetToBlock.ajacentBlocks(chests.get(0));
            for (int i = 1; i < chests.size(); i++) {
                goals = Autorun.concat(goals, GoalGetToBlock.ajacentBlocks(chests.get(i)));
            }
            MineBot.goal = new GoalComposite(goals);
            ChatCommand.path("path");
            return;
        }
        BlockPos near = getAjacentChest();
        if (near == null) {
            stuff = true;
            return;
        }
        if (near.equals(MineBot.whatAreYouLookingAt())) {
            if (chestStuff) {
                EntityPlayerSP player = Minecraft.theMinecraft.thePlayer;
                WorldClient world = Minecraft.theMinecraft.theWorld;
                if (Minecraft.theMinecraft.currentScreen == null) {
                    chestStuff = false;
                    return;
                }
                if (!(Minecraft.theMinecraft.currentScreen instanceof GuiChest)) {
                    player.closeScreen();
                    chestStuff = false;
                    return;
                }
                GuiChest contain = (GuiChest) Minecraft.theMinecraft.currentScreen;
                Slot slot = getFilledSlot(contain);
                if (slot != null) {
                    contain.shiftClick(slot.slotNumber);
                    return;
                }
                player.closeScreen();
                alreadyStolenFrom.add(near);
                return;

            }
            chestStuff = true;
            MineBot.isRightClick = true;
            current = MineBot.whatAreYouLookingAt();
            return;
        }
        LookManager.lookAtBlock(near, true);
        return;
    }

    public static BlockPos getAjacentChest() {
        BlockPos[] pos = GoalGetToBlock.ajacentBlocks(Minecraft.theMinecraft.thePlayer.getPosition0());
        WorldClient w = Minecraft.theMinecraft.theWorld;
        for (BlockPos p : pos) {
            if (!alreadyStolenFrom.contains(p) && w.getBlockState(p).getBlock().equals(CHEST)) {
                return p;
            }
        }
        return null;
    }

    public static Slot getFilledSlot(GuiChest chest) {
        for (int i = 0; i < chest.lowerChestInventory.getSizeInventory(); i++) {
            if (chest.lowerChestInventory.getStackInSlot(i) != null) {
                return chest.inventorySlots.getSlotFromInventory(chest.lowerChestInventory, i);
            }
        }
        return null;
    }
}
