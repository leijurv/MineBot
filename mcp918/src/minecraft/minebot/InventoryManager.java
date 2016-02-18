/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.HashMap;
import java.util.Random;
import minebot.mining.MickeyMine;
import minebot.util.Manager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class InventoryManager extends Manager {
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
    public static boolean place(int pos, Block check, Class<?> h) {
        ItemStack[] stacks = Minecraft.theMinecraft.thePlayer.inventory.mainInventory;
        int itemPos = -1;
        float bestStrength = Float.MIN_VALUE;
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();
            if (item.getClass() == h) {
                float strength = item.getStrVsBlock(stack, check);
                if (strength > bestStrength) {
                    bestStrength = strength;
                    itemPos = i;
                }
            }
        }
        if (itemPos == -1) {
            return false;
        }
        if (itemPos == pos) {
            return false;
        }
        if (itemPos < 9) {
            itemPos += 36;
        }
        if (!openedInvYet) {
            MineBot.slowOpenInventory();
            openedInvYet = true;
        }
        switchWithHotBar(itemPos, pos);
        return true;
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
    public static void randomize(int[] array, Random random) {
        for (int i = 0; i < array.length; i++) {
            int j = random.nextInt(array.length);
            int tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
    }
    protected void onTick() {
        if (maximumAmounts == null) {
            initMax();
        }
        if (Minecraft.theMinecraft.currentScreen != null && !(Minecraft.theMinecraft.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (openedInvYet && Minecraft.theMinecraft.currentScreen == null) {
            openedInvYet = false;
            return;
        }
        Random random = new Random(Minecraft.theMinecraft.thePlayer.getName().hashCode());
        int[] slots = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        randomize(slots, random);
        int ind = 0;
        if (place(slots[ind++], Block.getBlockFromName("stone"), ItemPickaxe.class)) {
            return;
        }
        if (placeSword(slots[ind++])) {
            return;
        }
        if (placeFood(slots[ind++])) {
            return;
        }
        if (place(slots[ind++], Block.getBlockFromName("log"), ItemAxe.class)) {
            return;
        }
        if (place(slots[ind++], Block.getBlockFromName("dirt"), ItemSpade.class)) {
            return;
        }
        if (putItemInSlot(slots[ind++], Item.getByNameOrId("minecraft:dirt"), Item.getByNameOrId("minecraft:cobblestone"))) {
            return;
        }
        if (putItemInSlot(slots[ind++], Item.getByNameOrId("minecraft:torch"))) {
            return;
        }
        if (putItemInSlot(slots[ind++], Item.getByNameOrId("minecraft:crafting_table"))) {
            return;
        }
        if (putItemInSlot(slots[ind++], Item.getByNameOrId("minecraft:furnace"))) {
            return;
        }
        BlockPos look = MineBot.whatAreYouLookingAt();
        boolean doThrowAway = true;
        if (look != null) {
            int xDiff = look.getX() - Minecraft.theMinecraft.thePlayer.getPosition0().getX();
            int zDiff = look.getZ() - Minecraft.theMinecraft.thePlayer.getPosition0().getZ();
            if (Math.abs(xDiff) + Math.abs(zDiff) <= 2) {
                doThrowAway = false;
            }
        }
        HashMap<Item, Integer> amounts = countItems();
        for (String itemName : maximumAmounts.keySet()) {
            if (!doThrowAway) {
                continue;
            }
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
            if (Minecraft.theMinecraft.currentScreen == null) {
                GuiScreen.sendChatMessage("Null container");
                openedInvYet = false;
                return;
            }
            int bestPos = -1;
            int bestSize = 0;
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
                    if (is.stackSize > bestSize && is.stackSize <= toThrowAway) {
                        bestSize = is.stackSize;
                        bestPos = i;
                    }
                }
            }
            if (bestPos != -1) {
                dropAll(bestPos);//throw away the largest stack that's smaller than toThrowAway, if it exists
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
    public static boolean placeSword(int slot) {
        ItemStack[] stacks = Minecraft.theMinecraft.thePlayer.inventory.mainInventory;
        int swordPos = -1;
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
                    swordPos = i;
                }
            }
        }
        if (swordPos == -1) {
            return false;
        }
        if (swordPos == slot) {
            return false;
        }
        if (swordPos < 9) {
            swordPos += 36;
        }
        if (!openedInvYet) {
            MineBot.slowOpenInventory();
            openedInvYet = true;
        }
        switchWithHotBar(swordPos, slot);
        return true;
    }
    public static boolean placeFood(int slot) {
        ItemStack[] stacks = Minecraft.theMinecraft.thePlayer.inventory.mainInventory;
        int foodPos = -1;
        float bestStrength = Float.MIN_VALUE;
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i];
            if (stack == null) {
                continue;
            }
            Item item = stack.getItem();
            if (item instanceof ItemFood) {
                ItemFood food = (ItemFood) item;
                float strength = food.getHealAmount(stack);
                if (strength > bestStrength) {
                    bestStrength = strength;
                    foodPos = i;
                }
            }
        }
        if (foodPos == -1) {
            return false;
        }
        if (foodPos == slot) {
            return false;
        }
        if (foodPos < 9) {
            foodPos += 36;
        }
        if (!openedInvYet) {
            MineBot.slowOpenInventory();
            openedInvYet = true;
        }
        switchWithHotBar(foodPos, slot);
        return true;
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
    @Override
    protected void onCancel() {
    }
    @Override
    protected void onStart() {
    }
    @Override
    protected boolean onEnabled(boolean enabled) {
        return MineBot.tickNumber % 10 == 0;
    }
}
