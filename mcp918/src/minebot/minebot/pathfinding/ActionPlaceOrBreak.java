/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.Arrays;
import java.util.List;
import minebot.MineBot;
import net.minecraft.block.Block;
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
        for (int i = 0; i < blocksToBreak.length; i++) {
            if (!blocksToBreak[i].equals(Block.getBlockById(0)) && !canWalkThrough(positionsToBreak[i])) {
                if (avoidBreaking(positionsToBreak[i])) {
                    sum += 1000000;
                }
                sum += 1 / ts.getStrVsBlock(blocksToBreak[i], positionsToBreak[i]);
            }
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
        for (int i = 0; i < blocksToBreak.length; i++) {
            if (!canWalkThrough(positionsToBreak[i])) {
                //System.out.println("Breaking " + blocksToBreak[i] + " at " + positionsToBreak[i]);
                if (!MineBot.lookAtBlock(positionsToBreak[i], true)) {
                    return false;
                    //look at the block we are breaking
                }
                if (!positionsToBreak[i].equals(MineBot.whatAreYouLookingAt())) {//hmmm, our crosshairs are looking at the wrong block
                    //TODO add a timer here, and if we are stuck looking at the wrong block for more than 1 second, do something
                    //(it cant take longer than twenty ticks, because the MineBot.MAX_YAW_CHANGE_PER_TICK=18, and 18*20 = 360°
                    System.out.println("Wrong");
                    return false;
                }
                switchtotool(Minecraft.theMinecraft.theWorld.getBlockState(positionsToBreak[i]).getBlock());
                MineBot.isLeftClick = true;//hold down left click
                if (canWalkThrough(positionsToBreak[i])) {
                    MineBot.letGoOfLeftClick();
                    System.out.println("Done breaking " + blocksToBreak[i] + " at " + positionsToBreak[i]);
                }
                return false;
            }
        }
        MineBot.letGoOfLeftClick();//sometimes it keeps on left clicking so we need this here (yes it scares me too)
        for (int i = 0; i < blocksToPlace.length; i++) {
            if (!canWalkOn(positionsToPlace[i])) {
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
    final List<Item> ACCEPTABLE_THROWAWAY_ITEMS = Arrays.asList(new Item[]{Item.getByNameOrId("minecraft:dirt"), Item.getByNameOrId("minecraft:cobblestone")});
    public void switchtothrowaway() {
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        for (byte i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                item = new ItemStack(Item.getByNameOrId("minecraft:apple"));
            }
            if (ACCEPTABLE_THROWAWAY_ITEMS.contains(item.getItem())) {
                p.inventory.currentItem = i;
                return;
            }
        }
        GuiScreen.sendChatMessage("bb pls get me some blocks. dirt or cobble", true);
    }
    public void switchtotool(Block b) {
        this.switchtotool(b, new ToolSet());
    }
    public void switchtotool(Block b, ToolSet ts) {
        Minecraft.theMinecraft.thePlayer.inventory.currentItem = ts.getBestSlot(b);
    }
    /**
     * Do the actual tick. This function can assume that all blocks in
     * positionsToBreak are now walk-through-able.
     *
     * @return
     */
    protected abstract boolean tick0();
}
