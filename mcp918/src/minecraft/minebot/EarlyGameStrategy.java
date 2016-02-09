/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import minebot.pathfinding.goals.GoalTwoBlocks;
import minebot.util.CraftingTask;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 * goals:
 *
 * get wood
 *
 * make a crafting table
 *
 * make a wooden pickaxe
 *
 * @author leijurv
 */
public class EarlyGameStrategy {
    static boolean gotWood_PHRASING = false;
    static final int WOOD_AMT = 64;//triggers stopping
    static final int MIN_WOOD_AMT = 16;//triggers getting more
    static final int DIRT_AMT = 32;
    static boolean didPlace = false;
    static boolean gotDirt = false;
    public static void tick() {
        if (!gotDirt) {
            int dirt = countDirt();
            if (dirt >= DIRT_AMT) {
                GuiScreen.sendChatMessage("Done getting dirt");
                gotDirt = true;
                return;
            }
            BlockPos closest = Memory.closest("dirt", "grass");
            if (closest == null) {
                GuiScreen.sendChatMessage("NO DIRT NEARBY. GOD DAMN IT");
                return;
            }
            MineBot.goal = new GoalTwoBlocks(closest);
            if (MineBot.currentPath == null && !MineBot.isThereAnythingInProgress) {
                MineBot.findPathInNewThread(false);
            }
            return;
        }
        int wood = countWood_PHRASING();
        if (wood >= WOOD_AMT) {
            if (!gotWood_PHRASING) {
                GuiScreen.sendChatMessage("Done getting wood", true);
            }
            gotWood_PHRASING = true;
        }
        if (wood < MIN_WOOD_AMT) {
            if (gotWood_PHRASING) {
                GuiScreen.sendChatMessage("Getting more wood", true);
            }
            gotWood_PHRASING = false;
        }
        if (!gotWood_PHRASING) {
            TreePuncher.tick();
            return;
        }
        boolean craftingTableInInventory = ensureCraftingDesired(Item.getByNameOrId("minecraft:crafting_table"), 1);
        boolean hasWooden = ensureCraftingDesired(Item.getByNameOrId("minecraft:wooden_pickaxe"), 1);
        ensureCraftingDesired(Item.getByNameOrId("minecraft:stone_pickaxe"), 1);
        if (hasWooden) {
            BlockPos closest = Memory.closest("stone");
            if (closest == null) {
                GuiScreen.sendChatMessage("NO STONE NEARBY. GOD DAMN IT");
                return;
            }
            MineBot.goal = new GoalTwoBlocks(closest);
            if (MineBot.currentPath == null && !MineBot.isThereAnythingInProgress) {
                MineBot.findPathInNewThread(false);
            }
        }
    }
    public static boolean ensureCraftingDesired(Item item, int quantity) {
        CraftingTask craftingTableTask = CraftingTask.findOrCreateCraftingTask(new ItemStack(item, 0));
        if (craftingTableTask.currentlyCrafting().stackSize < quantity) {
            craftingTableTask.increaseNeededAmount(quantity - craftingTableTask.currentlyCrafting().stackSize);
        }
        return craftingTableTask.alreadyHas() >= quantity;
    }
    public static int countWood_PHRASING() {
        Item log1 = Item.getItemFromBlock(Block.getBlockFromName("minecraft:log"));
        Item log2_NOTCH_IS_A_BAD_PROGRAMMER = Item.getItemFromBlock(Block.getBlockFromName("minecraft:log2"));
        int count = 0;
        for (ItemStack stack : Minecraft.theMinecraft.thePlayer.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }
            if (log1.equals(stack.getItem()) || log2_NOTCH_IS_A_BAD_PROGRAMMER.equals(stack.getItem())) {
                count += stack.stackSize;
            }
        }
        return count;
    }
    public static int countDirt() {
        Item dirt = Item.getItemFromBlock(Block.getBlockFromName("minecraft:dirt"));
        int count = 0;
        for (ItemStack stack : Minecraft.theMinecraft.thePlayer.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }
            if (dirt.equals(stack.getItem())) {
                count += stack.stackSize;
            }
        }
        return count;
    }
}
