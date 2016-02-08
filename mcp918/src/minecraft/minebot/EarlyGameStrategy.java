/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * goals:
 *
 * get wood
 *
 * make a crafting table
 *
 * make a wooden pickaxe
 *
 * @author leijurv
 */
public class EarlyGameStrategy {
    static boolean gotWood_PHRASING = false;
    static final int WOOD_AMT = 16;
    public static void tick() {
        int wood = countWood_PHRASING();
        if (wood >= WOOD_AMT) {
            gotWood_PHRASING = true;
        }
        if (!gotWood_PHRASING) {
            TreePuncher.tick();
            return;
        }
    }
    public static int countWood_PHRASING() {
        Item log1 = Item.getItemFromBlock(Block.getBlockFromName("minecraft:log"));
        Item log2_NOTCH_IS_A_BAD_PROGRAMMER = Item.getItemFromBlock(Block.getBlockFromName("minecraft:log2"));
        int count = 0;
        for (ItemStack stack : Minecraft.theMinecraft.thePlayer.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }
            if (stack.getItem().equals(log1) || stack.getItem().equals(log2_NOTCH_IS_A_BAD_PROGRAMMER)) {
                count += stack.stackSize;
            }
        }
        return count;
    }
}
