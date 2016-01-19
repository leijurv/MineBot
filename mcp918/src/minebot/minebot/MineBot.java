/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import minebot.pathfinding.*;

/**
 *
 * @author leijurv
 */
public class MineBot {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GoalBlock goalBlock = new GoalBlock(0, 0, 0);
        PathFinder finder = new PathFinder(new BlockPos(0, 0, 3), goalBlock);
        finder.calculatePath();
    }
}
