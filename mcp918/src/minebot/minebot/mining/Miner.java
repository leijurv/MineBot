/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.mining;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import minebot.MineBot;
import static minebot.pathfinding.Action.canWalkOn;
import static minebot.pathfinding.Action.canWalkThrough;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author avecowa
 */
public class Miner {
    private static Queue<BlockPos> blocks = new ConcurrentLinkedQueue<BlockPos>();
    private static boolean isMining = false;
    public static EnumFacing direction = EnumFacing.NORTH;
    public static void goMining() {
        isMining(true);
        MineBot.getToY(13);//optimal for diamonds
    }
    
    public static void stopMining() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void mineblocks(int howMany, EnumFacing direction){
        Miner.direction = direction;
        mineblocks(howMany);
    }
    
    public static void mineblocks(EnumFacing direction){
        Miner.direction = direction;
        mineblocks();
    }
    
    public static void mineblocks(){
        mineblocks(5);
    }
    
    public static void mineblocks(int howMany){
        if(direction==EnumFacing.DOWN || direction==EnumFacing.UP){
            throw new IllegalArgumentException("I can't mining "+direction.getName());
        }
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos position = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        for(int i = 0; i < howMany; i++){
            blocks.add(position = position.offset(direction));
            blocks.add(position.up());
        }
    }
    
    
    public static synchronized boolean isMining(boolean isMining){
        boolean toReturn = Miner.isMining;
        Miner.isMining = isMining;
        return toReturn;
    }
    
    public static synchronized boolean isMining(){
        return isMining;
    }
    
    public static void tick() {
        if(!isMining()){
            System.out.println("Not mining");
            return;
        }
        if(blocks.size() < 10)
            mineblocks();
        if (canWalkThrough(blocks.peek())) {
           blocks.poll();
           return;
        }
        if(!MineBot.lookAtBlock(blocks.peek(), true))
            return;
        if (MineBot.whatAreYouLookingAt() != null) {
                    MineBot.switchtotool(Minecraft.theMinecraft.theWorld.getBlockState(MineBot.whatAreYouLookingAt()).getBlock());
        }
        MineBot.isLeftClick = true;
    }
}
