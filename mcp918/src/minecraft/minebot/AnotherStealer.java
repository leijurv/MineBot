/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.ArrayList;
import minebot.pathfinding.goals.GoalComposite;
import minebot.pathfinding.goals.GoalGetToBlock;
import minebot.util.ChatCommand;
import minebot.util.Manager;
import minebot.util.Out;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 *
 * @author avecowa
 */
public class AnotherStealer extends Manager {
    protected static Manager newInstance() {
        return null;
    }
    public static ArrayList<BlockPos> alreadyStolenFrom = new ArrayList<BlockPos>();
    public static boolean chestStuff = false;
    public static boolean stuff = false;
    public static BlockPos current = null;
    private static final Block CHEST = Block.getBlockFromName("chest");
    private static boolean positionArmor = false;
    private static int positionSlot = 0;
    private static int positionStatus = 0;
    @Override
    public void onTick() {
        //try{
        if (invFull()) {
            ChatCommand.stealer("stealer");
            return;
        }
        if (MineBot.isThereAnythingInProgress || MineBot.currentPath != null) {
            Out.log(MineBot.currentPath);
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
            ChatCommand.path("path false");
            return;
        }
        if (positionArmor) {
            if (!(Minecraft.theMinecraft.currentScreen instanceof GuiInventory)) {
                GuiScreen.sendChatMessage("BAD GUI");
                positionArmor = false;
                return;
            }
            GuiScreen.sendChatMessage("Position Armor:" + positionSlot);
            if (positionStatus == 0) {
                Container inv = Minecraft.theMinecraft.thePlayer.inventoryContainer;
                GuiScreen.sendChatMessage("Position Status 0:" + inv.inventorySlots.size());
                for (int i = positionSlot; i < 45; i++) {
                    GuiScreen.sendChatMessage((inv.getSlot(i).getHasStack() ? inv.getSlot(i).getStack().getItem().toString() : "NULL STACK") + " :" + i);
                    if (inv.getSlot(i).getHasStack() && inv.getSlot(i).getStack().getItem() instanceof ItemArmor) {
                        GuiScreen.sendChatMessage("ITEM IS ARMOR");
                        ItemArmor armor = (ItemArmor) inv.getSlot(i).getStack().getItem();
                        if (inv.getSlot(armor.armorType).getHasStack() && ((ItemArmor) inv.getSlot(armor.armorType).getStack().getItem()).damageReduceAmount < armor.damageReduceAmount) {
                            positionSlot = i;
                            positionStatus = 1;
                            Minecraft.theMinecraft.playerController.windowClick(((GuiContainer) Minecraft.theMinecraft.currentScreen).inventorySlots.windowId, 103 - armor.armorType, 0, 1, Minecraft.theMinecraft.thePlayer);
                            return;
                        }
                    }
                }
                positionArmor = false;
                Minecraft.theMinecraft.thePlayer.closeScreen();
                return;
            }
            if (positionStatus == 1) {
                Minecraft.theMinecraft.playerController.windowClick(((GuiContainer) Minecraft.theMinecraft.currentScreen).inventorySlots.windowId, positionSlot, 0, 1, Minecraft.theMinecraft.thePlayer);
                positionStatus = 0;
                return;
            }
        }
        BlockPos near = getAjacentChest();
        if (near == null) {
            stuff = true;
            return;
        }
        if (near.equals(MineBot.whatAreYouLookingAt())) {
            if (chestStuff) {
                GuiScreen.sendChatMessage("CHEST STUFF");
                EntityPlayerSP player = Minecraft.theMinecraft.thePlayer;
                WorldClient world = Minecraft.theMinecraft.theWorld;
                if (Minecraft.theMinecraft.currentScreen == null) {
                    chestStuff = false;
                    GuiScreen.sendChatMessage("NULL GUI");
                    return;
                }
                if (!(Minecraft.theMinecraft.currentScreen instanceof GuiChest)) {
                    GuiScreen.sendChatMessage("NOT CHEST GUI");
                    return;
                }
                GuiChest contain = (GuiChest) Minecraft.theMinecraft.currentScreen;
                Slot slot = getFilledSlot(contain);
                GuiScreen.sendChatMessage(slot == null ? "null slot" : slot.getHasStack() ? slot.getStack().getItem().toString() : "empty slot");
                if (slot == null) {
                    GuiScreen.sendChatMessage("CLOSING THE SCREEN");
                    alreadyStolenFrom.add(near);
                    positionArmor = true;
                    positionSlot = 9;
                    positionStatus = 0;
                    MineBot.slowOpenInventory();
                    return;
                }
                contain.shiftClick(slot.slotNumber);
                return;
            }
            GuiScreen.sendChatMessage("NO CHEST STUFF");
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
    public static boolean invFull() {
        ItemStack[] inv = Minecraft.theMinecraft.thePlayer.inventory.mainInventory;
        for (ItemStack i : inv) {
            if (i == null) {
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onCancel() {
    }
    @Override
    protected void onStart() {
        alreadyStolenFrom = new ArrayList<BlockPos>();
        chestStuff = false;
        stuff = false;
        current = null;
        positionArmor = false;
        positionSlot = 0;
    }
}
