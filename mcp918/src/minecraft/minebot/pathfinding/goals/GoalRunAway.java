/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.goals;

import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class GoalRunAway implements Goal {
    public final int x;
    public final int z;
    final double distanceSq;
    public GoalRunAway(int x, int z, double distance) {
        this.x = x;
        this.z = z;
        this.distanceSq = distance * distance;
    }
    @Override
    public boolean isInGoal(BlockPos pos) {
        int diffX = pos.getX() - x;
        int diffZ = pos.getZ() - z;
        double distSq = diffX * diffX + diffZ * diffZ;
        return distSq > distanceSq;
    }
    @Override
    public double heuristic(BlockPos pos) {//mostly copied from GoalBlock
        double xDiff = pos.getX() - this.x;
        double zDiff = pos.getZ() - this.z;
        return -GoalXZ.calculate(xDiff, zDiff);
    }
    @Override
    public String toString() {
        return "GoalRunAwayFrom{x=" + x + ",z=" + z + "}";
    }
}
