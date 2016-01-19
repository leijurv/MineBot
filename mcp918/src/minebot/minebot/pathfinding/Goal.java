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
public interface Goal {
    public boolean isInGoal(BlockPos pos);
    public int heuristic(BlockPos pos);
}
