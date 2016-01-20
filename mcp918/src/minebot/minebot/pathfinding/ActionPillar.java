/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import minebot.MineBot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionPillar extends ActionPlaceOrBreak {
    public ActionPillar(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{new BlockPos(end.getX(), end.getY() + 1, end.getZ())}, new BlockPos[]{start});
    }
    @Override
    protected double calculateCost() {
        return 1000000 + getTotalHardnessOfBlocksToBreak() * 10;
    }
    @Override
    protected boolean tick0() {
        MineBot.lookAtBlock(new BlockPos(from.getX(), from.getY() - 1, from.getZ()), true);
        MineBot.jumping = true;
        Minecraft.theMinecraft.rightClickMouse();
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos((int) thePlayer.posX, (int) thePlayer.posY, (int) thePlayer.posZ);
        if (whereAmI.equals(to)) {
            System.out.println("Done walking to " + to);
            MineBot.jumping = false;
            return true;
        }
        return false;
    }
}
