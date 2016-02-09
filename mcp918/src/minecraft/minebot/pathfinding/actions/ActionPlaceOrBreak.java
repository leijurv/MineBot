/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.actions;

import minebot.util.ToolSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import minebot.LookManager;
import minebot.MineBot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public abstract class ActionPlaceOrBreak extends Action {
    public final BlockPos[] positionsToBreak;//the positions that need to be broken before this action can ensue
    public final BlockPos[] positionsToPlace;//the positions where we need to place a block before this aciton can ensue
    public final Block[] blocksToBreak;//the blocks at those positions
    public final Block[] blocksToPlace;
    public ActionPlaceOrBreak(BlockPos start, BlockPos end, BlockPos[] toBreak, BlockPos[] toPlace) {
        super(start, end);
        this.positionsToBreak = toBreak;
        this.positionsToPlace = toPlace;
        blocksToBreak = new Block[positionsToBreak.length];
        blocksToPlace = new Block[positionsToPlace.length];
        for (int i = 0; i < blocksToBreak.length; i++) {
            blocksToBreak[i] = Minecraft.theMinecraft.theWorld.getBlockState(positionsToBreak[i]).getBlock();
        }
        for (int i = 0; i < blocksToPlace.length; i++) {
            blocksToPlace[i] = Minecraft.theMinecraft.theWorld.getBlockState(positionsToPlace[i]).getBlock();
        }
    }
    public double getTotalHardnessOfBlocksToBreak() {//of all the blocks we need to break before starting this action, what's the sum of how hard they are (phrasing)
        ToolSet ts = new ToolSet();
        return this.getTotalHardnessOfBlocksToBreak(ts);
    }
    public double getTotalHardnessOfBlocksToBreak(ToolSet ts) {
        double sum = 0;
        HashSet<BlockPos> toBreak = new HashSet();
        for (int i = 0; i < positionsToBreak.length; i++) {
            toBreak.add(positionsToBreak[i]);
            BlockPos tmp = positionsToBreak[i].up();
            while (canFall(tmp)) {
                toBreak.add(tmp);
                tmp = tmp.up();
            }
        }
        for (BlockPos pos : toBreak) {
            sum += getHardness(ts, Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock(), pos);
        }
        if (!MineBot.allowBreakOrPlace || !MineBot.hasThrowaway) {
            for (int i = 0; i < blocksToPlace.length; i++) {
                if (!canWalkOn(positionsToPlace[i])) {
                    sum += 1000000;
                }
            }
        }
        return sum;
    }
    public static boolean canFall(BlockPos pos) {
        return Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock() instanceof BlockFalling;
    }
    public static double getHardness(ToolSet ts, Block block, BlockPos position) {
        double sum = 0;
        if (!block.equals(Block.getBlockById(0)) && !canWalkThrough(position)) {
            if (avoidBreaking(position)) {
                sum += 1000000;
            }
            if (!MineBot.allowBreakOrPlace) {
                sum += 1000000;
            }
            double m = Block.getBlockFromName("minecraft:crafting_table").equals(block) ? 10 : 1;
            sum += m / ts.getStrVsBlock(block, position);
        }
        return sum;
    }
    @Override
    public String toString() {
        return this.getClass() + " place " + Arrays.asList(blocksToPlace) + " break " + Arrays.asList(blocksToBreak) + " cost " + cost() + " break cost " + getTotalHardnessOfBlocksToBreak();
    }
    @Override
    public boolean tick() {
        //breaking first
        for (int i = 0; i < positionsToBreak.length; i++) {
            if (!canWalkThrough(positionsToBreak[i])) {
                if (!MineBot.allowBreakOrPlace) {
                    GuiScreen.sendChatMessage("BB I can't break this =(((", true);
                    return false;
                }
                //System.out.println("Breaking " + blocksToBreak[i] + " at " + positionsToBreak[i]);
                boolean lookingInCorrectDirection = LookManager.lookAtBlock(positionsToBreak[i], true);
                //TODO decide if when actuallyLookingAtTheBlock==true then should it still keep moving the look direction to the exact center of the block
                boolean actuallyLookingAtTheBlock = positionsToBreak[i].equals(MineBot.whatAreYouLookingAt());
                if (!(lookingInCorrectDirection || actuallyLookingAtTheBlock)) {
                    return false;
                }
                /*if (!positionsToBreak[i].equals(MineBot.whatAreYouLookingAt())) {//hmmm, our crosshairs are looking at the wrong block
                 //TODO add a timer here, and if we are stuck looking at the wrong block for more than 1 second, do something
                 //(it cant take longer than twenty ticks, because the MineBot.MAX_YAW_CHANGE_PER_TICK=18, and 18*20 = 360°
                 System.out.println("Wrong");
                 return false;
                 }*/
                if (MineBot.whatAreYouLookingAt() != null) {
                    MineBot.switchtotool(Minecraft.theMinecraft.theWorld.getBlockState(MineBot.whatAreYouLookingAt()).getBlock());
                }
                MineBot.isLeftClick = true;//hold down left click
                if (canWalkThrough(positionsToBreak[i])) {
                    MineBot.letGoOfLeftClick();
                    System.out.println("Done breaking " + blocksToBreak[i] + " at " + positionsToBreak[i]);
                }
                return false;
            }
        }
        MineBot.letGoOfLeftClick();//sometimes it keeps on left clicking so we need this here (yes it scares me too)
        for (int i = 0; i < positionsToPlace.length; i++) {
            if (!canWalkOn(positionsToPlace[i])) {
                if (!MineBot.allowBreakOrPlace) {
                    GuiScreen.sendChatMessage("BB I can't place this =(((", true);
                    return false;
                }
                //MineBot.lookAtBlock(positionsToPlace[i], true);
                //System.out.println("CANT DO IT. CANT WALK ON " + blocksToPlace[i] + " AT " + positionsToPlace[i]);
                //one of the blocks that needs to be there isn't there
                //so basically someone mined out our path from under us
                //
                //this doesn't really do anything, because all the cases for positionToPlace are handled in their respective action tick0s (e.g. pillar and bridge)
            }
        }
        return tick0();
    }
    //I dont want to make this static, because then it might be executed before Item gets initialized
    private static List<Item> ACCEPTABLE_THROWAWAY_ITEMS = null;
    private static void set() {
        if (ACCEPTABLE_THROWAWAY_ITEMS != null) {
            return;
        }
        ACCEPTABLE_THROWAWAY_ITEMS = Arrays.asList(new Item[]{Item.getByNameOrId("minecraft:dirt"), Item.getByNameOrId("minecraft:cobblestone")});
    }
    public boolean switchtothrowaway(boolean message) {
        set();
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        for (byte i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                item = new ItemStack(Item.getByNameOrId("minecraft:apple"));
            }
            if (ACCEPTABLE_THROWAWAY_ITEMS.contains(item.getItem())) {
                p.inventory.currentItem = i;
                return true;
            }
        }
        if (message) {
            GuiScreen.sendChatMessage("bb pls get me some blocks. dirt or cobble", true);
        }
        return false;
    }
    public static boolean hasthrowaway() {
        set();
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        for (byte i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                item = new ItemStack(Item.getByNameOrId("minecraft:apple"));
            }
            if (ACCEPTABLE_THROWAWAY_ITEMS.contains(item.getItem())) {
                return true;
            }
        }
        return false;
    }
    /**
     * Do the actual tick. This function can assume that all blocks in
     * positionsToBreak are now walk-through-able.
     *
     * @return
     */
    protected abstract boolean tick0();
}
