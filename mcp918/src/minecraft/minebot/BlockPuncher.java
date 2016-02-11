/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.ArrayList;
import java.util.Arrays;
import minebot.pathfinding.PathFinder;
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
    public static void tick(String... block) {
        ArrayList<BlockPos> closest = Memory.closest(10, block);
        if (closest == null) {
            GuiScreen.sendChatMessage("NO " + Arrays.asList(block) + " NEARBY. GOD DAMN IT");
            return;
        }
        Goal[] goals = new Goal[closest.size()];
        for (int i = 0; i < goals.length; i++) {
            goals[i] = new GoalTwoBlocks(closest.get(i));
        }
        MineBot.goal = new GoalComposite(goals);
        if (MineBot.currentPath == null && !MineBot.isThereAnythingInProgress) {
            MineBot.findPathInNewThread(false);
        }
    }
}