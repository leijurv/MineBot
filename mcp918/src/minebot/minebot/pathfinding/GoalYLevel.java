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
public class GoalYLevel implements Goal {
    final int level;
    public GoalYLevel(int level) {
        this.level = level;
    }
    @Override
    public boolean isInGoal(BlockPos pos) {
        return pos.getY() == level;
    }
    @Override
    public double heuristic(BlockPos pos) {
        return 20 * Math.abs(pos.getY() - level);
    }
}
