/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

/**
 *
 * @author galdara
 */
public class CraftingTask {
    
    static ArrayList<CraftingTask> overallCraftingTasks = new ArrayList<CraftingTask>();
    ArrayList<CraftingTask> subCraftingTasks = new ArrayList<CraftingTask>();
    private ItemStack currentlyCrafting = null;
    
    private CraftingTask(ItemStack craftStack) {
        this.currentlyCrafting = craftStack;
        this.execute();
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
                    int amount = input.stackSize;
                    
                }
            } else if(currentRecipe instanceof ShapelessRecipes) {
                ShapelessRecipes shapelessRecpe = (ShapelessRecipes) currentRecipe;
                
            } else {
                
            }
        } else {
             
        }
    }
    
    public static CraftingTask findOrCreateCraftingTask(ItemStack itemStack) {
        for(CraftingTask selectedTask : overallCraftingTasks) {
            if(selectedTask.currentlyCrafting().equals(itemStack))
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
         
    }
    
}
