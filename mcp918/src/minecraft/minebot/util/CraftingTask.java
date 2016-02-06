/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import minebot.MineBot;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.Tuple;

/**
 *
 * @author galdara
 */
public class CraftingTask {
    static ArrayList<CraftingTask> overallCraftingTasks = new ArrayList<CraftingTask>();
    ArrayList<CraftingTask> subCraftingTasks = new ArrayList<CraftingTask>();
    private Item currentlyCrafting = null;
    private int stackSize;
    private int alreadyHas;
    private CraftingTask(ItemStack craftStack) {
        this.currentlyCrafting = craftStack.getItem();
        this.stackSize = 0;
        buildTasks();
        increaseNeededAmount(craftStack.stackSize);
    }
    public static int map(int id, int width, int height) {//shamelessly copied from Objectives
        int yPos = id / width;
        int xPos = id % width;
        int z = xPos + 2 * yPos;
        //System.out.println("Mapping " + id + " in " + width + "," + height + " to " + xPos + "," + yPos + " with id " + z);
        return z + 1;
    }
    /**
     * @param item
     * @return recipe for that item, or null if item has no recipe
     */
    public static IRecipe getRecipeFromItem(Item item) {
        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        for (IRecipe currRecipe : recipes) {
            if (currRecipe == null) {
                continue;
            }
            if (currRecipe.getRecipeOutput() == null) {
                continue;
            }
            if (currRecipe.getRecipeOutput().getItem() == null) {
                continue;//probably not all of these are necessary, but when I added all three it stopped a nullpointerexception somewhere in this function
            }
            if (currRecipe.getRecipeOutput().getItem().equals(item)) {
                if (isRecipeOkay(currRecipe)) {
                    return currRecipe;
                }
            }
        }
        return null;
    }
    public static boolean isRecipeOkay(IRecipe recipe) {
        if (recipe instanceof ShapedRecipes) {
            for (ItemStack stack : ((ShapedRecipes) recipe).recipeItems) {
                if (stack.toString().toLowerCase().contains("block")) {
                    System.out.println("Not doing " + stack);
                    return false;
                }
            }
            return true;
        }
        if (recipe instanceof ShapelessRecipes) {
            for (ItemStack stack : ((ShapelessRecipes) recipe).recipeItems) {
                if (stack.toString().toLowerCase().contains("block")) {
                    System.out.println("Not doing " + stack);
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public static CraftingTask getRequirementsFromItem(Item item) {
        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        IRecipe recipe = recipes.get(0);
        //recipe.
        return null;
    }
    public static boolean recipeNeedsCraftingTable(IRecipe recipe) {
        return recipe.getRecipeSize() > 4;
    }
    public void onTick() {
        System.out.println(this + " tick " + stackSize + " " + alreadyHas + " " + currentlyCrafting);
        if (isDone()) {
            return;
        }
        craftInInventory(1);
        //calculate how many we could craft given current items
        //if we could craft given what we have in our inv right now
        //if this recipe could fit in 2x2 grid, craft immediately (if in guicrafting, use crafting table, otherwise use inv grid)
        //if this recipe needs 3x3 and we are in a GuiCrafting right now, just do it™
        //if this recipe needs 3x3 and we arent in a gui crafting and there is a crafting table nearby or in our inventory, place/open it
        //if we actualy ended up crafting some, go through our sub crafting tasks and decrease needed amounts accordingly
    }
    public void craftInInventory(int quantity) {
        IRecipe currentRecipe = getRecipeFromItem(currentlyCrafting);
        if (currentRecipe instanceof ShapedRecipes) {
            ShapedRecipes shaped = (ShapedRecipes) currentRecipe;
            if (shaped.recipeHeight <= 2 && shaped.recipeWidth <= 2) {
                Item[] items = new Item[shaped.recipeItems.length];
                int[] positions = new int[items.length];
                for (int i = 0; i < items.length; i++) {
                    if (shaped.recipeItems[i] == null) {
                        continue;
                    }
                    items[i] = shaped.recipeItems[i].getItem();
                    positions[i] = map(i, shaped.recipeWidth, shaped.recipeHeight);
                }
                actualDoCraftOne(items, positions, quantity);
            }
        }
        if (currentRecipe instanceof ShapelessRecipes) {
            ShapelessRecipes shapeless = (ShapelessRecipes) currentRecipe;
            if (shapeless.getRecipeSize() < 4) {
                Item[] items = new Item[shapeless.getRecipeSize()];
                int[] positions = new int[items.length];
                for (int i = 0; i < items.length; i++) {
                    items[i] = shapeless.recipeItems.get(i).getItem();
                    positions[i] = i + 1;
                }
                actualDoCraftOne(items, positions, quantity);
            }
        }
    }
    public static void actualDoCraftOne(Item[] items, int[] positions, int amount) {
        if (Minecraft.theMinecraft.currentScreen == null || !(Minecraft.theMinecraft.currentScreen instanceof GuiInventory)) {
            System.out.println("Opening");
            MineBot.slowOpenInventory();
        }
        int[] amounts = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            amounts[i] = amount;
            GuiScreen.sendChatMessage(i + " " + items[i] + " " + positions[i] + " " + amount, true);
        }
        GuiContainer contain = (GuiContainer) Minecraft.theMinecraft.currentScreen;
        for (int i = 9; i < contain.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = contain.inventorySlots.inventorySlots.get(i);
            if (!slot.getHasStack()) {
                continue;
            }
            ItemStack in = slot.getStack();
            if (in == null) {
                continue;
            }
            System.out.println(i + " " + in);
            Item item = in.getItem();
            int size = in.stackSize;
            for (int j = 0; j < items.length; j++) {
                if (amounts[j] <= 0) {
                    continue;
                }
                if (items[j].equals(item)) {
                    contain.leftClick(i);
                    if (size <= amounts[j]) {
                        contain.leftClick(positions[j]);
                        amounts[j] -= size;
                        size = 0;
                    } else {
                        for (int k = 0; k < amounts[j]; k++) {
                            contain.rightClick(positions[j]);
                        }
                        size -= amounts[j];
                        contain.leftClick(i);
                        amounts[j] = 0;
                    }
                }
            }
        }
        GuiScreen.sendChatMessage("shift clicking " + contain.inventorySlots.inventoryItemStacks.get(0), true);
        contain.shiftClick(0);
        for (int i = 0; i < amounts.length; i++) {
            if (amounts[i] > 0) {
                GuiScreen.sendChatMessage("Not enough " + i + " " + amounts[i] + " " + items[i] + " " + positions[i], true);//this detects if it didn't have enough, but you shouldn't call this function unless you have already made sure you have enough
            }
        }
    }
    public static void tickAll() {
        for (CraftingTask craftingTask : overallCraftingTasks) {
            craftingTask.onTick();
        }
    }
    public final void buildTasks() {
        IRecipe currentRecipe = getRecipeFromItem(currentlyCrafting);
        if (!(currentRecipe == null)) {
            if (currentRecipe instanceof ShapedRecipes) {
                ShapedRecipes shapedRecipe = (ShapedRecipes) currentRecipe;
                for (ItemStack input : shapedRecipe.recipeItems) {
                    IRecipe inputRecipe = getRecipeFromItem(input.getItem());
                    if (!(inputRecipe == null)) {
                        System.out.println("As a part of " + currentlyCrafting + ", getting " + input);
                        CraftingTask newTask = CraftingTask.findOrCreateCraftingTask(input);
                        subCraftingTasks.add(newTask);
                        //newTask.execute();
                    }
                }
            } else if (currentRecipe instanceof ShapelessRecipes) {
                ShapelessRecipes shapelessRecipe = (ShapelessRecipes) currentRecipe;
                for (ItemStack input : shapelessRecipe.recipeItems) {
                    IRecipe inputRecipe = getRecipeFromItem(input.getItem());
                    if (!(inputRecipe == null)) {
                        System.out.println("As a part of " + currentlyCrafting + ", getting " + input);
                        CraftingTask newTask = CraftingTask.findOrCreateCraftingTask(input);
                        subCraftingTasks.add(newTask);
                        //newTask.execute();
                    }
                }
            } else {
                throw new IllegalStateException("Current recipe isn't shapeless or shaped");
            }
        } else {
            throw new IllegalArgumentException("no recipe for this");
        }
    }
    public static CraftingTask findOrCreateCraftingTask(ItemStack itemStack) {
        System.out.println("Getting a task for " + itemStack);
        for (CraftingTask selectedTask : overallCraftingTasks) {
            if (selectedTask.currentlyCrafting().getItem().equals(itemStack.getItem())) {
                selectedTask.increaseNeededAmount(itemStack.stackSize);
                return selectedTask;
            }
        }
        CraftingTask newTask = new CraftingTask(itemStack);
        overallCraftingTasks.add(newTask);
        return newTask;
    }
    public ItemStack currentlyCrafting() {
        return new ItemStack(currentlyCrafting, stackSize);
    }
    public final void increaseNeededAmount(int amount) {
        stackSize += amount;
        for (CraftingTask craftingTask : subCraftingTasks) {
            craftingTask.increaseNeededAmount(amount);
        }
    }
    public void decreaseNeededAmount(int amount) {
        stackSize -= amount;
        for (CraftingTask craftingTask : subCraftingTasks) {
            craftingTask.decreaseNeededAmount(amount);
        }
    }
    public static HashMap<Item, ArrayList<Tuple<Integer, Integer>>> getCurrentRecipeItems(IRecipe recipe) {
        HashMap<Item, ArrayList<Tuple<Integer, Integer>>> amountHasAndWhere = new HashMap();
        ArrayList<ItemStack> needsItemStackstmp = null;
        if (recipe instanceof ShapedRecipes) {
            ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
            needsItemStackstmp = new ArrayList<ItemStack>(Arrays.asList(shapedRecipe.recipeItems));
        } else if (recipe instanceof ShapelessRecipes) {
            ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;
            needsItemStackstmp = new ArrayList<ItemStack>(shapelessRecipe.recipeItems);
        } else {
            throw new IllegalStateException("recipe was not shaped or shapeless");
        }
        ArrayList<Item> needsItemStacks = new ArrayList();
        for (ItemStack stack : needsItemStackstmp) {
            Item item = stack.getItem();
            if (!needsItemStacks.contains(item)) {
                needsItemStacks.add(item);
            }
        }
        for (int i = 0; i < Minecraft.theMinecraft.thePlayer.inventory.getSizeInventory(); i++) {
            for (int j = 0; j < needsItemStacks.size(); j++) {
                if (Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i) == null) {
                    continue;//prevents nullpointerexception if the stack is null
                }
                if (Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).getItem().equals(needsItemStacks.get(j))) {
                    if (!amountHasAndWhere.containsKey(needsItemStacks.get(j))) {
                        ArrayList<Tuple<Integer, Integer>> positionAmountArr = new ArrayList<Tuple<Integer, Integer>>();
                        positionAmountArr.add(new Tuple(Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).stackSize, i));
                        amountHasAndWhere.put(needsItemStacks.get(j), positionAmountArr);
                    } else {
                        amountHasAndWhere.get(needsItemStacks.get(j)).add(new Tuple(Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).stackSize, i));
                    }
                }
            }
        }
        return amountHasAndWhere;
    }
    public void calculateAlreadyHasAmount() {
        ItemStack targetItem = null;
        for (int i = 0; i < Minecraft.theMinecraft.thePlayer.inventory.getSizeInventory(); i++) {
            if (Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i) == null) {
                continue;
            }
            if (Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).getItem().equals(currentlyCrafting)) {
                targetItem = Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i);
            }
        }
        System.out.println(this + " " + currentlyCrafting + " " + targetItem);
        if (targetItem != null) {
            alreadyHas = targetItem.stackSize;
            return;
        }
        alreadyHas = 0;
    }
    public int alreadyHas() {
        return alreadyHas;
    }
    public boolean isDone() {
        calculateAlreadyHasAmount();
        return stackSize <= alreadyHas;
    }
}
