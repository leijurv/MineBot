/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import static minebot.MineBot.tickNumber;
import minebot.pathfinding.goals.GoalComposite;
import minebot.pathfinding.goals.GoalGetToBlock;
import minebot.util.ChatCommand;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;

/**
 *
 * @author avecowa
 */
public class AnotherStealer {

    public static ArrayList<BlockPos> alreadyStolenFrom = new ArrayList<BlockPos>();
    public static boolean chestStuff = false;
    public static boolean stuff = false;
    public static BlockPos current = null;
    public static boolean dropitem = false;
    private static final Block CHEST = Block.getBlockFromName("chest");

    public static void onTick() {
        //try{
        if (MineBot.isThereAnythingInProgress || MineBot.currentPath != null) {
            System.out.println(MineBot.currentPath);
            return;
        }
        if (stuff) {
            stuff = false;
            ArrayList<BlockPos> chests = (ArrayList<BlockPos>) (Memory.getMemory(Block.getBlockFromName("chest")).knownPositions.clone());
            chests.removeAll(alreadyStolenFrom);
            if (chests.isEmpty()) {
                return;
            }
            BlockPos[] goals = GoalGetToBlock.ajacentBlocks(chests.get(0));
            for (int i = 1; i < chests.size(); i++) {
                goals = Autorun.concat(goals, GoalGetToBlock.ajacentBlocks(chests.get(i)));
            }
            MineBot.goal = new GoalComposite(goals);
            ChatCommand.path("path");
            return;
        }
        BlockPos near = getAjacentChest();
        if (near == null) {
            stuff = true;
            return;
        }
        if (MineBot.whatAreYouLookingAt().equals(near)) {
            if (chestStuff) {
                EntityPlayerSP player = Minecraft.theMinecraft.thePlayer;
                WorldClient world = Minecraft.theMinecraft.theWorld;
                if(Minecraft.theMinecraft.currentScreen == null) {
                    chestStuff = false;
                    return;
                }
                if (!(Minecraft.theMinecraft.currentScreen instanceof GuiChest)) {
                    player.closeScreen();
                    chestStuff = false;
                    return;
                }
                GuiChest contain = (GuiChest) Minecraft.theMinecraft.currentScreen;
                contain.inventorySlots
                if (tickNumber % 2 == 0) {
                        try {
                            contain.mouseClicked(0, 0, 0);
                        } catch (IOException ex) {
                            Logger.getLogger(MineBot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (tickNumber % 2 == 1) {
                        contain.mouseReleased(0, 0, 0);
                    }
                return;
            }
            chestStuff = true;
            MineBot.isRightClick = true;
            current = MineBot.whatAreYouLookingAt();
            return;
        }
        LookManager.lookAtBlock(near, true);
        return;
//        if(chestStuff){
//            GuiScreen.sendChatMessage("Cheststuff");
//        } else {
//            if(Minecraft.theMinecraft.theWorld.getBlockState(MineBot.whatAreYouLookingAt()).getBlock()!=CHEST){
//                LookManager.lookAtBlock(getAjacentChest(), stuff);
//                MineBot.switchToBestTool();
//                MineBot.isLeftClick = true;
//            } else {
//            MineBot.isRightClick = true;
//            chestStuff = true;
//            }
//        } 
        //}catch (Exception e) { System.err.println(Arrays.toString(e.getStackTrace())); }

    }

    public static BlockPos getAjacentChest() {
        BlockPos[] pos = GoalGetToBlock.ajacentBlocks(Minecraft.theMinecraft.thePlayer.getPosition0());
        WorldClient w = Minecraft.theMinecraft.theWorld;
        for (BlockPos p : pos) {
            if (!alreadyStolenFrom.contains(p) && w.getBlockState(p).getBlock().equals(CHEST)) {
                return p;
            }
        }
        return null;
    }
}
