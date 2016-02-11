/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.Map.Entry;
import minebot.MineBot;
import minebot.pathfinding.actions.ActionPillar;
import minebot.pathfinding.goals.GoalBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;

/**
 *
 * @author leijurv
 */
public class SchematicBuilder {
    ArrayList<Tuple<BlockPos, Block>> plan = new ArrayList();
    BlockPos offset;
    Schematic schematic;
    public SchematicBuilder(Schematic schematic, BlockPos offset) {
        this.schematic = schematic;
        this.offset = offset;
        for (Entry<BlockPos, Block> entry : schematic.getEntries()) {
            plan.add(new Tuple(offset(entry.getKey()), entry.getValue()));
        }
    }
    public void tick() {
        BlockPos goal = getFirstToPlace();
        if (goal != null) {
            if (MineBot.isAir(goal.up(2))) {
                MineBot.goal = new GoalBlock(goal);
            } else {
                MineBot.goal = new GoalBlock(goal.up());
            }
            BlockPos playerCurrent = Minecraft.theMinecraft.thePlayer.getPosition0();
            if (playerCurrent.equals(goal) || playerCurrent.equals(goal.up())) {
                new ActionPillar(goal).tick1();
            } else {
                if (MineBot.currentPath == null && !MineBot.isThereAnythingInProgress) {
                    MineBot.findPathInNewThread(false);
                }
            }
        }
    }
    public BlockPos getFirstToPlace() {
        Block air = Block.getBlockById(0);
        BlockPos goal = null;
        for (int y = 0; y < schematic.getHeight(); y++) {
            for (int x = 0; x < schematic.getWidth(); x++) {
                for (int z = 0; z < schematic.getLength(); z++) {
                    BlockPos inSchematic = new BlockPos(x, y, z);
                    BlockPos inWorld = offset(inSchematic);
                    Block current = Minecraft.theMinecraft.theWorld.getBlockState(inWorld).getBlock();
                    Block desired = schematic.getBlockFromBlockPos(inSchematic);
                    boolean currentlyAir = air.equals(current);
                    boolean shouldBeAir = air.equals(desired);
                    if (currentlyAir && !shouldBeAir) {
                        goal = inWorld;
                    }
                }
            }
        }
        return goal;
    }
    private BlockPos offset(BlockPos original) {
        return new BlockPos(original.getX() + offset.getX(), original.getY() + offset.getY(), original.getZ() + offset.getZ());
    }
}
