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
            if (!blocksToBreak[i].equals(Block.getBlockById(0)))
                sum += 1 / ts.getStrVsBlock(blocksToBreak[i]);
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
                MineBot.lookAtBlock(positionsToBreak[i], true);//look at the block we are breaking
                if (!positionsToBreak[i].equals(MineBot.whatAreYouLookingAt())) {
                    System.out.println("Wrong");
                    return false;
                }
                switchtotool(blocksToBreak[i]);
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
            }
        }
        return tick0();
    }
    final List<Item> acceptable = Arrays.asList(new Item[]{Item.getByNameOrId("minecraft:dirt"), Item.getByNameOrId("minecraft:cobblestone")});
    public void switchtothrowaway() {
        //System.out.println("b: " + b);
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        //System.out.println("inv: " + Arrays.toString(inv));
        //System.out.println("best: " + best);
        float value = 0;
        for (byte i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                item = new ItemStack(Item.getByNameOrId("minecraft:apple"));
            }
            if (acceptable.contains(item.getItem())) {
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
    protected abstract boolean tick0();
}
