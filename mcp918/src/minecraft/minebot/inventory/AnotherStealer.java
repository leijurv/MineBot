/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.inventory;

import minebot.ui.LookManager;
import minebot.util.Memory;
import java.util.ArrayList;
import minebot.MineBot;
import minebot.movement.MovementManager;
import minebot.pathfinding.goals.GoalComposite;
import minebot.pathfinding.goals.GoalGetToBlock;
import minebot.util.Autorun;
import minebot.util.ChatCommand;
import minebot.util.ChatCommand;
import minebot.util.Manager;
import minebot.util.Manager;
import minebot.util.Memory;
import minebot.util.Out;
import minebot.util.Out;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
            ArrayList<BlockPos> chests = Memory.closest(100, "chest");
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
                Out.gui("BAD GUI", Out.Mode.Debug);
                positionArmor = false;
                return;
            }
            Out.gui("Position Armor:" + positionSlot, Out.Mode.Debug);
            if (positionStatus == 0) {
                Container inv = Minecraft.theMinecraft.thePlayer.inventoryContainer;
                Out.gui("Position Status 0:" + inv.inventorySlots.size(), Out.Mode.Debug);
                for (int i = positionSlot; i < 45; i++) {
                    Out.gui((inv.getSlot(i).getHasStack() ? inv.getSlot(i).getStack().getItem().toString() : "NULL STACK") + " :" + i, Out.Mode.Debug);
                    if (inv.getSlot(i).getHasStack() && inv.getSlot(i).getStack().getItem() instanceof ItemArmor) {
                        Out.gui("ITEM IS ARMOR", Out.Mode.Debug);
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
                Out.gui("CHEST STUFF", Out.Mode.Debug);
                EntityPlayerSP player = Minecraft.theMinecraft.thePlayer;
                WorldClient world = Minecraft.theMinecraft.theWorld;
                if (Minecraft.theMinecraft.currentScreen == null) {
                    chestStuff = false;
                    Out.gui("NULL GUI", Out.Mode.Debug);
                    return;
                }
                if (!(Minecraft.theMinecraft.currentScreen instanceof GuiChest)) {
                    Out.gui("NOT CHEST GUI", Out.Mode.Debug);
                    return;
                }
                GuiChest contain = (GuiChest) Minecraft.theMinecraft.currentScreen;
                Slot slot = getFilledSlot(contain);
                Out.gui(slot == null ? "null slot" : slot.getHasStack() ? slot.getStack().getItem().toString() : "empty slot", Out.Mode.Debug);
                if (slot == null) {
                    Out.gui("CLOSING THE SCREEN", Out.Mode.Debug);
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
            Out.gui("NO CHEST STUFF", Out.Mode.Debug);
            chestStuff = true;
            MovementManager.isRightClick = true;
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
