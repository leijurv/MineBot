/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.HashMap;
import minebot.mining.MickeyMine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 *
 * @author leijurv
 */
public class InventoryManager {
    static HashMap<String, Integer> maximumAmounts = null;
    static HashMap<String, Integer> minimumAmounts = null;
    public static void initMax() {
        maximumAmounts = new HashMap();
        minimumAmounts = new HashMap();
        add("cobblestone", 128, 64);
        add("coal", 128, 64);
        add("redstone", 64, 32);
        add("stone", 64, 32);
        add("dirt", 128, 64);
    }
    public static void add(String itemName, int max, int min) {
        Item item = Item.getByNameOrId("minecraft:" + itemName);
        if (item == null) {
            GuiScreen.sendChatMessage(itemName + " doesn't exist", true);
            throw new NullPointerException(itemName + " doesn't exist");
        }
        maximumAmounts.put(itemName, max);
        minimumAmounts.put(itemName, min);
    }
    public static void onTick() {
        if (maximumAmounts == null) {
            initMax();
        }
        HashMap<Item, Integer> amounts = countItems();
        boolean openedInvYet = false;
        for (String itemName : maximumAmounts.keySet()) {
            Item item = Item.getByNameOrId("minecraft:" + itemName);
            if (amounts.get(item) == null) {
                amounts.put(item, 0);
            }
            System.out.println(amounts.get(item));
            int toThrowAway = amounts.get(item) > maximumAmounts.get(itemName) ? amounts.get(item) - minimumAmounts.get(itemName) : 0;
            if (amounts.get(item) < minimumAmounts.get(itemName)) {
                MickeyMine.notifyFullness(itemName, false);
            }
            if (amounts.get(item) > ((minimumAmounts.get(itemName) + maximumAmounts.get(itemName)) / 2)) {
                MickeyMine.notifyFullness(itemName, true);
            }
            if (toThrowAway <= 0) {
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
                    c.leftClick(i);
                    if (is.stackSize <= toThrowAway) {
                        toThrowAway -= is.stackSize;
                        c.leftClickOutOfGui();
                    } else {
                        for (int j = 0; j < toThrowAway; j++) {
                            c.rightClickOutOfGui();
                        }
                        c.leftClick(i);
                        toThrowAway = 0;
                    }
                    if (toThrowAway <= 0) {
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
