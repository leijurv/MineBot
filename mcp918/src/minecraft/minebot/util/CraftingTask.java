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
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.client.Minecraft;
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
    private ItemStack currentlyCrafting = null;
    private int stackSize;
    private int alreadyHas;
    
    private CraftingTask(ItemStack craftStack) {
        this.currentlyCrafting = craftStack;
        this.stackSize = craftStack.stackSize;
    }
    
    /**
     * @param item
     * @return recipe for that item, or null if item has no recipe
     */
    public static IRecipe getRecipeFromItem(Item item) {
        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        for(IRecipe currRecipe : recipes) {
            if(currRecipe.getRecipeOutput().getItem().equals(item)) {
                return currRecipe;
            }
        }
        return null;
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
    
    public void execute() {
        IRecipe currentRecipe = getRecipeFromItem(currentlyCrafting.getItem());
        if(!(currentRecipe == null)) {
            if(currentRecipe instanceof ShapedRecipes) {
                ShapedRecipes shapedRecipe = (ShapedRecipes) currentRecipe;
                for(ItemStack input : shapedRecipe.recipeItems) {
                    IRecipe inputRecipe = getRecipeFromItem(input.getItem());
                    if(!(inputRecipe == null)) {
                        CraftingTask newTask = CraftingTask.findOrCreateCraftingTask(input);
                        subCraftingTasks.add(newTask);
                        newTask.execute();
                    }
                }
            } else if(currentRecipe instanceof ShapelessRecipes) {
                ShapelessRecipes shapelessRecipe = (ShapelessRecipes) currentRecipe;
                for(ItemStack input : shapelessRecipe.recipeItems) {
                    IRecipe inputRecipe = getRecipeFromItem(input.getItem());
                    if(!(inputRecipe == null)) {
                        CraftingTask newTask = CraftingTask.findOrCreateCraftingTask(input);
                        subCraftingTasks.add(newTask);
                        newTask.execute();
                    }
                }
            } else {
                throw new IllegalStateException("Current recipe isn't shapeless or shaped");
            }
        } else {
             
        }
    }
    
    public static CraftingTask findOrCreateCraftingTask(ItemStack itemStack) {
        for(CraftingTask selectedTask : overallCraftingTasks) {
            if(selectedTask.currentlyCrafting().equals(itemStack))
                selectedTask.addNeededAmount(itemStack.stackSize);
                return selectedTask;
        }
        CraftingTask newTask = new CraftingTask(itemStack);
        overallCraftingTasks.add(newTask);
        return newTask;
    }
    
    public ItemStack currentlyCrafting() {
        return currentlyCrafting;
    }
    
    public void addNeededAmount(int amount) {
        stackSize += amount;
    }
    
    public static HashMap<Item,ArrayList<Tuple<Integer, Integer>>> getCurrentRecipeItems(IRecipe recipe) throws ClassNotFoundException {
        HashMap<Item,ArrayList<Tuple<Integer, Integer>>> amountHasAndWhere = new HashMap();
        
        ArrayList<ItemStack> needsItemStacks = null;
        if(recipe instanceof ShapedRecipes) {
            ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
            needsItemStacks = new ArrayList<ItemStack>(Arrays.asList(shapedRecipe.recipeItems));
        } else if(recipe instanceof ShapelessRecipes) {
            ShapelessRecipes shapelessRecipe = (ShapelessRecipes) recipe;
            needsItemStacks = new ArrayList<ItemStack>(shapelessRecipe.recipeItems);
        } else {
            throw new IllegalStateException("recipe was not shaped or shapeless");
        }
        
        for(int i = 0; i < Minecraft.theMinecraft.thePlayer.inventory.getSizeInventory(); i++) {
            for(int j = 0; j < needsItemStacks.size(); j++) {
                if(Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).getItem().equals(needsItemStacks.get(j).getItem())) {
                    if(!amountHasAndWhere.containsKey(needsItemStacks.get(j).getItem())) {
                        ArrayList<Tuple<Integer, Integer>> positionAmountArr = new ArrayList<Tuple<Integer, Integer>>();
                        positionAmountArr.add(new Tuple(Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).stackSize, i));
                        amountHasAndWhere.put(needsItemStacks.get(j).getItem(), positionAmountArr);
                    } else {
                        amountHasAndWhere.get(needsItemStacks.get(j).getItem()).add(new Tuple(Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).stackSize, i));
                    }
                }
            }
        }
        
        return amountHasAndWhere;
    }
    
    public void calculateAlreadyHasAmmount() {
        ItemStack targetItem = null;
        for(int i = 0; i < Minecraft.theMinecraft.thePlayer.inventory.getSizeInventory(); i++){
            if(Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i).getItem().equals(currentlyCrafting.getItem())) {
                targetItem = Minecraft.theMinecraft.thePlayer.inventory.getStackInSlot(i);
            }
        }
        if(targetItem != null) {
            alreadyHas = stackSize - targetItem.stackSize;
            return;
        }
        alreadyHas = stackSize;
    }
    
    public int stillToCraft() {
        return alreadyHas;
    }
    
    public boolean isDone() {
        calculateAlreadyHasAmmount();
        return 0 <= alreadyHas;
    }
    
}
