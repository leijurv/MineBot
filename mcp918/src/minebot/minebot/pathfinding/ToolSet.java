/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 *
 * @author avecowa
 */
public class ToolSet {
    public ArrayList<Item> tools;
    public ArrayList<Byte> slots;
    public HashMap<Block, Byte> cache = new HashMap<>();
    ToolSet(ArrayList<Item> tools, ArrayList<Byte> slots) {
        this.tools = tools;
        this.slots = slots;
    }
    ToolSet() {
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        tools = new ArrayList<>();
        slots = new ArrayList<>();
        //System.out.println("inv: " + Arrays.toString(inv));
        boolean fnull = false;
        for (byte i = 0; i < 9; i++) {
            if (!fnull || (inv[i] != null && inv[i].getItem().isItemTool(null))) {
                tools.add(inv[i] != null ? inv[i].getItem() : null);
                slots.add(i);
                fnull |= inv[i] == null || (!inv[i].getItem().isDamageable());
            }
        }
    }
    public Item getBestTool(Block b) {
        if (cache.get(b) != null) {
            return tools.get(cache.get(b));
        }
        byte best = 0;
        //System.out.println("best: " + best);
        float value = 0;
        for (byte i = 0; i < tools.size(); i++) {
            Item item = tools.get(i);
            if (item == null) {
                item = Item.getByNameOrId("minecraft:apple");
            }
            //System.out.println(inv[i]);
            float v = item.getStrVsBlock(new ItemStack(item), b);
            //System.out.println("v: " + v);
            if (v > value) {
                value = v;
                best = i;
            }
        }
        //System.out.println("best: " + best);
        cache.put(b, best);
        return tools.get(best);
    }
    public byte getBestSlot(Block b) {
        if (cache.get(b) != null) {
            return slots.get(cache.get(b));
        }
        byte best = 0;
        //System.out.println("best: " + best);
        float value = 0;
        for (byte i = 0; i < tools.size(); i++) {
            Item item = tools.get(i);
            if (item == null) {
                item = Item.getByNameOrId("minecraft:apple");
            }
            //System.out.println(inv[i]);
            float v = item.getStrVsBlock(new ItemStack(item), b);
            //System.out.println("v: " + v);
            if (v > value) {
                value = v;
                best = i;
            }
        }
        //System.out.println("best: " + best);
        cache.put(b, best);
        return slots.get(best);
    }
    public double getStrVsBlock(Block b, BlockPos pos) {
        Item item = this.getBestTool(b);
        float f = b.getBlockHardness(Minecraft.theMinecraft.theWorld, pos);
        return f < 0.0F ? 0.0F : (!canHarvest(b, item) ? item.getStrVsBlock(new ItemStack(item), b) / f / 100.0F : item.getStrVsBlock(new ItemStack(item), b) / f / 30.0F);
    }
    public boolean canHarvest(Block blockIn, Item item) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        } else {
            return new ItemStack(item).canHarvestBlock(blockIn);
        }
    }
}
