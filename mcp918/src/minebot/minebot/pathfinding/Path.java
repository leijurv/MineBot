/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import minebot.MineBot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class Path {
    public final BlockPos start;
    public final BlockPos end;
    public final Goal goal;
    public final ArrayList<BlockPos> path;
    final IBlockState[] originalBlockStates;
    final ArrayList<Action> actions;
    Path(Node start, Node end, Goal goal) {
        this.start = start.pos;
        this.end = end.pos;
        this.goal = goal;
        this.path = new ArrayList<>();
        this.actions = new ArrayList<>();
        Node current = end;
        while (!current.equals(start)) {//assemble the path
            path.add(0, current.pos);
            actions.add(0, current.previousAction);
            current = current.previous;
        }
        path.add(0, start.pos);
        this.originalBlockStates = new IBlockState[path.size()];
        System.out.println("Final path: " + path);
        System.out.println("Final actions: " + actions);
        for (int i = 0; i < path.size() - 1; i++) {//print it all out
            int oldX = path.get(i).getX();
            int oldY = path.get(i).getY();
            int oldZ = path.get(i).getZ();
            int newX = path.get(i + 1).getX();
            int newY = path.get(i + 1).getY();
            int newZ = path.get(i + 1).getZ();
            int xDiff = newX - oldX;
            int yDiff = newY - oldY;
            int zDiff = newZ - oldZ;
            System.out.println(actions.get(i) + ": " + xDiff + "," + yDiff + "," + zDiff);//print it all out
        }
    }
    /**
     * We don't really use this any more
     */
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
    public double howFarAmIFromThePath(double x, double y, double z) {
        double best = -1;
        for (BlockPos pos : path) {
            double dist = distance(x, y, z, pos);
            if (dist < best || best == -1) {
                best = dist;
            }
        }
        return best;
    }
    public double distance(double x, double y, double z, BlockPos pos) {
        double xdiff = x - (pos.getX() + 0.5D);
        double ydiff = y - (pos.getY() + 0.5D);
        double zdiff = z - (pos.getZ() + 0.5D);
        return Math.sqrt(xdiff * xdiff + ydiff * ydiff + zdiff * zdiff);
    }
    /**
     * How many ticks have I been more than MAX_DISTANCE_FROM_PATH away from the
     * path
     */
    int ticksAway = 0;
    /**
     * How far away from the path can I get and still be okay
     */
    static final double MAX_DISTANCE_FROM_PATH = 2;
    /**
     * How many ticks can I be more than MAX_DISTANCE_FROM_PATH before we
     * consider it a failure
     */
    static final int MAX_TICKS_AWAY = 20 * 10;
    /**
     * How many ticks have elapsed on this action
     */
    int ticksOnCurrent = 0;
    /**
     * Did I fail, either by being too far away for too long, or by having an
     * action take too long
     */
    public boolean failed = false;
    public void doTheTorches() {
        Block carpet = Block.getBlockById(171);
        IBlockState state = carpet.getStateFromMeta(15);
        for (int i = 0; i < pathPosition + 3 && i < path.size(); i++) {
            IBlockState currentState = Minecraft.theMinecraft.theWorld.getBlockState(path.get(i));
            if (currentState.getBlock().equals(carpet)) {
                Minecraft.theMinecraft.theWorld.setBlockState(path.get(i), originalBlockStates[i]);
            }
        }
        for (int i = pathPosition + 3; i < Math.min(path.size(), pathPosition + 10); i++) {
            IBlockState currentState = Minecraft.theMinecraft.theWorld.getBlockState(path.get(i));
            if (!currentState.getBlock().equals(carpet)) {
                originalBlockStates[i] = currentState;
            } else {
                if (originalBlockStates[i] == null) {
                    originalBlockStates[i] = currentState;
                }
            }
            Minecraft.theMinecraft.theWorld.setBlockState(path.get(i), state);
        }
    }
    public boolean tick() {
        if (pathPosition >= path.size()) {
            MineBot.clearPath();//stop bugging me, I'm done
            return true;
        }
        BlockPos whereShouldIBe = path.get(pathPosition);
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        doTheTorches();
        BlockPos whereAmI = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (pathPosition == path.size() - 1) {
            System.out.println("On last path position");
            MineBot.clearPath();
            return true;
        }
        if (!whereShouldIBe.equals(whereAmI)) {
            System.out.println("Should be at " + whereShouldIBe + " actually am at " + whereAmI);
            //it's the duty of the action to tell us when it's done, so ignore the commented code below
            /*if (path.get(pathPosition + 1).equals(whereAmI)) {
             System.out.println("Hey I'm on the next one");
             pathPosition++;
             }*/
        }
        double distanceFromPath = howFarAmIFromThePath(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        if (distanceFromPath > MAX_DISTANCE_FROM_PATH) {
            ticksAway++;
            System.out.println("FAR AWAY FROM PATH FOR " + ticksAway + " TICKS. Current distance: " + distanceFromPath + ". Threshold: " + MAX_DISTANCE_FROM_PATH);
            if (ticksAway > MAX_TICKS_AWAY) {
                GuiScreen.sendChatMessage("Too far away from path for too long, cancelling path", true);
                System.out.println("Too many ticks");
                pathPosition = path.size() + 3;
                failed = true;
                return true;
            }
        } else {
            ticksAway = 0;
        }
        System.out.println(actions.get(pathPosition));
        MineBot.clearMovement();
        if (actions.get(pathPosition).tick()) {
            System.out.println("Action done, next path");
            pathPosition++;
            ticksOnCurrent = 0;
        } else {
            ticksOnCurrent++;
            if (ticksOnCurrent > actions.get(pathPosition).cost() + 100) {
                GuiScreen.sendChatMessage("This action has taken too long (" + ticksOnCurrent + " ticks, expected " + actions.get(pathPosition).cost() + "). Cancelling.", true);
                pathPosition = path.size() + 3;
                failed = true;
                return true;
            }
        }
        if (pathPosition < actions.size() - 1) {
            if ((actions.get(pathPosition) instanceof ActionBridge) && (actions.get(pathPosition + 1) instanceof ActionBridge)) {
                ActionBridge curr = (ActionBridge) actions.get(pathPosition);
                ActionBridge next = (ActionBridge) actions.get(pathPosition + 1);
                if (curr.dx() != next.dx() || curr.dz() != next.dz()) {//two actions are not parallel, so this is a right angle
                    if (curr.amIGood() && next.amIGood()) {//nothing in the way
                        double x = (next.from.getX() + next.to.getX() + 1.0D) * 0.5D;
                        double z = (next.from.getZ() + next.to.getZ() + 1.0D) * 0.5D;
                        MineBot.clearMovement();
                        MineBot.moveTowardsCoords(x, 0, z);
                        if (!MineBot.forward && curr.oneInTen != null && curr.oneInTen) {
                            MineBot.lookAtCoords(x, 0, z, false);
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
