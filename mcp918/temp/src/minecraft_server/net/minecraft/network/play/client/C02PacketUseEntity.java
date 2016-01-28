package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class C02PacketUseEntity implements Packet<INetHandlerPlayServer> {
   private int field_149567_a;
   private C02PacketUseEntity.Action field_149566_b;
   private Vec3 field_179713_c;

   public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {
      this.field_149567_a = p_148837_1_.func_150792_a();
      this.field_149566_b = (C02PacketUseEntity.Action)p_148837_1_.func_179257_a(C02PacketUseEntity.Action.class);
      if(this.field_149566_b == C02PacketUseEntity.Action.INTERACT_AT) {
         this.field_179713_c = new Vec3((double)p_148837_1_.readFloat(), (double)p_148837_1_.readFloat(), (double)p_148837_1_.readFloat());
      }

   }

   public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.func_150787_b(this.field_149567_a);
      p_148840_1_.func_179249_a(this.field_149566_b);
      if(this.field_149566_b == C02PacketUseEntity.Action.INTERACT_AT) {
         p_148840_1_.writeFloat((float)this.field_179713_c.field_72450_a);
         p_148840_1_.writeFloat((float)this.field_179713_c.field_72448_b);
         p_148840_1_.writeFloat((float)this.field_179713_c.field_72449_c);
      }

   }

   public void func_148833_a(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.func_147340_a(this);
   }

   public Entity func_149564_a(World p_149564_1_) {
      return p_149564_1_.func_73045_a(this.field_149567_a);
   }

   public C02PacketUseEntity.Action func_149565_c() {
      return this.field_149566_b;
   }

   public Vec3 func_179712_b() {
      return this.field_179713_c;
   }

   public static enum Action {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }
}
