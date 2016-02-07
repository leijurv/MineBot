/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import static minebot.MineBot.findPathInNewThread;
import static minebot.MineBot.goal;
import minebot.pathfinding.goals.GoalBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.EmptyChunk;

/**
 *
 * @author leijurv
 */
public class Memory {
    public static HashMap<Block, BlockMemory> blockMemory = new HashMap();
    public static HashMap<String, BlockPos> playerLocationMemory = new HashMap();
    public static HashMap<String, BlockPos> goalMemory = new HashMap();
    public static ArrayList<String> playersCurrentlyInRange = new ArrayList();
    public static Thread scanThread = null;
    public static Block air = null;

    public static class BlockMemory {
        final Block block;
        final HashSet<BlockPos> knownPositions;//idk whether to use hashset or arraylist here...
        public BlockMemory(Block block) {
            this.block = block;
            this.knownPositions = new HashSet();
        }
        public void put(BlockPos pos) {
            if (knownPositions.size() > 100) {
                return;
            }
            knownPositions.add(pos);
        }
        public BlockPos getOne() {
            for (BlockPos pos : knownPositions) {
                return pos;
            }
            return null;
        }
    }
    public static void tick() {
        if (air == null) {
            air = Block.getBlockById(0);
        }
        playersCurrentlyInRange.clear();
        for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
            String blah = pl.getName().trim().toLowerCase();
            playerLocationMemory.put(blah, new BlockPos(pl.posX, pl.posY, pl.posZ));
            playersCurrentlyInRange.add(blah);
        }
        if (scanThread == null) {
            scanThread = new Thread() {
                @Override
                public void run() {
                    GuiScreen.sendChatMessage("Starting passive block scan thread", true);
                    while (true) {
                        if (Minecraft.theMinecraft == null || Minecraft.theMinecraft.thePlayer == null || Minecraft.theMinecraft.theWorld == null) {
                            return;
                        }
                        GuiScreen.sendChatMessage("Beginning passive block scan", true);
                        long start = System.currentTimeMillis();
                        scan();
                        long end = System.currentTimeMillis();
                        GuiScreen.sendChatMessage("Passive block scan over after " + (end - start) + "ms", true);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
                            return;
                        }
                    }
                }
            };
            scanThread.start();
        }
    }
    public static String findCommand(String block) {
        String lower = block.toLowerCase();
        for (Block type : blockMemory.keySet()) {
            if (type.toString().toLowerCase().contains(block)) {
                BlockPos pos = blockMemory.get(type).getOne();
                if (pos != null) {
                    return block + " at " + pos;
                }
            }
        }
        return "none";
    }
    public static void scan() {
        BlockPos playerFeet = Minecraft.theMinecraft.thePlayer.getPosition0();
        int X = playerFeet.getX();
        int Y = playerFeet.getY();
        int Z = playerFeet.getZ();
        for (int x = X; x >= X - SCAN_DIST && blockLoaded(new BlockPos(x, Y, Z)); x--) {
            for (int z = Z; z >= Z - SCAN_DIST && blockLoaded(new BlockPos(x, Y, z)); z--) {
                for (int y = 0; y <= 128; y++) {
                    scanBlock(new BlockPos(x, y, z));
                }
            }
            for (int z = Z; z <= Z + SCAN_DIST && blockLoaded(new BlockPos(x, Y, z)); z++) {
                for (int y = 0; y <= 128; y++) {
                    scanBlock(new BlockPos(x, y, z));
                }
            }
        }
        for (int x = X; x <= X + SCAN_DIST && blockLoaded(new BlockPos(x, Y, Z)); x++) {
            for (int z = Z; z >= Z - SCAN_DIST && blockLoaded(new BlockPos(x, Y, z)); z--) {
                for (int y = 0; y <= 128; y++) {
                    scanBlock(new BlockPos(x, y, z));
                }
            }
            for (int z = Z; z <= Z + SCAN_DIST && blockLoaded(new BlockPos(x, Y, z)); z++) {
                for (int y = 0; y <= 128; y++) {
                    scanBlock(new BlockPos(x, y, z));
                }
            }
        }
    }
    public static final int SCAN_DIST = 100;
    public static void scanBlock(BlockPos pos) {
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock();
        if (air.equals(block)) {
            return;
        }
        BlockMemory memory = getMemory(block);
        memory.put(pos);
    }
    public static BlockMemory getMemory(Block block) {
        BlockMemory cached = blockMemory.get(block);
        if (cached != null) {
            return cached;
        }
        BlockMemory n = new BlockMemory(block);
        blockMemory.put(block, n);
        return n;
    }
    public static boolean blockLoaded(BlockPos pos) {
        return !(Minecraft.theMinecraft.theWorld.getChunkFromBlockCoords(pos) instanceof EmptyChunk);
    }
    public static String gotoCommand(String targetName) {
        for (String name : playerLocationMemory.keySet()) {
            if (name.contains(targetName) || targetName.contains(name)) {
                MineBot.goal = new GoalBlock(playerLocationMemory.get(name));
                findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0(), true);
                return "Pathing to " + name + " at " + goal;
            }
        }
        /*
         for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
         String blah = pl.getName().trim().toLowerCase();
         if (blah.contains(name) || name.contains(blah)) {
         BlockPos pos = new BlockPos(pl.posX, pl.posY, pl.posZ);
         goal = new GoalBlock(pos);
         findPathInNewThread(Minecraft.theMinecraft.thePlayer.getPosition0(), true);
         return "Pathing to " + pl.getName() + " at " + goal;
         }
         }*/
        return "Couldn't find " + targetName;
    }
    public static String playerCommand(String targetName) {
        String resp = "";
        for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
            resp += "(" + pl.getName() + "," + pl.posX + "," + pl.posY + "," + pl.posZ + ")\n";
            if (pl.getName().equals(targetName)) {
                BlockPos pos = new BlockPos(pl.posX, pl.posY, pl.posZ);
                goal = new GoalBlock(pos);
                return "Set goal to " + goal;
            }
        }
        for (String x : resp.split("\n")) {
            GuiScreen.sendChatMessage(x, true);
        }
        if (targetName.equals("")) {
            return "";
        }
        return "Couldn't find " + targetName;
    }
}
