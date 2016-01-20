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
        super(start, end, new BlockPos[]{new BlockPos(end.getX(), end.getY() + 1, end.getZ()), end, new BlockPos(end.getX(), end.getY() + 2, end.getZ())}, new BlockPos[]{new BlockPos(end.getX(), end.getY() - 1, end.getZ())});
    }
    @Override
    protected double calculateCost() {
        if (!canWalkOn(blocksToPlace[0])) {
            return 10000;
        }
        return WALK_ONE_BLOCK_COST + FALL_ONE_BLOCK_COST + getTotalHardnessOfBlocksToBreak() * 10;
    }
}
