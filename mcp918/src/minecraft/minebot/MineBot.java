/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import minebot.strategy.EarlyGameStrategy;
import minebot.movement.MovementManager;
import minebot.movement.Parkour;
import minebot.movement.Combat;
import minebot.inventory.AnotherStealer;
import minebot.util.Autorun;
import minebot.ui.LookManager;
import minebot.util.Memory;
import minebot.inventory.FoodManager;
import minebot.inventory.InventoryManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import minebot.mining.MickeyMine;
import minebot.pathfinding.Path;
import minebot.pathfinding.PathFinder;
import minebot.pathfinding.actions.Action;
import minebot.pathfinding.actions.ActionPlaceOrBreak;
import minebot.pathfinding.goals.Goal;
import minebot.pathfinding.goals.GoalComposite;
import minebot.inventory.CraftingTask;
import minebot.util.Manager;
import minebot.util.ManagerTick;
import minebot.util.Out;
import minebot.schematic.SchematicBuilder;
import minebot.inventory.SmeltingTask;
import minebot.util.MCEdit;
import minebot.util.ToolSet;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author leijurv
 */
public class MineBot {
    public static boolean allowDiagonal = true;
    public static boolean farf5 = false;
    public static boolean slowPath = false;
    public static boolean pause = false;
    public static boolean overrideF3 = true;
    public static boolean allowVerticalMotion = true;
    public static boolean actuallyPutMessagesInChat = false;
    public static boolean isThereAnythingInProgress = false;
    public static boolean fullBright = true;
    public static boolean plsCancel = false;
    public static int tickNumber = 0;
    public static boolean ticktimer = false;
    public static boolean allowBreakOrPlace = true;
    public static boolean hasThrowaway = true;
    public static Path currentPath = null;
    public static Path nextPath = null;
    public static boolean calculatingNext = false;
    public static Goal goal = null;
    static int numTicksInInventoryOrCrafting = 0;
    public static BlockPos death;
    public static long lastDeath = 0;
    public static SchematicBuilder currentBuilder = null;
    public static final ArrayList<Class<? extends Manager>> managers = new ArrayList<Class<? extends Manager>>();
    static {
        managers.add(LookManager.class);
        managers.add(SmeltingTask.class);
        managers.add(Memory.class);
        managers.add(AnotherStealer.class);
        managers.add(InventoryManager.class);
        managers.add(MCEdit.class);
        managers.add(Parkour.class);
        managers.add(Combat.class);
        managers.add(FoodManager.class);
        managers.add(EarlyGameStrategy.class);
        managers.add(CraftingTask.class);
        managers.add(MickeyMine.class);
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        String s = Autorun.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(5) + "../../autorun/runmc.command";
        if (s.contains("jar")) {
            Autorun.start(args);
            return;
        }
        Autorun.runprocess("/usr/local/bin/ant jar");
        Autorun.runprocess("java -Djava.library.path=jars/versions/1.8.8/1.8.8-natives/ -jar dist/MineBot.jar");
    }
    /**
     * Called by minecraft.java
     */
    public static void onTick() {
        try {
            long start = System.currentTimeMillis();
            onTick1();
            long end = System.currentTimeMillis();
            long time = end - start;
            if (ticktimer && time > 3) {
                Out.gui("Tick took " + time + "ms", Out.Mode.Debug);
                Out.log("Tick took " + time + "ms");
            }
        } catch (Exception ex) {
            Out.log("Exception");
            Logger.getLogger(MineBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void onTick1() {
        if (pause) {
            Manager.tick(LookManager.class, true);
            MovementManager.clearMovement();
            return;
        }
        if (Minecraft.theMinecraft.theWorld == null || Minecraft.theMinecraft.thePlayer == null) {
            MineBot.cancelPath();
            MineBot.plsCancel = true;
            return;
        }
        MovementManager.leftPressTime = 0;
        MovementManager.rightPressTime = 0;
        if (MovementManager.isLeftClick) {
            MovementManager.leftPressTime = 5;
        }
        if (MovementManager.isRightClick) {
            MovementManager.rightPressTime = 5;
        }
        for (Class c : managers) {
            Manager.tick(c, true);
        }
        /*MovementManager.isLeftClick = false;
         MovementManager.isRightClick = false;
         MovementManager.jumping = false;
         MovementManager.sneak = false;*/
        MovementManager.clearMovement();
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        BlockPos playerFeet = thePlayer.getPosition0();
        if (thePlayer.isDead && System.currentTimeMillis() > lastDeath + 10000) {
            death = playerFeet;
            lastDeath = System.currentTimeMillis();
            Out.gui("Saved death position (" + death + "). do /death to set goal.", Out.Mode.Minimal);
            thePlayer.respawnPlayer();
            Minecraft.theMinecraft.displayGuiScreen(null);
        }
        tickNumber++;
        hasThrowaway = ActionPlaceOrBreak.hasthrowaway();
        ManagerTick.tickPath = true;
        for (Class c : managers) {
            Manager.tick(c);
        }
        BlockPos ts = whatAreYouLookingAt();
        if (ts != null) {
            Memory.scanBlock(ts);
        }
        if (currentBuilder != null) {
            currentBuilder.tick();
        }
        if (currentPath != null && ManagerTick.tickPath) {
            if (currentPath.tick()) {
                Goal currentPathGoal = currentPath == null ? null : currentPath.goal;
                if (currentPath != null && currentPath.failed) {
                    clearPath();
                    Out.gui("Recalculating because path failed", Out.Mode.Standard);
                    nextPath = null;
                    if (isAir(playerFeet.down())) {//sometimes we are jumping and we make a path that starts in the air and then jumps up, which is impossible
                        Out.gui("DOING THE JANKY THING, WARNING", Out.Mode.Debug);
                        findPathInNewThread(playerFeet.down(), true);
                    } else {
                        findPathInNewThread(playerFeet, true);
                    }
                    return;
                } else {
                    clearPath();
                }
                currentPath = null;
                if (goal.isInGoal(playerFeet)) {
                    Out.gui("All done. At goal", Out.Mode.Standard);
                    nextPath = null;
                } else {
                    Out.gui("Done with segment", Out.Mode.Debug);
                    if (nextPath != null || calculatingNext) {
                        if (calculatingNext) {
                            calculatingNext = false;
                            Out.gui("Patiently waiting to finish", Out.Mode.Debug);
                        } else {
                            currentPath = nextPath;
                            nextPath = null;
                            if (!currentPath.start.equals(playerFeet)) {
                                //Out.gui("The next path starts at " + currentPath.start + " but I'm at " + playerFeet + ". not doing it", true);
                                currentPath = null;
                                findPathInNewThread(playerFeet, true);
                            } else {
                                Out.gui("Going onto next", Out.Mode.Debug);
                                if (!currentPath.goal.isInGoal(currentPath.end)) {
                                    planAhead();
                                }
                            }
                        }
                    } else {
                        Out.gui("Hmm. I'm not actually at the goal. Recalculating.", Out.Mode.Debug);
                        findPathInNewThread(playerFeet, (currentPathGoal != null && goal != null) ? !(currentPathGoal instanceof GoalComposite) && currentPathGoal.toString().equals(goal.toString()) : true);
                    }
                }
            } else {
                if (Action.isWater(theWorld.getBlockState(playerFeet).getBlock())) {
                    //if (Action.isWater(theWorld.getBlockState(playerFeet.down()).getBlock()) || !Action.canWalkOn(playerFeet.down()) || Action.isWater(theWorld.getBlockState(playerFeet.up()).getBlock())) {
                    //if water is deeper than one block, or we can't walk on what's below the water, or our head is in water, jump
                    Out.log("Jumping because in water");
                    MovementManager.jumping = true;
                    //}
                }
                if (nextPath != null) {
                    for (int i = 1; i < 20 && i < nextPath.path.size(); i++) {
                        if (playerFeet.equals(nextPath.path.get(i))) {
                            Out.gui("Jumping to position " + i + " in nextpath", Out.Mode.Debug);
                            currentPath = nextPath;
                            currentPath.calculatePathPosition();
                            nextPath = null;
                            if (!currentPath.goal.isInGoal(currentPath.end)) {
                                planAhead();
                            }
                            break;
                        }
                    }
                }
                LookManager.nudgeToLevel();
            }
        }
        if (Minecraft.theMinecraft.currentScreen != null && (Minecraft.theMinecraft.currentScreen instanceof GuiCrafting || Minecraft.theMinecraft.currentScreen instanceof GuiInventory || Minecraft.theMinecraft.currentScreen instanceof GuiFurnace)) {
            MovementManager.isLeftClick = false;
            MovementManager.leftPressTime = -5;
            numTicksInInventoryOrCrafting++;
            if (numTicksInInventoryOrCrafting > 20 * 20) {
                Minecraft.theMinecraft.thePlayer.closeScreen();
                numTicksInInventoryOrCrafting = 0;
            }
        } else {
            numTicksInInventoryOrCrafting = 0;
        }
        if (isThereAnythingInProgress && Action.isWater(theWorld.getBlockState(playerFeet).getBlock())) {
            if (Action.isWater(theWorld.getBlockState(playerFeet.down()).getBlock()) || !Action.canWalkOn(playerFeet.down()) || Action.isWater(theWorld.getBlockState(playerFeet.up()).getBlock())) {
                //if water is deeper than one block, or we can't walk on what's below the water, or our head is in water, jump
                Out.log("Jumping because in water and pathfinding");
                MovementManager.jumping = true;
            }
        }
        for (Class c : managers) {
            Manager.tick(c, false);
        }
    }
    public static void openInventory() {
        Out.gui("real open", Out.Mode.Debug);
        Minecraft.theMinecraft.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        GuiScreen screen = new GuiInventory(Minecraft.theMinecraft.thePlayer);
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.theMinecraft);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        screen.setWorldAndResolution(Minecraft.theMinecraft, i, j);
        Minecraft.theMinecraft.skipRenderWorld = false;
        Minecraft.theMinecraft.currentScreen = screen;
    }
    public static void slowOpenInventory() {
        Out.gui("slow open", Out.Mode.Debug);
        Minecraft.theMinecraft.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        Minecraft.theMinecraft.displayGuiScreen(new GuiInventory(Minecraft.theMinecraft.thePlayer));
    }
    public static void groundItems() {
        for (Entity enttaounaoeueoaoeu : Minecraft.theMinecraft.theWorld.loadedEntityList) {
            if (enttaounaoeueoaoeu instanceof EntityItem) {
                EntityItem entity = (EntityItem) enttaounaoeueoaoeu;
                ItemStack stack = entity.getEntityItem();
                Item item = stack.getItem();
                BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
            }
        }
    }
    public static boolean isPathFinding() {
        return isThereAnythingInProgress;
    }
    /**
     * Clears movement, clears the current path, and lets go of left click. It
     * purposefully does NOT clear nextPath.
     */
    public static void clearPath() {
        currentPath = null;
        MovementManager.letGoOfLeftClick();
        MovementManager.clearMovement();
    }
    public static String info(BlockPos bp) {
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(bp).getBlock();
        return bp + " " + block + " can walk on: " + Action.canWalkOn(bp) + " can walk through: " + Action.canWalkThrough(bp) + " is full block: " + block.isFullBlock() + " is full cube: " + block.isFullCube() + " is liquid: " + Action.isLiquid(block) + " is flow: " + Action.isFlowing(bp);
    }
    /**
     * Cancel the path
     *
     */
    public static void cancelPath() {
        nextPath = null;
        Combat.target = null;
        currentBuilder = null;
        clearPath();
    }
    public static boolean isAir(BlockPos pos) {
        return Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock().equals(Block.getBlockById(0));
    }
    public static void findPathInNewThread(final boolean talkAboutIt) {
        findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0(), talkAboutIt);
    }
    /**
     * In a new thread, pathfind to target blockpos
     *
     * @param start
     * @param talkAboutIt
     */
    public static void findPathInNewThread(final BlockPos start, final boolean talkAboutIt) {
        if (isThereAnythingInProgress) {
            return;
        }
        isThereAnythingInProgress = true;
        new Thread() {
            @Override
            public void run() {
                if (talkAboutIt) {
                    Out.gui("Starting to search for path from " + start + " to " + goal, Out.Mode.Debug);
                }
                try {
                    currentPath = findPath(start);
                } catch (Exception e) {
                }
                isThereAnythingInProgress = false;
                if (!currentPath.goal.isInGoal(currentPath.end)) {
                    if (talkAboutIt) {
                        Out.gui("I couldn't get all the way to " + goal + ", but I'm going to get as close as I can. " + currentPath.numNodes + " nodes considered", Out.Mode.Standard);
                    }
                    planAhead();
                } else if (talkAboutIt) {
                    Out.gui("Finished finding a path from " + start + " to " + goal + ". " + currentPath.numNodes + " nodes considered", Out.Mode.Debug);
                }
            }
        }.start();
    }
    /**
     * In a new thread, pathfind from currentPath.end to goal. Store resulting
     * path in nextPath (or in currentPath if calculatingNext was set to false
     * in the meantime).
     */
    public static void planAhead() {
        if (isThereAnythingInProgress) {
            return;
        }
        isThereAnythingInProgress = true;
        new Thread() {
            @Override
            public void run() {
                Out.gui("Planning ahead", Out.Mode.Debug);
                calculatingNext = true;
                Path path = findPath(currentPath.end);
                isThereAnythingInProgress = false;
                Out.gui("Done planning ahead " + calculatingNext, Out.Mode.Debug);
                if (calculatingNext) {
                    nextPath = path;
                } else {
                    currentPath = path;
                    if (!plsCancel) {
                        planAhead();
                    }
                }
                calculatingNext = false;
                Out.gui(path.numNodes + " nodes considered, calculated " + path.start + " to " + path.end, Out.Mode.Debug);
            }
        }.start();
    }
    /**
     * Actually do the pathfinding
     *
     * @param start
     * @return
     */
    private static Path findPath(BlockPos start) {
        if (goal == null) {
            Out.gui("babe, please. there is no goal", Out.Mode.Minimal);
            return null;
        }
        try {
            PathFinder pf = new PathFinder(start, goal);
            Path path = pf.calculatePath();
            return path;
        } catch (Exception e) {
            Logger.getLogger(MineBot.class.getName()).log(Level.SEVERE, null, e);
            isThereAnythingInProgress = false;
            return null;
        }
    }
    /**
     * What block is the player looking at
     *
     * @return the position of it
     */
    public static BlockPos whatAreYouLookingAt() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return mc.objectMouseOver.getBlockPos();
        }
        return null;
    }
    public static void switchToBestTool() {
        BlockPos pos = whatAreYouLookingAt();
        if (pos == null) {
            return;
        }
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock();
        if (block.equals(Block.getBlockById(0))) {
            return;
        }
        switchtotool(block);
    }
    public static void switchtotool(Block b) {
        MineBot.switchtotool(b, new ToolSet());
    }
    public static void switchtotool(Block b, ToolSet ts) {
        Minecraft.theMinecraft.thePlayer.inventory.currentItem = ts.getBestSlot(b);
    }
    public static Entity whatEntityAreYouLookingAt() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return mc.objectMouseOver.entityHit;
        }
        return null;
    }
    public static void onPlacedBlock(ItemStack itemStack, BlockPos blockPos) {
        Item item = itemStack.getItem();
    }
    private static final HashMap<Integer, Boolean> lockedKeys = new HashMap<Integer, Boolean>();
    public static Boolean isKeyLocked(int i) {
        return lockedKeys.get(i);
    }
    public static void setKeyLocked(int i, Boolean b) {
        lockedKeys.put(i, b);
    }
    public static boolean isKeyDown(int i) {
        return lockedKeys.get(i) == null ? Keyboard.isKeyDown(i) : lockedKeys.get(i);
    }
    public static List<String> getDebugGui() {
        if (!overrideF3) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        list.add("§5[§dMineBot§5]§f");
        for (Class<? extends Manager> c : managers) {
            list.add("§r[§" + (Manager.enabled(c) ? "a" : "c") + c.getSimpleName() + "§r]");
        }
        list.add("");
        list.add("Current goal: " + goal);
        list.add("");
        if (currentPath != null) {
            list.add("Current path start: " + currentPath.start);
            list.add("Current path end: " + currentPath.end);
            list.add("Current path ends in current goal: " + (goal == null ? null : goal.isInGoal(currentPath.end)));
            if (!currentPath.goal.equals(goal)) {
                list.add("Current path goal: " + currentPath.goal);
            }
        }
        if (nextPath != null) {
            list.add("");
            list.add("Next path start: " + nextPath.start);
            list.add("Next path end: " + nextPath.end);
            list.add("Next path ends in current goal: " + (goal == null ? null : goal.isInGoal(nextPath.end)));
        }
        return list;
    }
    public boolean isNull() throws NullPointerException {
        NullPointerException up = new NullPointerException("You are disgusting");
        throw up;
        //To use this surround the call to this message with a try-catch then compare the length of the NullPointerException.getMessage()
    }
}
