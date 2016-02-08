/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.Random;
import minebot.util.CraftingTask;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
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
    static final int WOOD_AMT = 16;//triggers stopping
    static final int MIN_WOOD_AMT = 5;//triggers getting more
    static boolean didPlace = false;
    public static void tick() {
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
        CraftingTask craftingTableTask = CraftingTask.findOrCreateCraftingTask(new ItemStack(Item.getByNameOrId("minecraft:crafting_table"), 0));
        if (craftingTableTask.currentlyCrafting().stackSize < 1) {
            craftingTableTask.increaseNeededAmount(1);
            return;
        }
        if (!putCraftingTableOnHotBar()) {
            return;
        }
        System.out.println("Ready to place!");
        if (!didPlace) {
            LookManager.lookAtBlock(Minecraft.theMinecraft.thePlayer.getPosition0().down(), true);
            MineBot.isRightClick = true;
            MineBot.jumping = true;
            BlockPos looking = MineBot.whatAreYouLookingAt();
            if (looking == null) {
                return;
            }
            Block current = Minecraft.theMinecraft.theWorld.getBlockState(looking).getBlock();
            if (current.equals(Block.getBlockFromItem(Item.getByNameOrId("minecraft:crafting_table")))) {
                GuiScreen.sendChatMessage("Did place");
                didPlace = true;
            }
            return;
        }
        System.out.println("Crafting table is there");
    }
    public static boolean putCraftingTableOnHotBar() {//shamelessly copied from MickeyMine.torch()
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        for (int i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                continue;
            }
            if (Item.getByNameOrId("minecraft:crafting_table").equals(item.getItem())) {
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
            if (Item.getByNameOrId("minecraft:crafting_table").equals(item.getItem())) {
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
            blankHotbarSpot = (new Random().nextInt(8) + 1);
        }
        if (Minecraft.theMinecraft.currentScreen == null) {
            MineBot.openInventory();
            return false;
        }
        if (Minecraft.theMinecraft.currentScreen instanceof GuiInventory) {
            GuiContainer contain = (GuiContainer) Minecraft.theMinecraft.currentScreen;
            contain.leftClick(torchPosition);
            contain.leftClick(blankHotbarSpot + 36);
            contain.leftClick(torchPosition);
            Minecraft.theMinecraft.thePlayer.closeScreen();
        }
        return false;
    }
    public static int countWood_PHRASING() {
        Item log1 = Item.getItemFromBlock(Block.getBlockFromName("minecraft:log"));
        Item log2_NOTCH_IS_A_BAD_PROGRAMMER = Item.getItemFromBlock(Block.getBlockFromName("minecraft:log2"));
        int count = 0;
        for (ItemStack stack : Minecraft.theMinecraft.thePlayer.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }
            if (stack.getItem().equals(log1) || stack.getItem().equals(log2_NOTCH_IS_A_BAD_PROGRAMMER)) {
                count += stack.stackSize;
            }
        }
        return count;
    }
}
