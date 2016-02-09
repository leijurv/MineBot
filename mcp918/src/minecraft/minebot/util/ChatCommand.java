/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Random;
import minebot.AnotherStealer;
import minebot.Combat;
import minebot.LookManager;
import minebot.Memory;
import minebot.MineBot;
import minebot.SketchyStealer;
import minebot.mining.MickeyMine;
import minebot.pathfinding.goals.GoalBlock;
import minebot.pathfinding.goals.GoalGetToBlock;
import minebot.pathfinding.goals.GoalXZ;
import minebot.pathfinding.goals.GoalYLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 *
 * @author avecowa
 */
public class ChatCommand {
    private static WorldClient theWorld() {
        return Minecraft.theMinecraft.theWorld;
    }
    private static EntityPlayerSP thePlayer() {
        return Minecraft.theMinecraft.thePlayer;
    }
    private static ArrayList<Field> fields;
    private static ArrayList<Method> methods;
    private static Method DONTYOUDARE;
    static {
        DONTYOUDARE = null;
//        try {
//            DONTYOUDARE = ChatCommand.class.getMethod("message", String.class);
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(ChatCommand.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(ChatCommand.class.getName()).log(Level.SEVERE, null, ex);
//        }
        methods = new ArrayList<Method>();
        fields = new ArrayList<Field>();
        addMethods(ChatCommand.class);
        addFields(MineBot.class);
        addFields(Combat.class);
    }
    public static void addFields(Class<?> c) {
        Field[] temp = c.getFields();
        for (Field f : temp) {
            if (f.getType().equals(boolean.class) && Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())) {
                fields.add(f);
            }
        }
    }
    public static void addMethods(Class<?> c) {
        Method[] temp = c.getDeclaredMethods();
        for (Method m : temp) {
            if (m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(String.class) && m.getReturnType().equals(String.class) && !m.equals(DONTYOUDARE)) {
                methods.add(m);
            }
        }
    }
    public static boolean message(String message) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("MSG: " + message);
        String text = (message.charAt(0) == '/' ? message.substring(1) : message).trim();
        String command = text.split(" ")[0];
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(command)) {
                GuiScreen.sendChatMessage((String) method.invoke(null, text));
                return true;
            }
        }
        int argc = text.split(" ").length;
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(command)) {
                boolean value = argc == 1 ? !field.getBoolean(null) : Boolean.parseBoolean(text.split(" ")[2]);
                field.setBoolean(null, value);
                GuiScreen.sendChatMessage(command + " is now " + value);
                return true;
            }
        }
        return false;
    }
    public static String set(String message) throws IllegalArgumentException, IllegalAccessException {
        int argc = message.split(" ").length;
        if (argc <= 1) {
            return "Arguments plz";
        }
        String item = message.split(" ")[1];
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(item)) {
                boolean value;
                if (argc == 2) {
                    value = !field.getBoolean(null);
                } else {
                    value = Boolean.parseBoolean(message.split(" ")[2]);
                }
                field.setBoolean(null, value);
                return item + " is now " + value;
            }
        }
        return "THATS NOT A THING";
    }

    public static String importfrom(String message) throws ClassNotFoundException{
        String[] args = message.split(" ");
        if(args.length!=3 || (!"m".equals(args[1]) && !"f".equals(args[1]))){
            return "import (m/f) class";
        }
        Class c = Class.forName(args[2]);
        if(args[1].equals("m")) addMethods(c); else addFields(c);
        return "Added from "+c;
    }
    public static String death(String message) {
        MineBot.goal = new GoalBlock(MineBot.death);
        return "Set goal to " + MineBot.goal;
    }
    public static String craft(String message) {
        String spec = message.substring(5).trim();
        if (spec.length() > 0) {
            String item = spec.split(" ")[0];
            String amt = spec.split(" ")[1];
            ItemStack stack = new ItemStack(Item.getByNameOrId(item), Integer.parseInt(amt));
            System.out.println(CraftingTask.findOrCreateCraftingTask(stack));
        }
        return "k";
    }
    public static String smelt(String message) {
        String spec = message.substring(5).trim();
        if (spec.length() > 0) {
            String item = spec.split(" ")[0];
            String amt = spec.split(" ")[1];
            ItemStack stack = new ItemStack(Item.getByNameOrId(item), Integer.parseInt(amt));
            new SmeltingTask(stack).begin();
        } else {
            new SmeltingTask(Minecraft.theMinecraft.thePlayer.getCurrentEquippedItem()).begin();
        }
        return "k";
    }
    public static String clearbh(String message) {
        String substr = message.substring(7).trim();
        if (substr.equals("crafting_table")) {
            MineBot.setCraftingHome(null);
        } else if (substr.equals("furnace")) {
            return "WAIT ONE GODDAMN MINUTE IM NOT READY 4 THIS";
        }
        return "";
    }
    public static String containeritem(String message) {
        CraftingTask.getRecipeFromItem(thePlayer().getCurrentEquippedItem().getItem());
        return "k";
    }
    public static String ore(String message) {
        MickeyMine.toggleOre(message.substring(3).trim());
        return "";
    }
    public static String mine(String message) {
        if (!(MineBot.mreowMine ^= true)) {
            MickeyMine.clear();
        }
        return "Mreow mine: " + MineBot.mreowMine;
    }
    public static String wizard(String message) {
        return "YOURE A LIZARD HARRY " + (MineBot.isThereAnythingInProgress ^= true);
    }
    public static String actuallyTalk(String message) {
        MineBot.actuallyPutMessagesInChat ^= true;
        return "toggled to " + MineBot.actuallyPutMessagesInChat;
    }
    public static String allowPlaceOrBreak(String message) {
        return adventure(message);
    }
    public static String adventure(String message) {
        return "allowBreakOrPlace: " + (MineBot.allowBreakOrPlace ^= true);
    }
    public static String steal(String message) {
        return stealer(message);
    }
    public static String save(String message) {
        String t = message.substring(4).trim();
        if (MineBot.goal == null) {
            return "no goal to save";
        }
        if (!(MineBot.goal instanceof GoalBlock)) {
            return "sorry, goal has to be instanceof GoalBlock";
        }
        Memory.goalMemory.put(t, ((GoalBlock) MineBot.goal).pos());
        return "Saved " + MineBot.goal + " under " + t;
    }
    public static String load(String message) {
        return "Set goal to " + (MineBot.goal = new GoalBlock(Memory.goalMemory.get(message.substring(4).trim())));
    }
    public static String random(String message) {
        double dist = Double.parseDouble(message.substring("random direction".length()).trim());
        double ang = new Random().nextDouble() * Math.PI * 2;
        GuiScreen.sendChatMessage("Angle: " + ang, true);
        BlockPos playerFeet = new BlockPos(thePlayer().posX, thePlayer().posY, thePlayer().posZ);
        int x = playerFeet.getX() + (int) (Math.sin(ang) * dist);
        int z = playerFeet.getZ() + (int) (Math.cos(ang) * dist);
        MineBot.goal = new GoalXZ(x, z);
        return "Set goal to " + MineBot.goal;
    }
    public static String findgo(String message) {
        return Memory.findGoCommand(message.substring(6).trim());
    }
    public static String find(String message) {
        return Memory.findCommand(message.substring(4).trim());
    }
    public static String look(String message) {
        LookManager.lookAtBlock(new BlockPos(0, 0, 0), true);
        return "";
    }
    public static String cancel(String message) {
        MineBot.cancelPath();
        MineBot.plsCancel = true;
        Combat.target = null;
        MickeyMine.clear();
        MineBot.mreowMine = false;
        MineBot.fullAuto = false;
        SmeltingTask.cancelAll();
        return MineBot.isThereAnythingInProgress ? "Cancelled it, but btw I'm pathfinding right now" : "Cancelled it";
    }
    public static String cancelfurnace(String message) {
        SmeltingTask.clearInProgress();
        return "k =)";
    }
    public static String st(String message) {
        WorldClient theWorld = theWorld();
        EntityPlayerSP thePlayer = thePlayer();
        BlockPos playerFeet = new BlockPos(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
        GuiScreen.sendChatMessage(MineBot.info(playerFeet), true);
        GuiScreen.sendChatMessage(MineBot.info(playerFeet.down()), true);
        GuiScreen.sendChatMessage(MineBot.info(playerFeet.up()), true);
        System.out.println(theWorld.getBlockState(playerFeet).getBlock());
        System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 1, thePlayer.posZ)).getBlock());
        System.out.println(theWorld.getBlockState(new BlockPos(thePlayer.posX, thePlayer.posY - 2, thePlayer.posZ)).getBlock());
        return "";
    }
    public static String setgoal(String message) {
        return goal(message);
    }
    public static String goal(String message) {
        MineBot.plsCancel = false;
        int ind = message.indexOf(' ') + 1;
        if (ind == 0) {
            MineBot.goal = new GoalBlock(thePlayer().playerFeet());
            return "Set goal to " + MineBot.goal;
        }
        String[] strs = message.substring(ind).split(" ");
        int[] coords = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            try {
                coords[i] = Integer.parseInt(strs[i]);
            } catch (NumberFormatException nfe) {
                MineBot.goal = new GoalBlock();
                return strs[i] + ". yup. A+ coordinate";//A+? you might even say A*
            }
        }
        switch (strs.length) {
            case 3:
                MineBot.goal = new GoalBlock(coords[0], coords[1], coords[2]);
                break;
            case 2:
                MineBot.goal = new GoalXZ(coords[0], coords[1]);
                break;
            case 1:
                MineBot.goal = new GoalYLevel(coords[0]);
                break;
            default:
                MineBot.goal = new GoalBlock();
                if (strs.length != 0) {
                    return strs.length + " coordinates. Nice.";
                }
                break;
        }
        return "Set goal to " + MineBot.goal;
    }
    public static String gotoblock(String message) {
        return Memory.gotoCommand(message.substring(4).trim().toLowerCase());
    }
    public static String kill(String message) {
        return Combat.killCommand(message.substring(4).trim().toLowerCase());
    }
    public static String player(String message) {
        return Memory.playerCommand(message.substring(6).trim());
    }
    public static String thisway(String message) {
        return "Set goal to " + (MineBot.goal = LookManager.fromAngleAndDirection(Double.parseDouble(message.substring(7).trim())));
    }
    public static String path(String message) {
        MineBot.plsCancel = false;
        MineBot.findPathInNewThread(thePlayer().playerFeet(), true);
        return "";
    }
    public static String hardness(String message) {
        BlockPos bp = MineBot.whatAreYouLookingAt();
        return bp == null ? "0" : (1 / theWorld().getBlockState(bp).getBlock().getPlayerRelativeBlockHardness(thePlayer(), theWorld(), MineBot.whatAreYouLookingAt())) + "";
    }
    public static String info(String message) {
        return MineBot.info(MineBot.whatAreYouLookingAt());
    }
    public static String toggle(String message) throws IllegalArgumentException, IllegalAccessException {
        return set(message);
    }
    public static String stealer(String message) {
        MineBot.sketchyStealer = false;
        AnotherStealer.stuff = true;
        AnotherStealer.chestStuff = false;
        return "stealer: " + (MineBot.stealer ^= true);
    }
    
    public static String printtag(String message) throws IOException {
        SchematicLoader.loadFromFile(new File("/Users/galdara/Downloads/schematics/Bakery.schematic"));
        return "printed schematic to console.";
    }
    
    public static String getToGoal(String message) {
        MineBot.plsCancel = false;
        int ind = message.indexOf(' ') + 1;
        if (ind == 0) {
            MineBot.goal = new GoalGetToBlock(thePlayer().playerFeet());
            return "Set goal to " + MineBot.goal;
        }
        String[] strs = message.substring(ind).split(" ");
        int[] coords = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            try {
                coords[i] = Integer.parseInt(strs[i]);
            } catch (NumberFormatException nfe) {
                MineBot.goal = new GoalGetToBlock();
                return strs[i] + ". yup. A+ coordinate";//A+? you might even say A*
            }
        }
        switch (strs.length) {
            case 3:
                MineBot.goal = new GoalGetToBlock(new BlockPos(coords[0], coords[1], coords[2]));
                break;
            default:
                MineBot.goal = new GoalGetToBlock();
                if (strs.length != 0) {
                    return strs.length + " coordinates. Nice.";
                }
                break;
        }
        return "Set goal to " + MineBot.goal;
    }
}