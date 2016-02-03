/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

/**
 *
 * @author galdara
 */
public class CraftingTask {
    
    public static IRecipe getRecipeFromItem(Item item) {
        List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();
        for(IRecipe currRecipe : recipes) {
            if(currRecipe.getRecipeOutput().getItem().equals(item)) {
                return currRecipe;
            }
        }
        return null;
    }
    
}
