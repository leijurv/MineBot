/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.awt.Color;
import minebot.pathfinding.Path;
import minebot.pathfinding.PathFinder;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author leijurv
 */
public class PathRenderer {
    public static void render(EntityPlayer player, float partialTicks) {
        if (MineBot.currentPath != null) {
            drawPath(MineBot.currentPath, player, partialTicks, Color.RED);
        }
        if (MineBot.nextPath != null) {
            drawPath(MineBot.nextPath, player, partialTicks, Color.GREEN);
        }
        try {
            if (PathFinder.currentlyRunning != null) {
                Path p = PathFinder.currentlyRunning.getTempSolution();
                if (p != null) {
                    drawPath(p, player, partialTicks, Color.BLUE);
                }
            }
        } catch (Exception e) {
        }
    }
    public static void drawPath(Path path, EntityPlayer player, float partialTicks, Color color) {
        for (BlockPos pos : path.path) {
            drawSelectionBox(player, pos, partialTicks, color);
        }
    }
    public static void drawSelectionBox(EntityPlayer player, BlockPos blockpos, float partialTicks, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.getColorComponents(null)[0], color.getColorComponents(null)[1], color.getColorComponents(null)[2], 0.4F);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        float f = 0.002F;
        //BlockPos blockpos = movingObjectPositionIn.getBlockPos();
        Block block = Blocks.dirt;
        block.setBlockBoundsBasedOnState(Minecraft.theMinecraft.theWorld, blockpos);
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
        AxisAlignedBB toDraw = block.getSelectedBoundingBox(Minecraft.theMinecraft.theWorld, blockpos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(toDraw.minX, toDraw.minY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.minY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.minY, toDraw.maxZ).endVertex();
        worldrenderer.pos(toDraw.minX, toDraw.minY, toDraw.maxZ).endVertex();
        worldrenderer.pos(toDraw.minX, toDraw.minY, toDraw.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(toDraw.minX, toDraw.maxY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.maxY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.maxY, toDraw.maxZ).endVertex();
        worldrenderer.pos(toDraw.minX, toDraw.maxY, toDraw.maxZ).endVertex();
        worldrenderer.pos(toDraw.minX, toDraw.maxY, toDraw.minZ).endVertex();
        tessellator.draw();
        worldrenderer.begin(1, DefaultVertexFormats.POSITION);
        worldrenderer.pos(toDraw.minX, toDraw.minY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.minX, toDraw.maxY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.minY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.maxY, toDraw.minZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.minY, toDraw.maxZ).endVertex();
        worldrenderer.pos(toDraw.maxX, toDraw.maxY, toDraw.maxZ).endVertex();
        worldrenderer.pos(toDraw.minX, toDraw.minY, toDraw.maxZ).endVertex();
        worldrenderer.pos(toDraw.minX, toDraw.maxY, toDraw.maxZ).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
