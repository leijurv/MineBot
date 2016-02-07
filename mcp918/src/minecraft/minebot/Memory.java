/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.ArrayList;
import java.util.HashMap;
import static minebot.MineBot.findPathInNewThread;
import static minebot.MineBot.goal;
import minebot.pathfinding.goals.GoalBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class Memory {
    public static HashMap<Block, ArrayList<BlockPos>> blockMemory = new HashMap();
    public static HashMap<String, BlockPos> playerLocationMemory = new HashMap();
    public static HashMap<String, BlockPos> goalMemory = new HashMap();
    public static void tick() {
        for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
            String blah = pl.getName().trim().toLowerCase();
            playerLocationMemory.put(blah, new BlockPos(pl.posX, pl.posY, pl.posZ));
        }
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
}
