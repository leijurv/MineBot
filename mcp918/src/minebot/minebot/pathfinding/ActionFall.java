/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class ActionFall extends ActionPlaceOrBreak {
    public ActionFall(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{new BlockPos(end.getX(), end.getY() + 1, end.getZ()), end, new BlockPos(end.getX(), end.getY() - 1, end.getZ())}, new BlockPos[0]);
    }
    @Override
    protected double calculateCost() {
        return 10 + getTotalHardnessOfBlocksToBreak() * 10;
    }
}
