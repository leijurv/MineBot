/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 *
 * @author leijurv
 */
public class SmeltingTask {
    private final Map<ItemStack, ItemStack> recipes = getRecipes();
    public SmeltingTask(ItemStack desired) {
        for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet()) {
            ItemStack input = recipe.getKey();
            ItemStack output = recipe.getValue();
            if (output.getItem().equals(desired.getItem())) {
                int desiredQuantity = desired.stackSize;
                int outputQuantity = output.stackSize;
                int totalQuantity = (int) Math.ceil(((double) desiredQuantity) / ((double) outputQuantity));
                System.out.println("Recipe from " + input + " to " + output);
            }
        }
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
