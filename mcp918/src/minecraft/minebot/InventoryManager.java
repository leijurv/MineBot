/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.HashMap;
import minebot.mining.MickeyMine;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

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
        addBounds("cobblestone", 128, 64);
        addBounds("coal", 128, 64);
        addBounds("redstone", 64, 32);
        addBounds("stone", 64, 32);
        addBounds("dirt", 128, 64);
    }
    public static void addBounds(String itemName, int max, int min) {
        Item item = Item.getByNameOrId("minecraft:" + itemName);
        if (item == null) {
            GuiScreen.sendChatMessage(itemName + " doesn't exist", true);
            throw new NullPointerException(itemName + " doesn't exist");
        }
        maximumAmounts.put(itemName, max);
        minimumAmounts.put(itemName, min);
    }
    static boolean openedInvYet = false;
    public static int bestPickaxe() {
        ItemStack[] stacks = Minecraft.theMinecraft.thePlayer.inventory.mainInventory;
        int bestPosition = -1;
        float bestStrength = Float.MIN_VALUE;
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();
            if (item instanceof ItemPickaxe) {
                ItemPickaxe pick = (ItemPickaxe) item;
                float strength = pick.getStrVsBlock(stack, Block.getBlockFromName("minecraft:stone"));
                if (strength > bestStrength) {
                    bestStrength = strength;
                    bestPosition = i;
                }
            }
        }
        return bestPosition;
    }
    public static int bestSword() {
        ItemStack[] stacks = Minecraft.theMinecraft.thePlayer.inventory.mainInventory;
        int bestPosition = -1;
        float bestStrength = Float.MIN_VALUE;
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();
            if (item instanceof ItemSword) {
                ItemSword sword = (ItemSword) item;
                float strength = sword.getDamageVsEntity();
                if (strength > bestStrength) {
                    bestStrength = strength;
                    bestPosition = i;
                }
            }
        }
        return bestPosition;
    }
    public static int find(Item... items) {
        ItemStack[] stacks = Minecraft.theMinecraft.thePlayer.inventory.mainInventory;
        int bestPosition = -1;
        int bestSize = 0;
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack == null) {
                continue;
            }
            for (Item it : items) {
                if (it.equals(stack.getItem())) {
                    if (stack.stackSize > bestSize) {
                        bestSize = stack.stackSize;
                        bestPosition = i;
                    }
                }
            }
        }
        return bestPosition;
    }
    public static boolean putItemInSlot(int hotbarslot, Item... items) {
        int currPos = find(items);
        ItemStack curr = Minecraft.theMinecraft.thePlayer.inventory.mainInventory[hotbarslot];
        if (curr != null) {
            for (Item item : items) {
                if (item.equals(curr.getItem())) {
                    return false;
                }
            }
        }
        if (currPos == -1) {
            return false;
        }
        if (currPos < 9) {
            currPos += 36;
        }
        if (!openedInvYet) {
            MineBot.slowOpenInventory();
            openedInvYet = true;
        }
        switchWithHotBar(currPos, hotbarslot);
        return true;
    }
    public static void onTick() {
        if (maximumAmounts == null) {
            initMax();
        }
        if (Minecraft.theMinecraft.currentScreen != null && !(Minecraft.theMinecraft.currentScreen instanceof GuiInventory)) {
            return;
        }
        int pickPos = bestPickaxe();
        if (pickPos > 0) {
            if (pickPos < 9) {
                pickPos += 36;
            }
            if (!openedInvYet) {
                MineBot.slowOpenInventory();
                openedInvYet = true;
            }
            switchWithHotBar(pickPos, 0);
            return;
        }
        int swordPos = bestSword();
        if (swordPos > 0) {
            if (swordPos < 9) {
                swordPos += 36;
            }
            if (!openedInvYet) {
                MineBot.slowOpenInventory();
                openedInvYet = true;
            }
            switchWithHotBar(swordPos, 0);
            return;
        }
        if (putItemInSlot(3, Item.getByNameOrId("minecraft:dirt"), Item.getByNameOrId("minecraft:cobblestone"))) {
            return;
        }
        if (putItemInSlot(1, Item.getByNameOrId("minecraft:torch"))) {
            return;
        }
        HashMap<Item, Integer> amounts = countItems();
        for (String itemName : maximumAmounts.keySet()) {
            Item item = Item.getByNameOrId("minecraft:" + itemName);
            if (amounts.get(item) == null) {
                amounts.put(item, 0);
            }
            //System.out.println(amounts.get(item));
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
                MineBot.slowOpenInventory();
                openedInvYet = true;
            }
            GuiContainer c = (GuiContainer) Minecraft.theMinecraft.currentScreen;
            if (c == null) {
                GuiScreen.sendChatMessage("Null container");
                openedInvYet = false;
                return;
            }
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
                    if (is.stackSize <= toThrowAway) {
                        toThrowAway -= is.stackSize;
                        dropAll(i);
                        return;
                    } else {
                        for (int j = 0; j < toThrowAway; j++) {
                            dropOne(i);
                            return;
                        }
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
            openedInvYet = false;
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
    public static void switchWithHotBar(int slotNumber, int hotbarPosition) {
        GuiContainer contain = (GuiContainer) Minecraft.theMinecraft.currentScreen;
        contain.sketchyMouseClick(slotNumber, hotbarPosition, 2);
    }
    public static void dropAll(int slotNumber) {
        GuiContainer contain = (GuiContainer) Minecraft.theMinecraft.currentScreen;
        contain.sketchyMouseClick(slotNumber, 1, 4);
    }
    public static void dropOne(int slotNumber) {
        GuiContainer contain = (GuiContainer) Minecraft.theMinecraft.currentScreen;
        contain.sketchyMouseClick(slotNumber, 0, 4);
    }
}
