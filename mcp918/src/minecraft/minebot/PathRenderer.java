/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import static net.minecraft.client.renderer.RenderGlobal.func_181561_a;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author leijurv
 */
public class PathRenderer {
    public static void render(EntityPlayer player, float partialTicks) {
        if (MineBot.currentPath != null) {
            for (BlockPos pos : MineBot.currentPath.path) {
                drawSelectionBox(player, pos, partialTicks);
            }
        }
    }
    public static void drawSelectionBox(EntityPlayer player, BlockPos blockpos, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        float f = 0.002F;
        //BlockPos blockpos = movingObjectPositionIn.getBlockPos();
        Block block = Blocks.dirt;
        if (block.getMaterial() != Material.air && Minecraft.theMinecraft.theWorld.getWorldBorder().contains(blockpos)) {
            block.setBlockBoundsBasedOnState(Minecraft.theMinecraft.theWorld, blockpos);
            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
            func_181561_a(block.getSelectedBoundingBox(Minecraft.theMinecraft.theWorld, blockpos).expand(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).offset(-d0, -d1, -d2));
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
