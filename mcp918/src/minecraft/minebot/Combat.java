
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.ArrayList;
import java.util.Comparator;
import static minebot.MineBot.findPathInNewThread;
import static minebot.MineBot.goal;
import static minebot.MineBot.isAir;
import static minebot.MineBot.what;
import minebot.pathfinding.goals.GoalBlock;
import minebot.pathfinding.goals.GoalRunAway;
import minebot.util.ManagerTick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 *
 * @author leijurv
 */
public class Combat extends ManagerTick {
    public static boolean mobHunting = false;
    public static boolean mobKilling = false;
    public static boolean playerHunt = false;
    public static Entity target = null;
    public static boolean wasTargetSetByMobHunt = false;
    @Override
    public boolean onTick0() {
        EntityPlayerSP thePlayer = Minecraft.theMinecraft.thePlayer;
        World theWorld = Minecraft.theMinecraft.theWorld;
        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        boolean healthOkToHunt = Minecraft.theMinecraft.thePlayer.getHealth() >= 12 || (target != null && target instanceof EntityPlayer);
        ArrayList<Entity> mobs = new ArrayList<Entity>();
        for (Entity entity : theWorld.loadedEntityList) {
            if (entity.isEntityAlive()) {
                if ((mobKilling && entity instanceof EntityMob) || ((playerHunt && (entity instanceof EntityPlayer) && !(entity.getName().equals(thePlayer.getName())) && !couldBeInCreative((EntityPlayer) entity)))) {
                    if (distFromMe(entity) < 5) {
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
            AxisAlignedBB lol = entity.getEntityBoundingBox();
            switchtosword();
            System.out.println("looking");
            LookManager.lookAtCoords((lol.minX + lol.maxX) / 2, (lol.minY + lol.maxY) / 2, (lol.minZ + lol.maxZ) / 2, true);
            if (entity.equals(MineBot.what())) {
                MineBot.isLeftClick = true;
                tickPath = false;
                System.out.println("Doing it");
            }
        }
        if (mobHunting && (target == null || wasTargetSetByMobHunt)) {
            ArrayList<Entity> mobs1 = new ArrayList<Entity>();
            for (Entity entity : theWorld.loadedEntityList) {
                if (entity.isEntityAlive()) {
                    if (!playerHunt && (entity instanceof EntityMob) && entity.posY > thePlayer.posY - 6) {
                        if (distFromMe(entity) < 30) {
                            mobs1.add(entity);
                        }
                    }
                    if ((playerHunt && (entity instanceof EntityPlayer) && !(entity.getName().equals(thePlayer.getName())) && !couldBeInCreative((EntityPlayer) entity))) {
                        if (distFromMe(entity) < 30) {
                            mobs1.add(entity);
                        }
                    }
                }
            }
            mobs1.sort(new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    return new Double(distFromMe(o1)).compareTo(distFromMe(o2));
                }
            });
            if (!mobs1.isEmpty()) {
                Entity entity = mobs1.get(0);
                if (!entity.equals(target)) {
                    if (!(!(entity instanceof EntityPlayer) && (target instanceof EntityPlayer) && playerHunt)) {//if playerhunt is true, dont overwrite a player target with a non player target
                        GuiScreen.sendChatMessage("Mobhunting=true. Killing " + entity, true);
                        if (MineBot.currentPath != null) {
                            MineBot.currentPath.clearPath();
                        }
                        MineBot.currentPath = null;
                        target = entity;
                        wasTargetSetByMobHunt = true;
                    }
                }
            }
        }
        if (!healthOkToHunt && target != null && wasTargetSetByMobHunt) {
            if (MineBot.currentPath != null) {
                if (!(MineBot.currentPath.goal instanceof GoalRunAway)) {
                    GuiScreen.sendChatMessage("Health too low, cancelling hunt", true);
                    if (MineBot.currentPath != null) {
                        MineBot.currentPath.clearPath();
                    }
                    MineBot.currentPath = null;
                }
            }
            MineBot.clearMovement();
            MineBot.goal = new GoalRunAway((int) target.posX, (int) target.posZ, 50);//TODO run away from more than one mob
            if (MineBot.currentPath == null) {
                GuiScreen.sendChatMessage("Running away", true);
                MineBot.findPathInNewThread(playerFeet, false);
            } else {
                GoalRunAway g = (GoalRunAway) MineBot.currentPath.goal;
                int xDiff = (int) (target.posX - g.x);
                int zDiff = (int) (target.posZ - g.z);
                int d = xDiff * xDiff + zDiff * zDiff;
                if (d > 5 * 5 && !MineBot.isThereAnythingInProgress) {
                    GuiScreen.sendChatMessage("Switching who I'm running away from", true);
                    MineBot.findPathInNewThread(playerFeet, false);
                }
            }
        }
        if (target != null && target.isDead) {
            GuiScreen.sendChatMessage(target + " is dead", true);
            target = null;
            if (MineBot.currentPath != null) {
                MineBot.currentPath.clearPath();
            }
            MineBot.currentPath = null;
            MineBot.clearMovement();
        }
        if (target != null && healthOkToHunt) {
            BlockPos targetPos = new BlockPos(target.posX, target.posY, target.posZ);
            MineBot.goal = new GoalBlock(targetPos);
            if (MineBot.currentPath != null) {
                double movementSince = dist(targetPos, MineBot.currentPath.end);
                if (movementSince > 4 && !MineBot.isThereAnythingInProgress) {
                    GuiScreen.sendChatMessage("They moved too much, " + movementSince + " blocks. recalculating", true);
                    MineBot.findPathInNewThread(playerFeet, false);//this will overwrite currentPath
                }
            }
            double dist = distFromMe(target);
            boolean actuallyLookingAt = target.equals(MineBot.what());
            //GuiScreen.sendChatMessage(dist + " " + actuallyLookingAt, true);
            if (dist > 4 && MineBot.currentPath == null) {
                MineBot.findPathInNewThread(playerFeet, false);
            }
            if (dist <= 4) {
                AxisAlignedBB lol = target.getEntityBoundingBox();
                switchtosword();
                boolean direction = LookManager.lookAtCoords((lol.minX + lol.maxX) / 2, (lol.minY + lol.maxY) / 2, (lol.minZ + lol.maxZ) / 2, true);
                if (direction && !actuallyLookingAt) {
                    MineBot.findPathInNewThread(playerFeet, false);
                }
            }
            if (actuallyLookingAt) {
                MineBot.isLeftClick = true;
                tickPath = false;
            }
        }
        return false;
    }
    public static double distFromMe(Entity a) {
        EntityPlayerSP player = Minecraft.theMinecraft.thePlayer;
        double diffX = player.posX - a.posX;
        double diffY = player.posY - a.posY;
        double diffZ = player.posZ - a.posZ;
        return Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
    }
    public static boolean couldBeInCreative(EntityPlayer player) {
        if (player.capabilities.isCreativeMode || player.capabilities.allowFlying || player.capabilities.isFlying) {
            return true;
        }
        BlockPos inFeet = new BlockPos(player.posX, player.posY, player.posZ);
        BlockPos standingOn = inFeet.down();
        return isAir(standingOn) && isAir(standingOn.north()) && isAir(standingOn.south()) && isAir(standingOn.east()) && isAir(standingOn.west()) && isAir(standingOn.north().west()) && isAir(standingOn.north().east()) && isAir(standingOn.south().west()) && isAir(standingOn.south().east());
    }
    public static double dist(BlockPos a, BlockPos b) {
        int diffX = a.getX() - b.getX();
        int diffY = a.getY() - b.getY();
        int diffZ = a.getZ() - b.getZ();
        return Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
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
                    bestDamage = damage;
                }
            }
            if (item.getItem() instanceof ItemAxe) {
                if (bestDamage == 0) {
                    p.inventory.currentItem = i;
                }
            }
        }
    }
    public static String killCommand(String name) {//TODO make this use Memory.playerLocationMemory
        BlockPos playerFeet = Minecraft.theMinecraft.thePlayer.getPosition0();
        if (name.length() > 2) {
            for (EntityPlayer pl : Minecraft.theMinecraft.theWorld.playerEntities) {
                String blah = pl.getName().trim().toLowerCase();
                if (!blah.equals(Minecraft.theMinecraft.thePlayer.getName().trim().toLowerCase())) {
                    GuiScreen.sendChatMessage("Considering " + blah, true);
                    if (Combat.couldBeInCreative(pl)) {
                        GuiScreen.sendChatMessage("No, creative", true);
                        continue;
                    }
                    if (blah.contains(name) || name.contains(blah)) {
                        Combat.target = pl;
                        Combat.wasTargetSetByMobHunt = false;
                        BlockPos pos = new BlockPos(Combat.target.posX, Combat.target.posY, Combat.target.posZ);
                        goal = new GoalBlock(pos);
                        findPathInNewThread(playerFeet, false);
                        return "Killing " + pl;
                    }
                }
            }
        }
        Entity w = what();
        if (w != null) {
            Combat.target = w;
            BlockPos pos = new BlockPos(Combat.target.posX, Combat.target.posY, Combat.target.posZ);
            goal = new GoalBlock(pos);
            Combat.wasTargetSetByMobHunt = false;
            findPathInNewThread(playerFeet, false);
            return "Killing " + w;
        }
        return "Couldn't find " + name;
    }
    @Override
    protected void onCancel() {
        target = null;
    }
    @Override
    protected void onStart() {
    }
    @Override
    protected boolean onEnabled(boolean enabled) {
        return true;
    }
}
