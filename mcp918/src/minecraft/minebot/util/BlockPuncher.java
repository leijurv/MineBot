/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.Arrays;
import minebot.MineBot;
import minebot.pathfinding.goals.Goal;
import minebot.pathfinding.goals.GoalComposite;
import minebot.pathfinding.goals.GoalTwoBlocks;
import minebot.util.Out;
import net.minecraft.util.BlockPos;

/**
 * yeah so just like go and punch this type of block
 *
 * @author avecowa
 * @author leijurv
 *
 * =P
 */
public class BlockPuncher {
    public static boolean setGoalTo(String... block) {
        ArrayList<BlockPos> closest = Memory.closest(10, block);
        if (closest == null || closest.isEmpty()) {
            Out.gui("NO " + Arrays.asList(block) + " NEARBY. OH MAN", Out.Mode.Standard);
            return false;
        }
        Goal[] goals = new Goal[closest.size()];
        for (int i = 0; i < goals.length; i++) {
            goals[i] = new GoalTwoBlocks(closest.get(i));
        }
        MineBot.goal = new GoalComposite(goals);
        return true;
    }
    public static boolean tick(String... block) {
        if (!setGoalTo(block)) {
            return false;
        }
        if (MineBot.currentPath == null && !MineBot.isThereAnythingInProgress) {
            MineBot.findPathInNewThread(false);
        }
        return true;
    }
}
