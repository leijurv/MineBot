/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;
import minebot.util.CraftingTask;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
    public static void tick() {
        int wood = countWood_PHRASING();
        if (wood >= WOOD_AMT) {
            if (!gotWood_PHRASING) {
                GuiScreen.sendChatMessage("Done getting wood", true);
            }
            gotWood_PHRASING = true;
        }
        if (wood > MIN_WOOD_AMT) {
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
        }
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
