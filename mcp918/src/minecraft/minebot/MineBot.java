/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import minebot.pathfinding.Action;
import minebot.pathfinding.GoalBlock;
import minebot.pathfinding.Path;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import minebot.pathfinding.PathFinder;
import minebot.mining.Miner;
import minebot.pathfinding.Goal;
import minebot.pathfinding.GoalRunAway;
import minebot.pathfinding.GoalXZ;
import minebot.pathfinding.GoalYLevel;
import minebot.util.ToolSet;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.FoodStats;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

/**
 *
 * @author leijurv
 */
public class MineBot {
    public static boolean actuallyPutMessagesInChat = false;
    static boolean isThereAnythingInProgress = false;
    static boolean plsCancel = false;
    public static boolean useCarpet = false;
    static Entity target = null;
    static boolean wasTargetSetByMobHunt = false;
    public static void main(String[] args) throws IOException, InterruptedException {
        String s = Autorun.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(5) + "../../autorun/runmc.command";
        if (s.contains("jar")) {
            Autorun.start(args);
            return;
        }
        Autorun.runprocess("/usr/local/bin/ant jar");
        Autorun.runprocess("java -Djava.library.path=jars/versions/1.8.8/1.8.8-natives/ -jar dist/MineBot.jar");
    }
    /**
     * Called by minecraft.java
     */
    public static void onTick() {
        try {
            long start = System.currentTimeMillis();
            onTick1();
            long end = System.currentTimeMillis();
            long time = end - start;
            if (time > 3) {
                System.out.println("Tick took " + time + "ms");
            }
        } catch (Exception ex) {
            Logger.getLogger(MineBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void onTick1() {
        if (Minecraft.theMinecraft.theWorld == null || Minecraft.theMinecraft.thePlayer == null) {
            cancelPath();
            return;
        }
        if (isLeftClick) {
            leftPressTime = 5;
        }
        if (isRightClick) {
            rightPressTime = 5;
        }
        if (lookingYaw) {
            Minecraft.theMinecraft.thePlayer.rotationYaw = desiredNextYaw;
        }
        if (lookingPitch) {
            Minecraft.theMinecraft.thePlayer.rotationPitch = desiredNextPitch;
        }
        lookingYaw = false;
        lookingPitch = false;
        isLeftClick = false;
        isRightClick = false;
        jumping = false;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        boolean tickPath = true;
        boolean healthOkToHunt = Minecraft.theMinecraft.thePlayer.getHealth() >= 12;
        if (mobKilling) {
            ArrayList<EntityMob> mobs = new ArrayList<EntityMob>();
            for (Entity entity : theWorld.loadedEntityList) {
                if (entity.isEntityAlive()) {
                    if (entity instanceof EntityMob) {
                        if (distFromMe(entity) < 5) {
                            mobs.add((EntityMob) entity);
                        }
                    }
                }
            }
            mobs.sort(new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return new Double(distFromMe(o1)).compareTo(distFromMe(o2));
                }
            });
            //System.out.println(mobs);
            if (!mobs.isEmpty()) {
                EntityMob entity = mobs.get(0);
                AxisAlignedBB lol = entity.getEntityBoundingBox();
                switchtosword();
                System.out.println("looking");
                lookAtCoords((lol.minX + lol.maxX) / 2, (lol.minY + lol.maxY) / 2, (lol.minZ + lol.maxZ) / 2, true);
                if (entity.equals(what())) {
                    isLeftClick = true;
                    tickPath = false;
                    System.out.println("Doing it");
                }
            }
        }
        if (mobHunting && (target == null || wasTargetSetByMobHunt)) {
            ArrayList<Entity> mobs = new ArrayList<Entity>();
            for (Entity entity : theWorld.loadedEntityList) {
                if (entity.isEntityAlive()) {
                    if ((entity instanceof EntityMob) && entity.posY > thePlayer.posY - 6) {
                        if (distFromMe(entity) < 30) {
                            mobs.add(entity);
                        }
                    }
                    if ((playerHunt && (entity instanceof EntityPlayer) && !(entity.getName().equals(thePlayer.getName())) && !couldBeInCreative((EntityPlayer) entity))) {
                        if (distFromMe(entity) < 30) {
                            mobs.add(entity);
                        }
                    }
                }
            }
            mobs.sort(new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return new Double(distFromMe(o1)).compareTo(distFromMe(o2));
                }
            });
            if (!mobs.isEmpty()) {
                Entity entity = mobs.get(0);
                if (!entity.equals(target)) {
                    if (!(!(entity instanceof EntityPlayer) && (target instanceof EntityPlayer) && playerHunt)) {
                        GuiScreen.sendChatMessage("Mobhunting=true. Killing " + entity, true);
                        if (currentPath != null) {
                            currentPath.clearPath();
                        }
                        currentPath = null;
                        target = entity;
                        wasTargetSetByMobHunt = true;
                    }
                }
            }
        }
        if (!healthOkToHunt && target != null && wasTargetSetByMobHunt) {
            if (currentPath != null) {
                if (!(currentPath.goal instanceof GoalRunAway)) {
                    GuiScreen.sendChatMessage("Health too low, cancelling hunt", true);
                    if (currentPath != null) {
                        currentPath.clearPath();
                    }
                    currentPath = null;
                }
            }
            clearMovement();
            goal = new GoalRunAway((int) target.posX, (int) target.posZ, 50);//TODO run away from more than one mob
            if (currentPath == null) {
                GuiScreen.sendChatMessage("Running away", true);
                findPathInNewThread(playerFeet);
            } else {
                GoalRunAway g = (GoalRunAway) currentPath.goal;
                int xDiff = (int) (target.posX - g.x);
                int zDiff = (int) (target.posZ - g.z);
                int d = xDiff * xDiff + zDiff * zDiff;
                if (d > 5 * 5 && !isThereAnythingInProgress) {
                    GuiScreen.sendChatMessage("Switching who I'm running away from", true);
                    findPathInNewThread(playerFeet);
                }
            }
        }
        if (target != null && target.isDead) {
            GuiScreen.sendChatMessage(target + " is dead", true);
            target = null;
            if (currentPath != null) {
                currentPath.clearPath();
            }
            currentPath = null;
            clearMovement();
        }
        if (target != null && healthOkToHunt) {
            BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);
            goal = new GoalBlock(targetPos);
            if (currentPath != null) {
                double movementSince = dist(targetPos, currentPath.end);
                if (movementSince > 4 && !isThereAnythingInProgress) {
                    GuiScreen.sendChatMessage("They moved too much, " + movementSince + " blocks. recalculating", true);
                    findPathInNewThread(playerFeet);
                }
            }
            double dist = distFromMe(target);
            boolean actuallyLookingAt = target.equals(what());
            //GuiScreen.sendChatMessage(dist + " " + actuallyLookingAt, true);
            if (dist > 4 && currentPath == null) {
                findPathInNewThread(playerFeet);
            }
            if (dist <= 4) {
                AxisAlignedBB lol = target.getEntityBoundingBox();
                switchtosword();
                boolean direction = lookAtCoords((lol.minX + lol.maxX) / 2, (lol.minY + lol.maxY) / 2, (lol.minZ + lol.maxZ) / 2, true);
                if (direction && !actuallyLookingAt) {
                    findPathInNewThread(playerFeet);
                }
            }
            if (actuallyLookingAt) {
                isLeftClick = true;
                tickPath = false;
            }
        }
        System.out.println("Ticking: " + tickPath);
        //System.out.println("Mob hunting: " + !tickPath);
        if (tickPath) {
            if (dealWithFood()) {
                tickPath = false;
            }
        }
        if (currentPath == null && tickPath) {
            Miner.tick();
            tickPath = false;
        }
        if (currentPath != null && tickPath) {
            if (currentPath.tick()) {
                if (currentPath != null) {
                    currentPath.clearPath();
                }
                if (currentPath != null && currentPath.failed) {
                    clearPath();
                    GuiScreen.sendChatMessage("Recalculating because path failed", true);
                    nextPath = null;
                    findPathInNewThread(playerFeet);
                    return;
                } else {
                    clearPath();
                }
                currentPath = null;
                if (goal.isInGoal(playerFeet)) {
                    GuiScreen.sendChatMessage("All done. At " + goal, true);
                    nextPath = null;
                } else {
                    GuiScreen.sendChatMessage("Done with segment", true);
                    if (nextPath != null || calculatingNext) {
                        if (calculatingNext) {
                            calculatingNext = false;
                            GuiScreen.sendChatMessage("Patiently waiting to finish", true);
                        } else {
                            currentPath = nextPath;
                            nextPath = null;
                            if (!currentPath.start.equals(playerFeet)) {
                                GuiScreen.sendChatMessage("The next path starts at " + currentPath.start + " but I'm at " + playerFeet + ". not doing it", true);
                                currentPath = null;
                                findPathInNewThread(playerFeet);
                            } else {
                                GuiScreen.sendChatMessage("Going onto next", true);
                                if (!currentPath.goal.isInGoal(currentPath.end)) {
                                    planAhead();
                                }
                            }
                        }
                    } else {
                        GuiScreen.sendChatMessage("Hmm. I'm not actually at the goal. Recalculating.", true);
                        findPathInNewThread(playerFeet);
                    }
                }
            } else {
                if (Action.isWater(theWorld.getBlockState(playerFeet).getBlock())) {
                    System.out.println("Jumping because in water");
                    jumping = true;
                }
                if (!lookingPitch) {
                    if (thePlayer.rotationPitch < -20) {
                        thePlayer.rotationPitch++;
                    } else if (thePlayer.rotationPitch > 20) {
                        thePlayer.rotationPitch--;
                    }
                }
            }
        }
        if (isThereAnythingInProgress && Action.isWater(theWorld.getBlockState(playerFeet).getBlock())) {
            System.out.println("Jumping because in water and pathfinding");
            jumping = true;
        }
        if (lookingYaw) {
            previousYaw = Minecraft.theMinecraft.thePlayer.rotationYaw;
            desiredYaw += 360;
            desiredYaw %= 360;
            float yawDistance = Minecraft.theMinecraft.thePlayer.rotationYaw - desiredYaw;
            if (yawDistance > 180) {
                yawDistance -= 360;
            } else if (yawDistance < -180) {
                yawDistance += 360;
            }
            if (Math.abs(yawDistance) > MAX_YAW_CHANGE_PER_TICK) {
                yawDistance = Math.signum(yawDistance) * MAX_YAW_CHANGE_PER_TICK;
            }
            desiredNextYaw = Minecraft.theMinecraft.thePlayer.rotationYaw - yawDistance;
        }
        if (lookingPitch) {
            previousPitch = Minecraft.theMinecraft.thePlayer.rotationPitch;
            desiredPitch += 360;
            desiredPitch %= 360;
            float pitchDistance = Minecraft.theMinecraft.thePlayer.rotationPitch - desiredPitch;
            if (pitchDistance > 180) {
                pitchDistance -= 360;
            } else if (pitchDistance < -180) {
                pitchDistance += 360;
            }
            if (Math.abs(pitchDistance) > MAX_PITCH_CHANGE_PER_TICK) {
                pitchDistance = Math.signum(pitchDistance) * MAX_PITCH_CHANGE_PER_TICK;
            }
            desiredNextPitch = Minecraft.theMinecraft.thePlayer.rotationPitch - pitchDistance;
        }
    }
    static float previousYaw = 0;
    static float previousPitch = 0;
    static float desiredNextYaw = 0;
    static float desiredNextPitch = 0;
    public static void frame(float partialTicks) {
        System.out.println("Part: " + partialTicks);
        if (lookingPitch) {
            Minecraft.theMinecraft.thePlayer.rotationPitch = (desiredNextPitch - previousPitch) * partialTicks + previousPitch;
        }
        if (lookingYaw) {
            Minecraft.theMinecraft.thePlayer.rotationYaw = (desiredNextYaw - previousYaw) * partialTicks + previousYaw;
        }
    }
    public static double distFromMe(Entity a) {
        EntityPlayerSP player = Minecraft.theMinecraft.thePlayer;
        double diffX = player.posX - a.posX;
        double diffY = player.posY - a.posY;
        double diffZ = player.posZ - a.posZ;
        return Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
    }
    public static double dist(BlockPos a, BlockPos b) {
        int diffX = a.getX() - b.getX();
        int diffY = a.getY() - b.getY();
        int diffZ = a.getZ() - b.getZ();
        return Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
    }
    public static float getDesiredYaw() {
        return desiredYaw;
    }
    static final float MAX_YAW_CHANGE_PER_TICK = 360 / 20;
    static final float MAX_PITCH_CHANGE_PER_TICK = 360 / 20;
    public static boolean wasScreen = false;
    public static boolean calculatingNext = false;
    public static Path currentPath = null;
    public static Path nextPath = null;
    public static Goal goal = null;
    public static int leftPressTime = 0;
    public static int rightPressTime = 0;
    public static boolean isLeftClick = false;
    public static boolean isRightClick = false;
    public static boolean jumping = false;
    public static boolean forward = false;
    public static boolean backward = false;
    public static boolean left = false;
    public static boolean right = false;
    public static boolean sneak = false;
    static HashMap<String, Goal> saved = new HashMap<String, Goal>();
    /**
     * Do not question the logic. Called by Minecraft.java
     *
     * @return
     */
    public static boolean getLeftIsPressed() {
        return isLeftClick /*&& Minecraft.theMinecraft.currentScreen == null*/ && leftPressTime >= -2;
    }
    /**
     * Do not question the logic. Called by Minecraft.java
     *
     * @return
     */
    public static boolean leftIsPressed() {
        if (leftPressTime <= 0) {
            return false;
        } else {
            --leftPressTime;
            return true;
        }
    }
    /**
     * Do not question the logic. Called by Minecraft.java
     *
     * @return
     */
    public static boolean getRightIsPressed() {
        return isRightClick && rightPressTime >= -2;
    }
    /**
     * Do not question the logic. Called by Minecraft.java
     *
     * @return
     */
    public static boolean rightIsPressed() {
        if (rightPressTime <= 0) {
            return false;
        } else {
            --rightPressTime;
            return true;
        }
    }
    /**
     * Called by our code
     */
    public static void letGoOfLeftClick() {
        leftPressTime = 0;
        isLeftClick = false;
    }
    /**
     * Clears movement, but nothing else. Includes jumping and sneaking, but not
     * left clicking.
     */
    public static void clearMovement() {
        jumping = false;
        forward = false;
        left = false;
        right = false;
        backward = false;
        sneak = false;
        isRightClick = false;
        rightPressTime = 0;
    }
    /**
     * Clears movement, clears the current path, and lets go of left click. It
     * purposefully does NOT clear nextPath.
     */
    public static void clearPath() {
        if (currentPath != null) {
            final Path p = currentPath;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        p.clearPath();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MineBot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        }
        currentPath = null;
        letGoOfLeftClick();
        isRightClick = false;
        rightPressTime = 0;
        clearMovement();
    }
    public static String therewasachatmessage(String message) {
        try {
            return therewasachatmessage1(message);
        } catch (Exception e) {
            return message;
        }
    }
    static boolean mobHunting = false;
    static boolean mobKilling = true;
    static boolean playerHunt = false;
    /**
     * Called by GuiScreen.java
     *
     * @param message the message that was sent in chat to trigger this
     * @return what message should actually be sent. can be null to send nothing
     */
    public static String therewasachatmessage1(String message) {
        Minecraft mc = Minecraft.theMinecraft;
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        System.out.println("MSG: " + message);
        String text = (message.charAt(0) == '/' ? message.substring(1) : message).trim();
        if (text.contains("wizard")) {
            isThereAnythingInProgress = false;
            return "YOURE A LIZARD HARRY";
        }
        if (text.startsWith("actuallyPutMessagesInChat")) {
            actuallyPutMessagesInChat = !actuallyPutMessagesInChat;
            return "toggled to " + actuallyPutMessagesInChat;
        }
        if (text.equals("mobkill")) {
            mobKilling = !mobKilling;
            return "Mob killing: " + mobKilling;
        }
        if (text.equals("playerhunt")) {
            playerHunt = !playerHunt;
            return "Also do players during mobhunt: " + playerHunt;
        }
        if (text.equals("mobhunt")) {
            mobHunting = !mobHunting;
            return "Mob hunting: " + mobHunting;
        }
        if (text.equals("carpet")) {
            useCarpet = !useCarpet;
            return "Use carpet: " + useCarpet;
        }
        if (text.startsWith("save")) {
            String t = text.substring(4).trim();
            saved.put(t, goal);
            return "Saved " + goal + " under " + t;
        }
        if (text.startsWith("load")) {
            String t = text.substring(4).trim();
            goal = saved.get(t);
            return "Set goal to " + goal;
        }
        if (text.startsWith("random direction")) {
            double dist = Double.parseDouble(text.substring("random direction".length()).trim());
            double ang = new Random().nextDouble() * Math.PI * 2;
            GuiScreen.sendChatMessage("Angle: " + ang, true);
            int x = playerFeet.getX() + (int) (Math.sin(ang) * dist);
            int z = playerFeet.getZ() + (int) (Math.cos(ang) * dist);
            goal = new GoalXZ(x, z);
            return "Set goal to " + goal;
        }
        if (text.equals("look")) {
            lookAtBlock(new BlockPos(0, 0, 0), true);
            return null;
        }
        if (text.equals("cancel")) {
            cancelPath();
            plsCancel = true;
            target = null;
            Miner.stopMining();
            return isThereAnythingInProgress ? "Cancelled it, but btw I'm pathfinding right now" : "Cancelled it";
        }
        if (text.equals("st")) {
            System.out.println(theWorld.getBlockState(playerFeet).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 1, thePlayer.posZ)).getBlock());
            System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 2, thePlayer.posZ)).getBlock());
        }
        if (text.equals("lac")) {
            BlockPos pos = closestBlock();
            lookAtBlock(pos, true);
            return pos.toString();
        }
        if (text.startsWith("goal") || text.startsWith("setgoal")) {
            plsCancel = false;
            int ind = text.indexOf(' ') + 1;
            if (ind == 0) {
                goal = new GoalBlock(playerFeet);
                return "Set goal to " + goal;
            }
            String[] strs = text.substring(ind).split(" ");
            int[] coords = new int[strs.length];
            for (int i = 0; i < strs.length; i++) {
                try {
                    coords[i] = Integer.parseInt(strs[i]);
                } catch (NumberFormatException nfe) {
                    goal = new GoalBlock(playerFeet);
                    return strs[i] + ". yup. A+ coordinate";//A+? you might even say A*
                }
            }
            switch (strs.length) {
                case 3:
                    goal = new GoalBlock(coords[0], coords[1], coords[2]);
                    break;
                case 2:
                    goal = new GoalXZ(coords[0], coords[1]);
                    break;
                case 1:
                    goal = new GoalYLevel(coords[0]);
                    break;
                default:
                    goal = new GoalBlock(playerFeet);
                    if (strs.length != 0) {
                        return strs.length + " coordinates. Nice.";
                    }
                    break;
            }
            return "Set goal to " + goal;
        }
        if (text.startsWith("goto")) {
            String name = text.substring(4).trim().toLowerCase();
            for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
                String blah = pl.getName().trim().toLowerCase();
                if (blah.contains(name) || name.contains(blah)) {
                    BlockPos pos = new BlockPos(pl.posX, pl.posY, pl.posZ);
                    goal = new GoalBlock(pos);
                    findPathInNewThread(playerFeet);
                    return "Pathing to " + pl.getName() + " at " + goal;
                }
            }
            return "Couldn't find " + name;
        }
        if (text.startsWith("kill")) {
            String name = text.substring(4).trim().toLowerCase();
            if (name.length() > 2) {
                for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
                    String blah = pl.getName().trim().toLowerCase();
                    if (!blah.equals(Minecraft.theMinecraft.thePlayer.getName().trim().toLowerCase())) {
                        GuiScreen.sendChatMessage("Considering " + blah, true);
                        if (couldBeInCreative(pl)) {
                            GuiScreen.sendChatMessage("No, creative", true);
                            continue;
                        }
                        if (blah.contains(name) || name.contains(blah)) {
                            target = pl;
                            wasTargetSetByMobHunt = false;
                            BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
                            goal = new GoalBlock(pos);
                            findPathInNewThread(playerFeet);
                            return "Killing " + pl;
                        }
                    }
                }
            }
            Entity w = what();
            if (w != null) {
                target = w;
                BlockPos pos = new BlockPos(target.posX, target.posY, target.posZ);
                goal = new GoalBlock(pos);
                wasTargetSetByMobHunt = false;
                findPathInNewThread(playerFeet);
                return "Killing " + w;
            }
            return "Couldn't find " + name;
        }
        if (text.startsWith("player")) {
            String name = text.substring(6).trim();
            String resp = "";
            for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
                resp += "(" + pl.getName() + "," + pl.posX + "," + pl.posY + "," + pl.posZ + ")";
                if (pl.getName().equals(name)) {
                    BlockPos pos = new BlockPos(pl.posX, pl.posY, pl.posZ);
                    goal = new GoalBlock(pos);
                    return "Set goal to " + goal;
                }
            }
            return resp;
        }
        if (text.startsWith("thisway")) {
            double dist = Double.parseDouble(text.substring(7).trim());
            goal = fromAngleAndDirection(dist);
            return "Set goal to " + goal;
        }
        if (text.startsWith("path")) {
            plsCancel = false;
            findPathInNewThread(playerFeet);
            return null;
        }
        if (text.startsWith("hardness")) {
            BlockPos bp = MineBot.whatAreYouLookingAt();
            return bp == null ? "0" : (1 / theWorld.getBlockState(bp).getBlock().getPlayerRelativeBlockHardness(Minecraft.theMinecraft.thePlayer, Minecraft.theMinecraft.theWorld, MineBot.whatAreYouLookingAt())) + "";
        }
        if (text.startsWith("info")) {
            BlockPos bp = MineBot.whatAreYouLookingAt();
            Block block = theWorld.getBlockState(bp).getBlock();
            return block + " can walk on: " + Action.canWalkOn(bp) + " can walk through: " + Action.canWalkThrough(bp) + " is full block: " + block.isFullBlock() + " is full cube: " + block.isFullCube();
        }
        if (text.startsWith("mine")) {
            plsCancel = false;
            goMiningInNewThread();
            return null;
        }
        return message;
    }
    /**
     * Cancel the path
     *
     */
    public static void cancelPath() {
        nextPath = null;
        clearPath();
        Miner.stopMining();
    }
    /**
     * Go mining in a new thread. Literally starts a new thread that calls
     * Miner.goMining()
     */
    public static void goMiningInNewThread() {
        new Thread() {
            @Override
            public void run() {
                Miner.goMining();
            }
        }.start();
    }
    public static boolean couldBeInCreative(EntityPlayer player) {
        if (player.capabilities.isCreativeMode || player.capabilities.allowFlying || player.capabilities.isFlying) {
            return true;
        }
        BlockPos inFeet = new BlockPos(player.posX, player.posY, player.posZ);
        BlockPos standingOn = inFeet.down();
        if (isAir(standingOn) && isAir(standingOn.north()) && isAir(standingOn.south()) && isAir(standingOn.east()) && isAir(standingOn.west()) && isAir(standingOn.north().west()) && isAir(standingOn.north().east()) && isAir(standingOn.south().west()) && isAir(standingOn.south().east())) {
            //if the block they are standing on, and every block touching it, are all air, they are probably flying
            return true;
        }
        return false;
    }
    public static boolean isAir(BlockPos pos) {
        return Minecraft.theMinecraft.theWorld.getBlockState(pos).getBlock().equals(Block.getBlockById(0));
    }
    /**
     * Go to the specified Y coordinate. Methods blocks via Thread.sleep until
     * currentPath is null. Note: does NOT actually check Y coordinate at the
     * end (so it could have been canceled)
     *
     * @param y
     */
    public static void getToY(int y) {
        if (currentPath != null) {
            cancelPath();
        }
        if (isThereAnythingInProgress) {
            GuiScreen.sendChatMessage("Nope. I'm busy", true);
            return;
        }
        EntityPlayer p = Minecraft.theMinecraft.thePlayer;
        MineBot.goal = new GoalYLevel(y);
        MineBot.findPathInNewThread(new BlockPos(p.posX, p.posY, p.posZ));
        try {
            do {
                Thread.sleep(50);
            } while (currentPath != null);
        } catch (InterruptedException ex) {
            Logger.getLogger(MineBot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * In a new thread, pathfind to target blockpos
     *
     * @param start
     */
    public static void findPathInNewThread(final BlockPos start) {
        new Thread() {
            @Override
            public void run() {
                if (isThereAnythingInProgress) {
                    return;
                }
                isThereAnythingInProgress = true;
                GuiScreen.sendChatMessage("Starting to search for path from " + start + " to " + goal, true);
                currentPath = findPath(start);
                if (!currentPath.goal.isInGoal(currentPath.end)) {
                    GuiScreen.sendChatMessage("I couldn't get all the way to " + goal + ", but I'm going to get as close as I can", true);
                    isThereAnythingInProgress = false;
                    planAhead();
                } else {
                    GuiScreen.sendChatMessage("Finished finding a path from " + start + " to " + goal, true);
                    isThereAnythingInProgress = false;
                }
            }
        }.start();
    }
    /**
     * In a new thread, pathfind from currentPath.end to goal. Store resulting
     * path in nextPath (or in currentPath if calculatingNext was set to false
     * in the meantime).
     */
    public static void planAhead() {
        new Thread() {
            @Override
            public void run() {
                if (isThereAnythingInProgress) {
                    return;
                }
                isThereAnythingInProgress = true;
                GuiScreen.sendChatMessage("Planning ahead", true);
                calculatingNext = true;
                Path path = findPath(currentPath.end);
                GuiScreen.sendChatMessage("Done planning ahead " + calculatingNext, true);
                if (calculatingNext) {
                    nextPath = path;
                } else {
                    currentPath = path;
                    if (!plsCancel) {
                        planAhead();
                    }
                }
                calculatingNext = false;
                isThereAnythingInProgress = false;
            }
        }.start();
    }
    /**
     * Actually do the pathfinding
     *
     * @param start
     * @return
     */
    public static Path findPath(BlockPos start) {
        try {
            PathFinder pf = new PathFinder(start, goal);
            Path path = pf.calculatePath();
            GuiScreen.sendChatMessage("calculated " + start + " to " + path.end, true);
            return path;
        } catch (Exception e) {
            isThereAnythingInProgress = false;
            return null;
        }
        /* if (stone) {
         path.showPathInStone();
         return;
         }*/
    }
    public static boolean dealWithFood() {
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        FoodStats fs = p.getFoodStats();
        if (!fs.needFood()) {
            return false;
        }
        int foodNeeded = 20 - fs.getFoodLevel();
        boolean anything = foodNeeded >= 3 && Minecraft.theMinecraft.thePlayer.getHealth() < 20;
        //System.out.println("Needs food: " + foodNeeded);
        ItemStack[] inv = p.inventory.mainInventory;
        byte slotForFood = -1;
        int worst = 10000;
        for (byte i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                continue;
            }
            if (item.getItem() instanceof ItemFood) {
                int healing = ((ItemFood) (item.getItem())).getHealAmount(item);
                //System.out.println(item + " " + healing);
                if (healing <= foodNeeded) {
                    slotForFood = i;
                }
                if (anything && healing > foodNeeded && healing < worst) {
                    slotForFood = i;
                }
            }
        }
        if (slotForFood != -1) {
            //System.out.println("Switching to slot " + slotForFood + " and right clicking");
            MineBot.clearMovement();
            isRightClick = true;
            p.inventory.currentItem = slotForFood;
            return true;
        }
        return false;
    }
    public static void switchtosword() {
        EntityPlayerSP p = Minecraft.theMinecraft.thePlayer;
        ItemStack[] inv = p.inventory.mainInventory;
        float bestDamage = 0;
        for (byte i = 0; i < 9; i++) {
            ItemStack item = inv[i];
            if (inv[i] == null) {
                item = new ItemStack(Item.getByNameOrId("minecraft:apple"));
            }
            if (item.getItem() instanceof ItemSword) {
                float damage = ((ItemSword) (item.getItem())).getDamageVsEntity();
                if (damage > bestDamage) {
                    p.inventory.currentItem = i;
                }
            }
        }
    }
    /**
     * Give a block that's sorta close to the player, at foot level
     *
     * @return
     */
    public static BlockPos closestBlock() {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        Block air = Block.getBlockById(0);
        for (int x = (int) (thePlayer.posX - 5); x <= thePlayer.posX + 5; x++) {
            for (int z = (int) (thePlayer.posZ - 5); z <= thePlayer.posZ + 5; z++) {
                BlockPos pos = new BlockPos(x, thePlayer.posY, z);
                Block b = theWorld.getBlockState(pos).getBlock();
                if (!b.equals(air)) {
                    return pos;
                }
            }
        }
        return null;
    }
    /**
     * The desired yaw, as set by whatever action is happening. Remember to also
     * set lookingYaw to true if you really want the yaw to change
     *
     */
    static float desiredYaw;
    /**
     * The desired pitch, as set by whatever action is happening. Remember to
     * also set lookingPitch to true if you really want the yaw to change
     *
     */
    static float desiredPitch;
    /**
     * Set to true if the action wants the player's yaw to be moved towards
     * desiredYaw
     */
    static boolean lookingYaw = false;
    /**
     * Set to true if the action wants the player's pitch to be moved towards
     * desiredPitch
     */
    static boolean lookingPitch = false;
    /**
     * Called by our code in order to look in the direction of the center of a
     * block
     *
     * @param p the position to look at
     * @param alsoDoPitch whether to set desired pitch or just yaw
     * @return is the actual player yaw (and actual player pitch, if alsoDoPitch
     * is true) within ANGLE_THRESHOLD (currently 7°) of looking straight at
     * this block?
     */
    public static boolean lookAtBlock(BlockPos p, boolean alsoDoPitch) {
        Block b = Minecraft.theMinecraft.theWorld.getBlockState(p).getBlock();
        double xDiff = (b.getBlockBoundsMinX() + b.getBlockBoundsMaxX()) / 2;
        double yolo = (b.getBlockBoundsMinY() + b.getBlockBoundsMaxY()) / 2;
        double zDiff = (b.getBlockBoundsMinZ() + b.getBlockBoundsMaxZ()) / 2;
        /*System.out.println("min X: " + b.getBlockBoundsMinX());
         System.out.println("max X: " + b.getBlockBoundsMaxX());
         System.out.println("xdiff: " + xDiff);
         System.out.println("min Y: " + b.getBlockBoundsMinY());
         System.out.println("max Y: " + b.getBlockBoundsMaxY());
         System.out.println("ydiff: " + yolo);
         System.out.println("min Z: " + b.getBlockBoundsMinZ());
         System.out.println("max Z: " + b.getBlockBoundsMaxZ());
         System.out.println("zdiff: " + zDiff);*/
        double x = p.getX() + xDiff;
        double y = p.getY() + yolo;
        double z = p.getZ() + zDiff;
        //System.out.println("Trying to look at " + p + " actually looking at " + whatAreYouLookingAt() + " xyz is " + x + "," + y + "," + z);
        return lookAtCoords(x, y, z, alsoDoPitch);
    }
    /**
     * The threshold for how close it tries to get to looking straight at things
     */
    public static final float ANGLE_THRESHOLD = 7;
    public static GoalXZ fromAngleAndDirection(double distance) {
        double theta = ((double) Minecraft.theMinecraft.thePlayer.rotationYaw) * Math.PI / 180D;
        double x = Minecraft.theMinecraft.thePlayer.posX - Math.sin(theta) * distance;
        double z = Minecraft.theMinecraft.thePlayer.posZ + Math.cos(theta) * distance;
        return new GoalXZ((int) x, (int) z);
    }
    /**
     * Look at coordinates
     *
     * @param x
     * @param y
     * @param z
     * @param alsoDoPitch also adjust the pitch? if false, y is ignored
     * @return is the actual player yaw (and actual player pitch, if alsoDoPitch
     * is true) within ANGLE_THRESHOLD (currently 7°) of looking straight at
     * these coordinates?
     */
    public static boolean lookAtCoords(double x, double y, double z, boolean alsoDoPitch) {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        double yDiff = (thePlayer.posY + 1.62) - y;
        double yaw = Math.atan2(thePlayer.posX - x, -thePlayer.posZ + z);
        double dist = Math.sqrt((thePlayer.posX - x) * (thePlayer.posX - x) + (-thePlayer.posZ + z) * (-thePlayer.posZ + z));
        double pitch = Math.atan2(yDiff, dist);
        desiredYaw = (float) (yaw * 180 / Math.PI);
        lookingYaw = true;
        float yawDist = Math.abs(desiredYaw - thePlayer.rotationYaw);
        boolean withinRange = yawDist < ANGLE_THRESHOLD || yawDist > 360 - ANGLE_THRESHOLD;
        if (alsoDoPitch) {
            lookingPitch = true;
            desiredPitch = (float) (pitch * 180 / Math.PI);
            float pitchDist = Math.abs(desiredPitch - thePlayer.rotationPitch);
            withinRange = withinRange && (pitchDist < ANGLE_THRESHOLD || pitchDist > 360 - ANGLE_THRESHOLD);
        }
        return withinRange;
    }
    /**
     * calls moveTowardsCoords on the center of this block
     *
     * @param p
     * @return am I moving, or am I still rotating
     */
    public static boolean moveTowardsBlock(BlockPos p) {
        Block b = Minecraft.theMinecraft.theWorld.getBlockState(p).getBlock();
        double xDiff = (b.getBlockBoundsMinX() + b.getBlockBoundsMaxX()) / 2;
        double yolo = (b.getBlockBoundsMinY() + b.getBlockBoundsMaxY()) / 2;
        double zDiff = (b.getBlockBoundsMinZ() + b.getBlockBoundsMaxZ()) / 2;
        /*System.out.println("min X: " + b.getBlockBoundsMinX());
         System.out.println("max X: " + b.getBlockBoundsMaxX());
         System.out.println("xdiff: " + xDiff);
         System.out.println("min Y: " + b.getBlockBoundsMinY());
         System.out.println("max Y: " + b.getBlockBoundsMaxY());
         System.out.println("ydiff: " + yolo);
         System.out.println("min Z: " + b.getBlockBoundsMinZ());
         System.out.println("max Z: " + b.getBlockBoundsMaxZ());
         System.out.println("zdiff: " + zDiff);*/
        double x = p.getX() + xDiff;
        double y = p.getY() + yolo;
        double z = p.getZ() + zDiff;
        //System.out.println("Trying to look at " + p + " actually looking at " + whatAreYouLookingAt() + " xyz is " + x + "," + y + "," + z);
        return moveTowardsCoords(x, y, z);
    }
    /**
     * Move towards coordinates, not necesarily forwards. e.g. if coordinates
     * are closest to being directly behind us, go backwards. This minimizes
     * time spent waiting for rotating
     *
     * @param x
     * @param y
     * @param z
     * @return true if we are moving, false if we are still rotating. we will
     * rotate until within ANGLE_THRESHOLD (currently 7°) of moving in correct
     * direction
     */
    public static boolean moveTowardsCoords(double x, double y, double z) {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        float currentYaw = thePlayer.rotationYaw;
        float yaw = (float) (Math.atan2(thePlayer.posX - x, -thePlayer.posZ + z) * 180 / Math.PI);
        float diff = yaw - currentYaw;
        if (diff < 0) {
            diff += 360;
        }
        float distanceToForward = Math.min(Math.abs(diff - 0), Math.abs(diff - 360)) % 360;
        float distanceToForwardRight = Math.abs(diff - 45) % 360;
        float distanceToRight = Math.abs(diff - 90) % 360;
        float distanceToBackwardRight = Math.abs(diff - 135) % 360;
        float distanceToBackward = Math.abs(diff - 180) % 360;
        float distanceToBackwardLeft = Math.abs(diff - 225) % 360;
        float distanceToLeft = Math.abs(diff - 270) % 360;
        float distanceToForwardLeft = Math.abs(diff - 315) % 360;
        float tmp = Math.round(diff / 45) * 45;
        if (tmp > 359) {
            tmp -= 360;
        }
        desiredYaw = yaw - tmp;
        //System.out.println(currentYaw + " " + yaw + " " + diff + " " + tmp + " " + desiredYaw);
        //System.out.println(distanceToForward + " " + distanceToLeft + " " + distanceToRight + " " + distanceToBackward);
        lookingYaw = true;
        if (distanceToForward < ANGLE_THRESHOLD || distanceToForward > 360 - ANGLE_THRESHOLD) {
            forward = true;
            return true;
        }
        if (distanceToForwardLeft < ANGLE_THRESHOLD || distanceToForwardLeft > 360 - ANGLE_THRESHOLD) {
            forward = true;
            left = true;
            return true;
        }
        if (distanceToForwardRight < ANGLE_THRESHOLD || distanceToForwardRight > 360 - ANGLE_THRESHOLD) {
            forward = true;
            right = true;
            return true;
        }
        if (distanceToBackward < ANGLE_THRESHOLD || distanceToBackward > 360 - ANGLE_THRESHOLD) {
            backward = true;
            return true;
        }
        if (distanceToBackwardLeft < ANGLE_THRESHOLD || distanceToBackwardLeft > 360 - ANGLE_THRESHOLD) {
            backward = true;
            left = true;
            return true;
        }
        if (distanceToBackwardRight < ANGLE_THRESHOLD || distanceToBackwardRight > 360 - ANGLE_THRESHOLD) {
            backward = true;
            right = true;
            return true;
        }
        if (distanceToLeft < ANGLE_THRESHOLD || distanceToLeft > 360 - ANGLE_THRESHOLD) {
            left = true;
            return true;
        }
        if (distanceToRight < ANGLE_THRESHOLD || distanceToRight > 360 - ANGLE_THRESHOLD) {
            right = true;
            return true;
        }
        return false;
    }
    /**
     * What block is the player looking at
     *
     * @return the position of it
     */
    public static BlockPos whatAreYouLookingAt() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            return mc.objectMouseOver.getBlockPos();
        }
        return null;
    }
    public static void switchtotool(Block b) {
        MineBot.switchtotool(b, new ToolSet());
    }
    public static void switchtotool(Block b, ToolSet ts) {
        Minecraft.theMinecraft.thePlayer.inventory.currentItem = ts.getBestSlot(b);
    }
    public static Entity what() {
        Minecraft mc = Minecraft.theMinecraft;
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return mc.objectMouseOver.entityHit;
        }
        return null;
    }
}
