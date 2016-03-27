/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.ui;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * I swear, this *isn't* copied from net.minecraft.util.ScreenShotHelper. Pinky
 * promise.
 *
 * @author leijurv
 */
public class Screenshot {
    /**
     * A buffer to hold pixel values returned by OpenGL.
     */
    private static IntBuffer pixelBuffer;
    /**
     * The built-up array that contains all the pixel values returned by OpenGL.
     */
    private static int[] pixelValues;
    public static BufferedImage screenshot() {
        int width = Minecraft.theMinecraft.displayWidth;
        int height = Minecraft.theMinecraft.displayHeight;
        Framebuffer buffer = Minecraft.theMinecraft.getFramebuffer();
        if (OpenGlHelper.isFramebufferEnabled()) {
            width = buffer.framebufferTextureWidth;
            height = buffer.framebufferTextureHeight;
        }
        int i = width * height;
        if (pixelBuffer == null || pixelBuffer.capacity() < i) {
            pixelBuffer = BufferUtils.createIntBuffer(i);
            pixelValues = new int[i];
        }
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();
        if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(buffer.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }
        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, width, height);
        BufferedImage bufferedimage;
        if (OpenGlHelper.isFramebufferEnabled()) {
            bufferedimage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
            int j = buffer.framebufferTextureHeight - buffer.framebufferHeight;
            for (int k = j; k < buffer.framebufferTextureHeight; ++k) {
                for (int l = 0; l < buffer.framebufferWidth; ++l) {
                    bufferedimage.setRGB(l, k - j, pixelValues[k * buffer.framebufferTextureWidth + l]);
                }
            }
        } else {
            bufferedimage = new BufferedImage(width, height, 1);
            bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
        }
        return bufferedimage;
    }
}
