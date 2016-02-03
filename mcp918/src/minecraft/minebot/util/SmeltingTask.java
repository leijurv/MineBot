/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import minebot.MineBot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class SmeltingTask {
    private final Map<ItemStack, ItemStack> recipes = getRecipes();
    private final ItemStack toPutInTheFurnace;
    private BlockPos furnace = null;
    private boolean didIPutItInAlreadyPhrasing = false;
    public SmeltingTask(ItemStack desired) {
        toPutInTheFurnace = recipe(desired);
        if (toPutInTheFurnace == null) {
            String m = "Babe I can't smelt anyting to make " + desired;
            GuiScreen.sendChatMessage(m, true);
            throw new IllegalArgumentException(m);
        }
    }
    public void heyPleaseActuallyPutItInTheFurnaceNowOkay() {
        if (didIPutItInAlreadyPhrasing) {
            return;
        }
        didIPutItInAlreadyPhrasing = true;
        GuiFurnace contain = (GuiFurnace) Minecraft.theMinecraft.currentScreen;//I don't check this, because you should check this before calling it, and if you don't you deserve the ClassCastException
        furnace = MineBot.whatAreYouLookingAt();
        int desired = toPutInTheFurnace.stackSize;
        if (currentSize(contain) >= desired) {
            GuiScreen.sendChatMessage("done", true);
            return;
        }
        if (currentSize(contain) == -1) {
            GuiScreen.sendChatMessage("Furnace already in use", true);
            return;
        }
        int burnTicks = desired * 200;
        ArrayList<Item> burnableItems = new ArrayList<Item>();
        ArrayList<Integer> burnTimes = new ArrayList<Integer>();
        ArrayList<Integer> amountWeHave = new ArrayList<Integer>();
        ArrayList<Integer> amtNeeded = new ArrayList<Integer>();
        for (int i = 3; i < contain.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = contain.inventorySlots.inventorySlots.get(i);
            if (!slot.getHasStack()) {
                continue;
            }
            ItemStack in = slot.getStack();
            if (in == null) {
                continue;
            }
            Item item = in.getItem();
            int ind = burnableItems.indexOf(item);
            if (ind == -1) {
                int time = TileEntityFurnace.getItemBurnTime(in);
                if (time <= 0) {
                    GuiScreen.sendChatMessage(in + " isn't fuel, lol", true);
                    continue;
                }
                burnableItems.add(in.getItem());
                amountWeHave.add(in.stackSize);
                burnTimes.add(time);
                int numRequired = (int) Math.ceil(((double) burnTicks) / ((double) time));
                amtNeeded.add(numRequired);
            } else {
                amountWeHave.set(ind, amountWeHave.get(ind) + in.stackSize);
            }
        }
        for (int i = 0; i < burnableItems.size(); i++) {
            if (amountWeHave.get(i) < amtNeeded.get(i)) {
                GuiScreen.sendChatMessage("Not using fuel " + burnableItems.get(i) + " because not enough (have " + amountWeHave.get(i) + ", need " + amtNeeded.get(i) + ")", true);
                burnableItems.remove(i);
                amountWeHave.remove(i);
                amtNeeded.remove(i);
                burnTimes.remove(i);
                i--;
            }
        }
        if (burnableItems.isEmpty()) {
            GuiScreen.sendChatMessage("lol no fuel", true);
            return;
        }
        System.out.println(burnableItems);
        System.out.println(amountWeHave);
        System.out.println(amtNeeded);
        System.out.println(burnTimes);
        Item bestFuel = null;
        int bestAmt = Integer.MAX_VALUE;
        int bestExtra = Integer.MAX_VALUE;
        for (int i = 0; i < burnableItems.size(); i++) {
            int amt = amtNeeded.get(i);
            int extra = burnTimes.get(i) * amtNeeded.get(i) - burnTicks;
            if (extra < bestExtra || (extra == bestExtra && amt < bestAmt)) {
                bestAmt = amt;
                bestExtra = extra;
                bestFuel = burnableItems.get(i);
            }
        }
        GuiScreen.sendChatMessage("Using " + bestAmt + " items of " + bestFuel + ", which wastes " + bestExtra + " ticks of fuel.", true);
        for (int i = 3; i < contain.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = contain.inventorySlots.inventorySlots.get(i);
            if (!slot.getHasStack()) {
                continue;
            }
            ItemStack in = slot.getStack();
            if (in == null) {
                continue;
            }
            if (in.getItem().equals(toPutInTheFurnace.getItem())) {
                int currentSize = currentSize(contain);
                int amountHere = in.stackSize;
                int amountNeeded = desired - currentSize;
                if (currentSize == -1) {
                    GuiScreen.sendChatMessage("Furnace already in use", true);
                    return;
                }
                contain.sketchyMouseClick(i, 0, 0);
                if (amountNeeded >= amountHere) {
                    contain.sketchyMouseClick(0, 0, 0);
                } else {
                    for (int j = 0; j < amountNeeded; j++) {
                        contain.sketchyMouseClick(0, 1, 0);
                    }
                }
                contain.sketchyMouseClick(i, 0, 0);
                if (currentSize(contain) >= desired) {
                    GuiScreen.sendChatMessage("done", true);
                    return;
                }
            }
        }
        if (currentSize(contain) >= desired) {
            GuiScreen.sendChatMessage("done", true);
            return;
        }
        GuiScreen.sendChatMessage("Still need " + (desired - currentSize(contain)) + " items", true);
    }
    private int currentSize(GuiFurnace contain) {
        Slot slot = contain.inventorySlots.inventorySlots.get(0);
        if (!slot.getHasStack()) {
            return 0;
        }
        ItemStack in = slot.getStack();
        if (in == null) {
            return 0;
        }
        if (!in.getItem().equals(toPutInTheFurnace.getItem())) {
            return -1;
        }
        return in.stackSize;
    }
    private static ItemStack recipe(ItemStack desired) {
        for (Entry<ItemStack, ItemStack> recipe : getRecipes().entrySet()) {
            ItemStack input = recipe.getKey();
            ItemStack output = recipe.getValue();
            if (output.getItem().equals(desired.getItem())) {
                int desiredQuantity = desired.stackSize;
                int outputQuantity = output.stackSize;
                int totalQuantity = (int) Math.ceil(((double) desiredQuantity) / ((double) outputQuantity));
                int inputQuantity = input.stackSize * totalQuantity;
                System.out.println("Recipe from " + input + " to " + output + " " + desiredQuantity + " " + outputQuantity + " " + totalQuantity + " " + inputQuantity);
                if (inputQuantity > 64) {
                    throw new IllegalStateException("lol");
                }
                return new ItemStack(input.getItem(), inputQuantity, input.getMetadata());
            }
        }
        return null;
    }

    private static class wrapper {//so that people don't try to directly reference recipess
        private static Map<ItemStack, ItemStack> recipes = null;
        public static Map<ItemStack, ItemStack> getRecipes() {
            if (recipes == null) {
                recipes = FurnaceRecipes.instance().getSmeltingList();
            }
            return recipes;
        }
    }
    public static Map<ItemStack, ItemStack> getRecipes() {
        return wrapper.getRecipes();
    }
}
