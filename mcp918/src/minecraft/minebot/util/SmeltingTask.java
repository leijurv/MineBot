/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 *
 * @author leijurv
 */
public class SmeltingTask {
    public SmeltingTask(ItemStack desired) {
    }
    static {
        FurnaceRecipes.instance().getSmeltingList();
    }
}
