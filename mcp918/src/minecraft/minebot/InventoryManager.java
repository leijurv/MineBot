/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 * @author leijurv
 */
public class InventoryManager {
    static HashMap<Item, Integer> maximumAmounts = null;
    public static void initMax() {
        maximumAmounts = new HashMap();
        maximumAmounts.put(Item.getByNameOrId("minecraft:cobblestone"), 128);
        maximumAmounts.put(Item.getByNameOrId("minecraft:coal"), 128);
        maximumAmounts.put(Item.getByNameOrId("minecraft:redstone_dust"), 64);
        maximumAmounts.put(Item.getByNameOrId("minecraft:stone"), 64);
        maximumAmounts.put(Item.getByNameOrId("minecraft:dirt"), 128);
    }
    public static void onTick() {
        if (maximumAmounts == null) {
            initMax();
        }
        HashMap<Item, Integer> amounts = countItems();
        boolean openedInvYet = false;
        for (Item item : maximumAmounts.keySet()) {
            int max = maximumAmounts.get(item);
            if (amounts.get(item) == null) {
                continue;
            }
            int actualAmount = amounts.get(item);
            if (actualAmount <= max) {
                continue;
            }
            if (!openedInvYet) {
                MineBot.openInventory();
                openedInvYet = true;
            }
            GuiContainer c = (GuiContainer) Minecraft.theMinecraft.currentScreen;
            for (int i = 0; i < c.inventorySlots.inventorySlots.size(); i++) {
                Slot slot = c.inventorySlots.inventorySlots.get(i);
                if (slot == null) {
                    continue;
                }
                ItemStack is = slot.getStack();
                if (is == null) {
                    continue;
                }
                if (item.equals(is.getItem())) {
                    actualAmount -= is.stackSize;
                    c.sketchyMouseClick(i, 0, 0);
                    c.sketchyMouseClick(-999, 0, 0);
                    if (actualAmount <= max) {
                        break;
                    }
                }
            }
        }
        if (openedInvYet) {
            Minecraft.theMinecraft.thePlayer.closeScreen();
        }
    }
    public static HashMap<Item, Integer> countItems() {
        HashMap<Item, Integer> amounts = new HashMap();
        for (ItemStack is : Minecraft.theMinecraft.thePlayer.inventory.mainInventory) {
            if (is != null && is.getItem() != null) {
                if (amounts.get(is.getItem()) == null) {
                    amounts.put(is.getItem(), is.stackSize);
                } else {
                    amounts.put(is.getItem(), is.stackSize + amounts.get(is.getItem()));
                }
            }
        }
        return amounts;
    }
}
