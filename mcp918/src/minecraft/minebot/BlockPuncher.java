/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.Arrays;
import minebot.pathfinding.goals.GoalTwoBlocks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.BlockPos;

/**
 *
 * @author avecowa
 */
public class BlockPuncher {
    public static void tick(String... block) {
        BlockPos closest = Memory.closest(block);
        if (closest == null) {
            GuiScreen.sendChatMessage("NO " + Arrays.asList(block) + " NEARBY. GOD DAMN IT");
            return;
        }
        MineBot.goal = new GoalTwoBlocks(closest);
        if (MineBot.currentPath == null && !MineBot.isThereAnythingInProgress) {
            MineBot.findPathInNewThread(false);
        }
    }
}
