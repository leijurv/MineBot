/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.mining;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import minebot.MineBot;
import static minebot.pathfinding.Action.canWalkOn;
import static minebot.pathfinding.Action.canWalkThrough;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 *
 * @author avecowa
 */
public class Miner {

    private static Queue<BlockPos> blocks = new ConcurrentLinkedQueue<BlockPos>();
    private static Deque<BlockPos> ores = new LinkedList<BlockPos>();
    private static boolean isMining = false;
    private static boolean wasMining = false;
    public static EnumFacing direction = EnumFacing.NORTH;

    public static void goMining() {
        isMining(true);
        MineBot.getToY(13);//optimal for diamonds
    }

    public static void stopMining() {
        isMining(false);
        wasMining(false);
        blocks.clear();
        ores.clear();
    }

    public static void mineblocks(int howMany, EnumFacing direction) {
        Miner.direction = direction;
        mineblocks(howMany);
    }

    public static void mineblocks(EnumFacing direction) {
        Miner.direction = direction;
        mineblocks();
    }

    public static void mineblocks() {
        mineblocks(5);
    }

    public static void mineblocks(int howMany) {
        if (direction == EnumFacing.DOWN || direction == EnumFacing.UP) {
            throw new IllegalArgumentException("I can't mining " + direction.getName());
        }
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos position = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        for (int i = 0; i < howMany; i++) {
            blocks.add(position = position.offset(direction));
            blocks.add(position.up());
        }
    }

    public static synchronized boolean isMining(boolean isMining) {
        boolean toReturn = Miner.isMining;
        Miner.isMining = isMining;
        return toReturn;
    }

    public static synchronized boolean isMining() {
        return isMining;
    }
    
    public static synchronized boolean wasMining(boolean wasMining) {
        boolean toReturn = Miner.wasMining;
        Miner.wasMining = wasMining;
        return toReturn;
    }

    public static synchronized boolean wasMining() {
        return wasMining;
    }

    public static void tick() {
        if(!isMining && wasMining()){
            isMining(true);
            wasMining(false);
            
        }
        if (!isMining()) {
            System.out.println("Not mining");
            return;
        }
        MineBot.forward = true;
        if (blocks.size() < 10) {
                mineblocks();
        }
        tryToMine(ores.isEmpty() ? blocks.peek() : ores.peek(), ores.isEmpty());

    }

    public static void justMined(BlockPos block, boolean ore) {
        World world = Minecraft.theMinecraft.theWorld;
        for (EnumFacing ef : EnumFacing.values()) {
            if (world.getBlockState(block.offset(ef)).getBlock().toString().contains("ore")) {
                ores.addFirst(block.offset(ef));
            }
        }
        if(ores.isEmpty() && ore){
            wasMining(true);
            isMining(false);
            MineBot.goMiningInNewThread();
        }
            
    }

    public static void tryToMine(BlockPos block, boolean ore) {
        if (canWalkThrough(block)) {
            System.out.println("Just Mined "+block+" and ores.isEmpty() is "+ores.isEmpty());
            justMined((ore ? blocks.poll() : ores.poll()), ore);
            return;
        }
        if (!MineBot.lookAtBlock(blocks.peek(), true)) {
            return;
        }
        if (MineBot.whatAreYouLookingAt() != null) {
            MineBot.switchtotool(Minecraft.theMinecraft.theWorld.getBlockState(MineBot.whatAreYouLookingAt()).getBlock());
            MineBot.isLeftClick = true;
        }
    }
}
