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
public class GoalBlock implements Goal {
    final int x, y, z;
    public GoalBlock(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }
    public GoalBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public boolean isInGoal(BlockPos pos) {
        return pos.getX() == this.x && pos.getY() == this.y && pos.getZ() == this.z;
    }
    @Override
    public double heuristic(BlockPos pos) {
        double xDiff = pos.getX() - this.x;
        double yDiff = pos.getY() - this.y;
        double zDiff = pos.getZ() - this.z;
        return ((Math.abs(xDiff) + Math.abs(yDiff) + Math.abs(zDiff)) + Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff) / 5) * Action.WALK_ONE_BLOCK_COST;
    }
}
