package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload implements Packet<INetHandlerPlayServer> {
   private String field_149562_a;
   private PacketBuffer field_149561_c;

   public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {
      this.field_149562_a = p_148837_1_.func_150789_c(20);
      int i = p_148837_1_.readableBytes();
      if(i >= 0 && i <= 32767) {
         this.field_149561_c = new PacketBuffer(p_148837_1_.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.func_180714_a(this.field_149562_a);
      p_148840_1_.writeBytes((ByteBuf)this.field_149561_c);
   }

   public void func_148833_a(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.func_147349_a(this);
   }

   public String func_149559_c() {
      return this.field_149562_a;
   }

   public PacketBuffer func_180760_b() {
      return this.field_149561_c;
   }
}
