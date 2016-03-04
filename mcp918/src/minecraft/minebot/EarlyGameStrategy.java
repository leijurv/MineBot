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
 * get dirt
 *
 * get wood
 *
 * make a crafting table
 *
 * make a wooden pickaxe
 *
 * get stone
 *
 * make a stone pickaxe
 *
 * get more stone
 *
 * make stone tools and a furnace
 *
 * go mining at level 36
 *
 * craft torches
 *
 * smelt iron
 *
 * make iron pick and iron armor and an iron sword
 *
 * change mining level to 6
 *
 * craft a diamond pickaxe
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
        /*
         TODO
         Don't craft a pick if we already have a higher level of pick
         Don't craft any tools in general if we already have a higher level
         Throw away any tools where we have two higher levels. (e.g. throw away wood pick once we get both stone pick and iron pick)
         Same thing for armor. Throw away any armor in inventory if we have better armor in armorInventory or mainInventory
         */
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
        int miningLevel = 36;
        if (readyForMining) {
            int amtIron = 0;
            boolean ironPick = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:iron_pickaxe"), 1);
            if (ironPick) {
                boolean ironSword = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:iron_sword"), 1);
                if (ironSword) {
                    boolean ironHelmet = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:iron_helmet"), 1);
                    boolean ironChestplate = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:iron_chestplate"), 1);
                    boolean ironLeggings = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:iron_leggings"), 1);
                    boolean ironBoots = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("minecraft:iron_boots"), 1);
                    if (ironHelmet && ironChestplate && ironLeggings && ironBoots) {
                        miningLevel = 6;
                    } else {
                        amtIron = (!ironHelmet ? 5 : 0) + (!ironChestplate ? 8 : 0) + (!ironLeggings ? 7 : 0) + (!ironBoots ? 4 : 0);
                    }
                } else {
                    amtIron = 2;
                }
            } else {
                amtIron = 3;
            }
            int currIron = countItem("minecraft:iron_ingot");
            boolean hasOre = countItem("iron_ore") >= amtIron - currIron;
            if (hasOre && currIron < amtIron) {
                int tasksForIron = SmeltingTask.tasksFor(Item.getByNameOrId("iron_ingot"));
                int newTask = amtIron - currIron - tasksForIron;
                if (newTask > 0) {
                    new SmeltingTask(new ItemStack(Item.getByNameOrId("iron_ingot"), Math.min(countItem("iron_ore"), 64))).begin();
                }
                readyForMining = false;
            }
        }
        int numDiamonds = countItem("diamond");
        if (readyForMining && numDiamonds >= 1) {
            if (CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_pickaxe"), 1)) {
                if (CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_sword"), 1)) {
                    boolean shovel = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_shovel"), 1);
                    boolean axe = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_axe"), 1);
                    boolean boots = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_boots"), 1);
                    boolean leg = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_leggings"), 1);
                    boolean chest = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_chestplate"), 1);
                    boolean helmet = CraftingTask.ensureCraftingDesired(Item.getByNameOrId("diamond_helmet"), 1);
                    if (shovel && axe && boots && leg && chest && helmet) {
                        GuiScreen.sendChatMessage("My job here is done.");
                        cancel();
                        return false;
                    }
                }
            }
        }
        Manager instance = Manager.getManager(MickeyMine.class);
        if (readyForMining) {
            MickeyMine.yLevel = miningLevel;
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
        Combat.mobKilling = false;
        SmeltingTask.coalOnly = false;
        Manager.getManager(MickeyMine.class).cancel();
    }
    @Override
    protected void onStart() {
        gotWood_PHRASING = false;
        WOOD_AMT = 16;
        MIN_WOOD_AMT = 1;
        gotDirt = false;
        cobble = false;
        Combat.mobKilling = true;
        SmeltingTask.coalOnly = true;
    }
}
