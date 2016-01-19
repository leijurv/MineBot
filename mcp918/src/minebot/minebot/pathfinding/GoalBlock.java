/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

/**
 *
 * @author leijurv
 */
public class GoalBlock implements Goal {
    final int x, y, z;
    public GoalBlock(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public boolean isInGoal(BlockPos pos) {
        return pos.x == this.x && pos.y == this.y && pos.z == this.z;
    }
    @Override
    public int heuristic(BlockPos pos) {
        return Math.abs(pos.x - this.x) + Math.abs(pos.y - this.y) + Math.abs(pos.z - this.z);
    }
}
