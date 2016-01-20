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
public class ActionClimb extends ActionPlaceOrBreak {
    public ActionClimb(BlockPos start, BlockPos end) {
        super(start, end, new BlockPos[]{new BlockPos(start.getX(), start.getY() + 2, start.getZ()), end, new BlockPos(end.getX(), end.getY() + 1, end.getZ())}, new BlockPos[]{new BlockPos(end.getX(), end.getY() - 1, end.getZ())});
    }
    @Override
    protected double calculateCost() {
        if (!canWalkOn(blocksToPlace[0])) {
            return 10000;
        }
        return 10 + getTotalHardnessOfBlocksToBreak() * 10;
    }
}
