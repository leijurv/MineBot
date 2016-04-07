/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.aiming;

import minebot.ui.LookManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

/**
 *
 * @author galdara
 */
public class AimBow {
    public static double tick = 0;
    public static boolean canHit(BlockPos target) {
        return false;
    }
    public static void render(EntityPlayer player, float partialTicks) {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemBow) {
            drawArrowArc(player, new Arrow(Constants.BowConstants.bowFullDraw, Helper.degreesToRadians(player.rotationPitch * -1)), Color.BLUE, Color.RED, partialTicks);
        }
    }
    public static void drawArrowArc(EntityPlayer player, Arrow arrow, ReadableColor airColor, ReadableColor liquidColor, float partialTicks) {
        tick++;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(0.5F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.color(airColor.getRed(), airColor.getGreen(), airColor.getBlue());
        double previousDist = 0;
        double previousX = player.posX + previousDist - (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        double previousY = arrow.getVerticalPositionAtHorizontalPosition(previousDist) + player.posY + (double) player.getEyeHeight() - 0.10000000149011612D;;
        double previousZ = player.posZ + previousDist - (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
        for (double dist = Constants.BowConstants.renderTrajectoryIncrement; dist < Constants.BowConstants.renderTrajectoryCutoff; dist += Constants.BowConstants.renderTrajectoryIncrement) {
            BlockPos blockPos = new BlockPos(previousX, previousY, previousZ);
            IBlockState blockState = Minecraft.theMinecraft.theWorld.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (Constants.BlockConstants.materialsPassable.contains(block.getMaterial())) {
                if (Constants.BlockConstants.materialsLiquid.contains(block.getMaterial()) && arrow.isInAir) {
                    arrow.setIsInAir(false);
                    GlStateManager.color(liquidColor.getRed(), liquidColor.getGreen(), liquidColor.getBlue());
                } else if (!arrow.isInAir) {
                    arrow.setIsInAir(true);
                    GlStateManager.color(airColor.getRed(), airColor.getGreen(), airColor.getBlue());
                }
                double currentX = (Math.sin(Helper.degreesToRadians(Helper.translateHalfAngle(player.rotationYaw * -1))) * dist) + player.posX - (double) (MathHelper.cos(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
                double currentY = arrow.getVerticalPositionAtHorizontalPosition(dist) + player.posY + (double) player.getEyeHeight() - 0.10000000149011612D;
                double currentZ = (Math.cos(Helper.degreesToRadians(Helper.translateHalfAngle(player.rotationYaw * -1))) * dist) + player.posZ - (double) (MathHelper.sin(player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F);
                drawLine(player, previousX, previousY, previousZ, currentX, currentY, currentZ, partialTicks);
                if (tick == 60) {
                    System.out.println("--- NEW TOTAL TICK ---");
                    System.out.println("Current yaw: " + player.rotationYaw);
                    System.out.println("Current position: X" + player.posX + "  #  Y" + player.posY + "  #  Z" + player.posZ);
                    System.out.println("Previous X: " + previousX);
                    System.out.println("Previous Y: " + previousY);
                    System.out.println("Previous Z: " + previousZ);
                    System.out.println("Current X: " + currentX);
                    System.out.println("Current Y: " + currentY);
                    System.out.println("Current Z: " + currentZ);
                    System.out.println("---   END OF TICK  ---");
                }
                previousX = currentX;
                previousY = currentY;
                previousZ = currentZ;
            } else {
                drawSelectionBox(player, blockPos, partialTicks, (arrow.isInAir ? airColor : liquidColor));
                break;
            }
        }
        if (tick == 60) {
            tick = 0;
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawLine(EntityPlayer player, double bp1x, double bp1y, double bp1z, double bp2x, double bp2y, double bp2z, float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
        worldrenderer.begin(3, DefaultVertexFormats.POSITION);
        worldrenderer.pos(bp1x - d0, bp1y - d1, bp1z - d2).endVertex();
        worldrenderer.pos(bp2x - d0, bp2y - d1, bp2z - d2).endVertex();
        worldrenderer.pos(bp2x - d0, bp2y - d1, bp2z - d2).endVertex();
        worldrenderer.pos(bp1x - d0, bp1y - d1, bp1z - d2).endVertex();
        worldrenderer.pos(bp1x - d0, bp1y - d1, bp1z - d2).endVertex();
        tessellator.draw();
    }
    public static void drawSelectionBox(EntityPlayer player, BlockPos blockpos, float partialTicks, ReadableColor color) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), 0.4F);
        GL11.glLineWidth(5.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        float f = 0.002F;
        //BlockPos blockpos = movingObjectPositionIn.getBlockPos();
        Block block = Minecraft.theMinecraft.theWorld.getBlockState(blockpos).getBlock();
        if (block.equals(Blocks.air)) {
            block = Blocks.dirt;
        }
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
    public static void aimAtEntity(Entity entity) {
        LookManager.setDesiredYaw((float) Math.atan2(entity.posX - Minecraft.theMinecraft.thePlayer.posX, entity.posZ - Minecraft.theMinecraft.thePlayer.posZ));
    }
}
