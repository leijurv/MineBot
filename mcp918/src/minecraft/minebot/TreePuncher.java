/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;
import minebot.pathfinding.goals.GoalTwoBlocks;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class TreePuncher {
    public static void tick() {
        BlockPos closest = Memory.closest("log");
        if (closest == null) {
            GuiScreen.sendChatMessage("NO TREES NEARBY. GOD DAMN IT");
            return;
        }
        MineBot.goal = new GoalTwoBlocks(closest);
        if (MineBot.currentPath == null && !MineBot.isThereAnythingInProgress) {
            MineBot.findPathInNewThread(false);
        }
    }
}
