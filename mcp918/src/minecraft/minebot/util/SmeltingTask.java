/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import minebot.MineBot;
import net.minecraft.block.Block;
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
    static HashMap<BlockPos, SmeltingTask> furnacesInUse = new HashMap();//smelting tasks that have been put in a furnace are here
    static ArrayList<SmeltingTask> inProgress = new ArrayList();//all smelting tasks will be in here
    static ArrayList<BlockPos> knownFurnaces = new ArrayList();
    public static void onTick() {
        for (SmeltingTask task : inProgress) {
            task.tick();
        }
    }
    public static void onFurnacePlace(BlockPos pos) {
        if (pos == null) {
            throw new NullPointerException("take a hike");
        }
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock();
        if (!Block.getBlockFromName("minecraft:furnace").equals(block) && !Block.getBlockFromName("minecraft:lit_furnace").equals(block)) {
            GuiScreen.sendChatMessage(block + " isn't a furnace", true);
            throw new IllegalStateException(block + " isn't a furnace");
        }
        if (!knownFurnaces.contains(pos)) {
            knownFurnaces.add(pos);
        }
    }
    private final ItemStack toPutInTheFurnace;
    private final ItemStack desired;
    private BlockPos furnace = null;
    private boolean didIPutItInAlreadyPhrasing = false;
    private boolean isItDone = false;
    public final int burnTicks;
    public SmeltingTask(ItemStack desired) {
        toPutInTheFurnace = recipe(desired);
        if (toPutInTheFurnace == null) {
            String m = "Babe I can't smelt anyting to make " + desired;
            GuiScreen.sendChatMessage(m, true);
            throw new IllegalArgumentException(m);
        }
        burnTicks = toPutInTheFurnace.stackSize * 200;
        this.desired = desired;
    }
    public void begin() {
        if (!inProgress.contains(this)) {
            inProgress.add(this);
            //todo: merge different smelting tasks for the same item
        }
    }
    int numTicks = -2;//wait a couple extra ticks, for no reason (I guess server lag maybe)
    private void tick() {
        System.out.println(didIPutItInAlreadyPhrasing + " " + isItDone + " " + numTicks + " " + burnTicks);
        if (Minecraft.theMinecraft.currentScreen != null && Minecraft.theMinecraft.currentScreen instanceof GuiFurnace) {
            GuiFurnace contain = (GuiFurnace) Minecraft.theMinecraft.currentScreen;//I don't check this, because you should check this before calling it, and if you don't you deserve the ClassCastException
            if (!didIPutItInAlreadyPhrasing) {
                if (realPutItIn_PHRASING(contain)) {
                    didIPutItInAlreadyPhrasing = true;
                    furnace = MineBot.whatAreYouLookingAt();
                    knownFurnaces.add(furnace);
                }
            }
        }
        if (didIPutItInAlreadyPhrasing && !isItDone) {
            numTicks++;
            if (numTicks >= burnTicks) {
                isItDone = true;
                GuiScreen.sendChatMessage("Hey we're done. Go to your furnace at " + furnace + " and pick up " + desired, true);
                //todo: pathfind to furnace and take result out
            }
        }
    }
    public boolean isInFurnace() {
        return didIPutItInAlreadyPhrasing;
    }
    public boolean lookingForFurnace() {
        return furnace == null;
    }
    private boolean realPutItIn_PHRASING(GuiFurnace contain) {
        int desiredAmount = toPutInTheFurnace.stackSize;
        if (currentSize(contain, 0, toPutInTheFurnace.getItem()) == -1) {
            GuiScreen.sendChatMessage("Furnace already in use", true);
            return false;
        }
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
            return false;
        }
        System.out.println(burnableItems);
        System.out.println(amountWeHave);
        System.out.println(amtNeeded);
        System.out.println(burnTimes);
        Item bestFuel = null;
        int fuelAmt = Integer.MAX_VALUE;
        int bestExtra = Integer.MAX_VALUE;
        for (int i = 0; i < burnableItems.size(); i++) {
            int amt = amtNeeded.get(i);
            int extra = burnTimes.get(i) * amtNeeded.get(i) - burnTicks;
            if (extra < bestExtra || (extra == bestExtra && amt < fuelAmt)) {
                fuelAmt = amt;
                bestExtra = extra;
                bestFuel = burnableItems.get(i);
            }
        }
        GuiScreen.sendChatMessage("Using " + fuelAmt + " items of " + bestFuel + ", which wastes " + bestExtra + " ticks of fuel.", true);
        for (int i = 3; i < contain.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = contain.inventorySlots.inventorySlots.get(i);
            if (!slot.getHasStack()) {
                continue;
            }
            ItemStack in = slot.getStack();
            if (in == null) {
                continue;
            }
            if (in.getItem().equals(bestFuel)) {
                int currentSize = currentSize(contain, 1, bestFuel);
                int amountHere = in.stackSize;
                int amountNeeded = fuelAmt - currentSize;
                if (currentSize == -1) {
                    GuiScreen.sendChatMessage("Furnace already in use", true);
                    return false;
                }
                contain.leftClick(i);
                if (amountNeeded >= amountHere) {
                    contain.leftClick(1);
                } else {
                    for (int j = 0; j < amountNeeded; j++) {
                        contain.rightClick(1);
                    }
                }
                contain.leftClick(i);
                if (currentSize(contain, 1, bestFuel) >= fuelAmt) {
                    GuiScreen.sendChatMessage("done with fuel", true);
                    break;
                }
            }
        }
        if (currentSize(contain, 0, toPutInTheFurnace.getItem()) >= desiredAmount) {
            GuiScreen.sendChatMessage("done", true);
            return true;
        }
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
                int currentSize = currentSize(contain, 0, toPutInTheFurnace.getItem());
                int amountHere = in.stackSize;
                int amountNeeded = desiredAmount - currentSize;
                if (currentSize == -1) {
                    GuiScreen.sendChatMessage("Furnace already in use", true);
                    return false;
                }
                contain.leftClick(i);
                if (amountNeeded >= amountHere) {
                    contain.leftClick(0);
                } else {
                    for (int j = 0; j < amountNeeded; j++) {
                        contain.rightClick(0);
                    }
                }
                contain.leftClick(i);
                if (currentSize(contain, 0, toPutInTheFurnace.getItem()) >= desiredAmount) {
                    GuiScreen.sendChatMessage("done", true);
                    return true;
                }
            }
        }
        if (currentSize(contain, 0, toPutInTheFurnace.getItem()) >= desiredAmount) {
            GuiScreen.sendChatMessage("done", true);
            return true;
        }
        GuiScreen.sendChatMessage("Still need " + (desiredAmount - currentSize(contain, 0, toPutInTheFurnace.getItem())) + " items", true);
        return false;
    }
    private int currentSize(GuiFurnace contain, int id, Item item) {
        Slot slot = contain.inventorySlots.inventorySlots.get(id);
        if (!slot.getHasStack()) {
            return 0;
        }
        ItemStack in = slot.getStack();
        if (in == null) {
            return 0;
        }
        if (!in.getItem().equals(item)) {
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
