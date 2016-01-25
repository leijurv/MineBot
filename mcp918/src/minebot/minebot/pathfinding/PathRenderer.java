/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.ArrayList;
import minebot.MineBot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author galdara
 */
public class PathRenderer {

    public PathRenderer() {
    }
    
    public static void drawPath() {
        if(MineBot.currentPath != null) {
            ArrayList<BlockPos> toDraw=MineBot.currentPath.path;
                    
        double doubleX = Minecraft.theMinecraft.thePlayer.posX - 0.5;
        double doubleY = Minecraft.theMinecraft.thePlayer.posY + 0.1;
        double doubleZ = Minecraft.theMinecraft.thePlayer.posZ - 0.5;
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
        GlStateManager.color(0.937f, 0.016f, 0.424f);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3f((float) doubleX,(float) doubleY,(float) doubleZ);
        GL11.glVertex3f((float) doubleX + 1,(float) doubleY,(float) doubleZ);
        GL11.glEnd();
        GlStateManager.popMatrix();
        }
    }
}   
