/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.ArrayList;
import java.util.Arrays;
import minebot.pathfinding.goals.Goal;
import minebot.pathfinding.goals.GoalComposite;
import minebot.pathfinding.goals.GoalTwoBlocks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.BlockPos;

/**
 *
 * @author avecowa
 */
public class BlockPuncher {
    public static boolean setGoalTo(String... block) {
        ArrayList<BlockPos> closest = Memory.closest(10, block);
        if (closest == null || closest.isEmpty()) {
            GuiScreen.sendChatMessage("NO " + Arrays.asList(block) + " NEARBY. GOD DAMN IT");
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
