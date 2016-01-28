package net.minecraft.network.rcon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RConOutputStream {
   private ByteArrayOutputStream field_72674_a;
   private DataOutputStream field_72673_b;

   public RConOutputStream(int p_i1533_1_) {
      this.field_72674_a = new ByteArrayOutputStream(p_i1533_1_);
      this.field_72673_b = new DataOutputStream(this.field_72674_a);
   }

   public void func_72670_a(byte[] p_72670_1_) throws IOException {
      this.field_72673_b.write(p_72670_1_, 0, p_72670_1_.length);
   }

   public void func_72671_a(String p_72671_1_) throws IOException {
      this.field_72673_b.writeBytes(p_72671_1_);
      this.field_72673_b.write(0);
   }

   public void func_72667_a(int p_72667_1_) throws IOException {
      this.field_72673_b.write(p_72667_1_);
   }

   public void func_72668_a(short p_72668_1_) throws IOException {
      this.field_72673_b.writeShort(Short.reverseBytes(p_72668_1_));
   }

   public byte[] func_72672_a() {
      return this.field_72674_a.toByteArray();
   }

   public void func_72669_b() {
      this.field_72674_a.reset();
   }
}
