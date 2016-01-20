/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class Path {
    final BlockPos start;
    final BlockPos end;
    final Goal goal;
    final ArrayList<BlockPos> path;
    final ArrayList<Action> actions;
    Path(Node start, Node end, Goal goal) {
        this.start = start.pos;
        this.end = end.pos;
        this.goal = goal;
        this.path = new ArrayList<>();
        this.actions = new ArrayList<>();
        Node current = end;
        while (!current.equals(start)) {
            path.add(0, current.pos);
            actions.add(0, current.previousAction);
            current = current.previous;
        }
        path.add(0, start.pos);
        System.out.println("Final path: " + path);
        System.out.println("Final actions: " + actions);
        for (int i = 0; i < path.size() - 1; i++) {
            int oldX = path.get(i).getX();
            int oldY = path.get(i).getY();
            int oldZ = path.get(i).getZ();
            int newX = path.get(i + 1).getX();
            int newY = path.get(i + 1).getY();
            int newZ = path.get(i + 1).getZ();
            int xDiff = newX - oldX;
            int yDiff = newY - oldY;
            int zDiff = newZ - oldZ;
            System.out.println(actions.get(i) + ": " + xDiff + "," + yDiff + "," + zDiff);
        }
    }
    public void showPathInStone() {
        IBlockState[] originalStates = new IBlockState[path.size()];
        for (int i = 0; i < path.size(); i++) {
            originalStates[i] = Minecraft.theMinecraft.theWorld.getBlockState(path.get(i));
            Minecraft.theMinecraft.theWorld.setBlockState(path.get(i), Block.getBlockById(1).getDefaultState());
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
                Logger.getLogger(Path.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            Thread.sleep(2500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Path.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < path.size(); i++) {
            Minecraft.theMinecraft.theWorld.setBlockState(path.get(i), originalStates[i]);
        }
    }
    int pathPosition = 0;
    public boolean tick() {
        if (pathPosition >= path.size()) {
            return true;
        }
        BlockPos whereShouldIBe = path.get(pathPosition);
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        BlockPos whereAmI = new BlockPos((int) thePlayer.posX, (int) thePlayer.posY, (int) thePlayer.posZ);
        if (pathPosition == path.size() - 1) {
            System.out.println("On last path position");
            return true;
        }
        if (!whereShouldIBe.equals(whereAmI)) {
            System.out.println("Should be at " + whereShouldIBe + " actually am at " + whereAmI);
            /*if (path.get(pathPosition + 1).equals(whereAmI)) {
             System.out.println("Hey I'm on the next one");
             pathPosition++;
             }*/
        }
        if (actions.get(pathPosition).tick()) {
            System.out.println("Action done, next path");
            pathPosition++;
            System.out.println("At position " + pathPosition);
        }
        return false;
    }
}
