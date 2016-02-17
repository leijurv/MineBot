/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import minebot.mining.MickeyMine;
import minebot.util.CraftingTask;
import minebot.util.Manager;
import minebot.util.ManagerTick;
import minebot.util.SmeltingTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
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
public class EarlyGameStrategy extends ManagerTick {
    static boolean gotWood_PHRASING = false;
    static int WOOD_AMT = 16;//triggers stopping
    static int MIN_WOOD_AMT = 1;//triggers getting more
    static final int DIRT_AMT = 32;
    static boolean gotDirt = false;
    static boolean cobble = false;
    @Override
    protected boolean onTick0() {
        if (!gotDirt) {
            int dirt = countDirt();
            if (dirt >= DIRT_AMT) {
                GuiScreen.sendChatMessage("Done getting dirt");
                gotDirt = true;
                return false;
            }
            if (!BlockPuncher.tick("dirt", "grass")) {
                GuiScreen.sendChatMessage("No dirt or grass nearby =(");
            }
            return false;
        }
        int wood = countWood_PHRASING();
        if (wood >= WOOD_AMT) {
            if (!gotWood_PHRASING) {
                GuiScreen.sendChatMessage("Done getting wood", true);
            }
            gotWood_PHRASING = true;
        }
        if (wood < MIN_WOOD_AMT) {
            if (gotWood_PHRASING) {
                GuiScreen.sendChatMessage("Getting more wood", true);
            }
            gotWood_PHRASING = false;
        }
        if (!gotWood_PHRASING) {
            if (!BlockPuncher.tick("log", "log2")) {
                GuiScreen.sendChatMessage("No wood nearby =(");
            }
            return false;
        }
        boolean hasWooden = false;
        boolean readyForMining = true;
        boolean hasStone = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:stone_pickaxe"), 1);
        if (hasStone) {
            dontCraft(Item.getByNameOrId("minecraft:wooden_pickaxe"));
        } else {
            hasWooden = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:wooden_pickaxe"), 1);
        }
        readyForMining &= hasStone;
        if (hasWooden || hasStone) {
            if (!cobble) {
                if (countCobble() > 16) {
                    cobble = true;
                } else {
                    if (!BlockPuncher.tick("stone")) {
                        GuiScreen.sendChatMessage("No stone nearby =(");
                    }
                }
            }
        }
        if (!cobble) {
            readyForMining = false;
        }
        if (cobble && gotDirt && countCobble() + countDirt() < 10) {//if we have already gotten cobble and dirt, but our amounts have run low, get more
            if (!BlockPuncher.tick("dirt", "grass", "stone")) {
                GuiScreen.sendChatMessage("No dirt, grass, or stone");
            }
            readyForMining = false;
        }
        if (countCobble() > 5) {
            boolean axe = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:stone_axe"), 1);
            if (axe) {
                WOOD_AMT = 64;
                MIN_WOOD_AMT = 16;
            } else {
                readyForMining = false;
            }
            if (!CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:stone_shovel"), 1)) {
                readyForMining = false;
            }
            if (!CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:stone_sword"), 1)) {
                readyForMining = false;
            }
        }
        if (countCobble() > 8) {
            if (!CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:furnace"), 1)) {
                readyForMining = false;
            }
        }
        if (readyForMining) {
            boolean ironPick = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:iron_pickaxe"), 1);
            boolean hasOre = countItem("iron_ore") >= 3;
            if (!ironPick && hasOre && countItem("minecraft:iron_ingot") < 3) {//if we don't have a pick or enough ingots, and we have enough ore, do the smelting
                int tasksForIron = SmeltingTask.tasksFor(Item.getByNameOrId("iron_ingot"));
                if (tasksForIron == 0) {
                    new SmeltingTask(new ItemStack(Item.getByNameOrId("iron_ingot"), Math.min(countItem("iron_ore"), 64))).begin();
                }
                readyForMining = false;
            }
        }
        MickeyMine instance = (MickeyMine) Manager.getManager(MickeyMine.class);
        if (readyForMining) {
            MickeyMine.yLevel = 36;
            if (!instance.enabled()) {
                instance.toggle();
            }
        } else {
            if (instance.enabled()) {
                instance.toggle();
            }
        }
        return false;
    }
    public static void dontCraft(Item item) {
        CraftingTask task = CraftingTask.findOrCreateCraftingTask(new ItemStack(item, 0));
        if (task.currentlyCrafting().stackSize > 0) {
            task.decreaseNeededAmount(1);
        }
    }
    public static int countItem(String s) {
        Item item = Item.getByNameOrId(s);
        int count = 0;
        for (ItemStack stack : Minecraft.theMinecraft.thePlayer.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }
            if (item.equals(stack.getItem())) {
                count += stack.stackSize;
            }
        }
        return count;
    }
    public static int countWood_PHRASING() {
        return countItem("log") + countItem("log2");
    }
    public static int countDirt() {
        return countItem("dirt");
    }
    public static int countCobble() {
        return countItem("cobblestone");
    }
    @Override
    protected void onCancel() {
        gotWood_PHRASING = false;
        WOOD_AMT = 16;
        MIN_WOOD_AMT = 1;
        gotDirt = false;
        cobble = false;
    }
    @Override
    protected void onStart() {
        gotWood_PHRASING = false;
        WOOD_AMT = 16;
        MIN_WOOD_AMT = 1;
        gotDirt = false;
        cobble = false;
    }
}
