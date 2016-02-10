///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package minebot;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import static minebot.MineBot.sketchyStealer;
//import static minebot.MineBot.tickNumber;
//import minebot.util.Manager;
//import net.minecraft.block.Block;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.gui.inventory.GuiChest;
//import net.minecraft.client.gui.inventory.GuiContainer;
//import net.minecraft.util.BlockPos;
//import net.minecraft.world.World;
//
///**
// *
// * @author leijurv
// */
//public class SketchyStealer extends Manager{
//    public static ArrayList<BlockPos> alreadyStolenFrom = new ArrayList<BlockPos>();
//    public void onTick() {
//        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
//        World theWorld = Minecraft.theMinecraft.theWorld;
//        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
//        if (Minecraft.theMinecraft.currentScreen != null && Minecraft.theMinecraft.currentScreen instanceof GuiChest) {
//            GuiContainer contain = (GuiContainer) Minecraft.theMinecraft.currentScreen;
//            if (sketchyStealer) {
//                if (contain.yoyoyo()) {
//                    if (tickNumber % 2 == 0) {
//                        try {
//                            contain.mouseClicked(0, 0, 0);
//                        } catch (IOException ex) {
//                            Logger.getLogger(MineBot.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                    if (tickNumber % 2 == 1) {
//                        contain.mouseReleased(0, 0, 0);
//                    }
//                } else {
//                    Minecraft.theMinecraft.thePlayer.closeScreen();
//                    BlockPos p = MineBot.whatAreYouLookingAt();
//                    GuiScreen.sendChatMessage("Finished stealing from " + p, true);
//                    alreadyStolenFrom.add(p);
//                }
//            }
//        }
//        if (sketchyStealer && Minecraft.theMinecraft.currentScreen == null) {
//            for (int x = playerFeet.getX() - 5; x < playerFeet.getX() + 5; x++) {//TODO increase the range, and make it actually pathfind to the chests
//                for (int y = playerFeet.getY() - 5; y < playerFeet.getY() + 5; y++) {
//                    for (int z = playerFeet.getZ() - 5; z < playerFeet.getZ() + 5; z++) {
//                        BlockPos pos = new BlockPos(x, y, z);
//                        if (alreadyStolenFrom.contains(pos)) {
//                            continue;
//                        }
//                        if (theWorld.getBlockState(pos).getBlock().equals(Block.getBlockFromName("minecraft:chest"))) {
//                            if (LookManager.couldIReach(pos)) {
//                                if (LookManager.lookAtBlock(pos, true)) {
//                                    Minecraft.theMinecraft.rightClickMouse();
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Override
//    protected void onCancel() {
//        
//    }
//
//    @Override
//    protected void onStart() {
//        
//    }
//}
