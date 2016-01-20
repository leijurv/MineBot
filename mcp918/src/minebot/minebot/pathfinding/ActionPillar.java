/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionPillar extends ActionPlaceOrBreak {
    public ActionPillar(BlockPos from, BlockPos to) {
        super(from, to, new BlockPos[]{to, new BlockPos(to.getX(), to.getY() + 1, to.getZ())}, new BlockPos[]{new BlockPos(to.getX(), to.getY() - 1, to.getZ())});
    }
    @Override
    public int cost() {
        if (canWalkOn(toPlace[0])) {
            if (canWalkThrough(toBreak[0]) && canWalkThrough(toBreak[1])) {
                return 1;
            }
            System.out.println("Can't walk through " + Minecraft.theMinecraft.theWorld.getBlockState(toBreak[0]).getBlock() + " " + Minecraft.theMinecraft.theWorld.getBlockState(toBreak[1]).getBlock());
        } else {
            System.out.println("Can't walk on " + Minecraft.theMinecraft.theWorld.getBlockState(toPlace[0]).getBlock());
        }
        return 1000;
    }
}
