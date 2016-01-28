package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C19PacketResourcePackStatus implements Packet<INetHandlerPlayServer> {
   private String field_179720_a;
   private C19PacketResourcePackStatus.Action field_179719_b;

   public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {
      this.field_179720_a = p_148837_1_.func_150789_c(40);
      this.field_179719_b = (C19PacketResourcePackStatus.Action)p_148837_1_.func_179257_a(C19PacketResourcePackStatus.Action.class);
   }

   public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.func_180714_a(this.field_179720_a);
      p_148840_1_.func_179249_a(this.field_179719_b);
   }

   public void func_148833_a(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.func_175086_a(this);
   }

   public static enum Action {
      SUCCESSFULLY_LOADED,
      DECLINED,
      FAILED_DOWNLOAD,
      ACCEPTED;
   }
}
