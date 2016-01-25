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
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
        //if (MineBot.currentPath != null) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        double doubleX = Minecraft.theMinecraft.thePlayer.posX - 0.5;
        double doubleY = Minecraft.theMinecraft.thePlayer.posY + 0.1;
        double doubleZ = Minecraft.theMinecraft.thePlayer.posZ - 0.5;
        Tessellator tess = Tessellator.getInstance();
        System.out.println("m9");
        WorldRenderer worldRenderer = tess.getWorldRenderer();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION);
        worldRenderer.pos(doubleX, doubleY, doubleZ).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ + 10).endVertex();
        worldRenderer.pos(doubleX, doubleY, doubleZ + 10).endVertex();
        tess.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION);
        worldRenderer.pos(doubleX, doubleY, doubleZ).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ + 10).endVertex();
        worldRenderer.pos(doubleX, doubleY, doubleZ + 10).endVertex();
        tess.draw();
        worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(doubleX, doubleY, doubleZ).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ + 10).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        worldRenderer.pos(doubleX, doubleY, doubleZ + 10).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        tess.draw();
        worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(doubleX, doubleY, doubleZ).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        worldRenderer.pos(doubleX + 10, doubleY, doubleZ + 10).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        worldRenderer.pos(doubleX, doubleY, doubleZ + 10).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
        tess.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        //}
    }
    public static void drawPath1() {
        //System.out.println("yolo swag 420 noscope");
        if (MineBot.currentPath != null) {
            ArrayList<BlockPos> toDraw = MineBot.currentPath.path;
            double doubleX = Minecraft.theMinecraft.thePlayer.posX - 0.5;
            double doubleY = Minecraft.theMinecraft.thePlayer.posY + 0.1;
            double doubleZ = Minecraft.theMinecraft.thePlayer.posZ - 0.5;
            //GlStateManager.disableFog();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
            GlStateManager.disableCull();
            Tessellator tess = Tessellator.getInstance();
            WorldRenderer worldRenderer = tess.getWorldRenderer();
            GlStateManager.pushAttrib();
            GlStateManager.pushMatrix();
            worldRenderer.setTranslation(doubleX, doubleY, doubleZ);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(0, 0, 0).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
            worldRenderer.pos(10, 0, 0).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
            worldRenderer.pos(10, 0, 10).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
            worldRenderer.pos(0, 0, 10).color(0.937f, 0.016f, 0.424f, 0.5f).endVertex();
            tess.draw();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.enableAlpha();
            /*
             GlStateManager.enableBlend();


             //
             GlStateManager.color(0.937f, 0.016f, 0.424f);
             GL11.glLineWidth(10);
             System.out.println("a");
             GL11.glBegin(GL11.GL_LINES);
             System.out.println("b");
             GL11.glVertex2d(0, 0);
             GL11.glVertex2d(100, 100);
             //GL11.glVertex3f((float) doubleX + 10, (float) doubleY, (float) doubleZ);
             System.out.println("c");
             GL11.glEnd();
             GlStateManager.popMatrix();
             */
            //GlStateManager.enableFog();
        }
    }
}
