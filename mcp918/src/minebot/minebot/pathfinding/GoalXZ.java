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
public class GoalXZ implements Goal {
    final int x;
    final int z;
    public GoalXZ(int x, int z) {
        this.x = x;
        this.z = z;
    }
    @Override
    public boolean isInGoal(BlockPos pos) {
        return pos.getX() == x && pos.getZ() == z;
    }
    @Override
    public double heuristic(BlockPos pos) {
        double xDiff = pos.getX() - this.x;
        double zDiff = pos.getZ() - this.z;
        double pythaDist = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double heuristic = 0;
        heuristic += Math.abs(xDiff) * Action.WALK_ONE_BLOCK_COST * 1.1;
        heuristic += Math.abs(zDiff) * Action.WALK_ONE_BLOCK_COST * 1.1;
        heuristic += pythaDist / 10 * Action.WALK_ONE_BLOCK_COST;
        return heuristic;
    }
    @Override
    public String toString() {
        return "Goal{x=" + x + ",z=" + z + "}";
    }
}
