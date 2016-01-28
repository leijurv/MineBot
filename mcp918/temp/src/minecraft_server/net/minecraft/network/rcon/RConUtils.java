package net.minecraft.network.rcon;

import com.google.common.base.Charsets;

public class RConUtils {
   public static final char[] field_72666_a = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static String func_72661_a(byte[] p_72661_0_, int p_72661_1_, int p_72661_2_) {
      int i = p_72661_2_ - 1;

      int j;
      for(j = p_72661_1_ > i?i:p_72661_1_; 0 != p_72661_0_[j] && j < i; ++j) {
         ;
      }

      return new String(p_72661_0_, p_72661_1_, j - p_72661_1_, Charsets.UTF_8);
   }

   public static int func_72662_b(byte[] p_72662_0_, int p_72662_1_) {
      return func_72665_b(p_72662_0_, p_72662_1_, p_72662_0_.length);
   }

   public static int func_72665_b(byte[] p_72665_0_, int p_72665_1_, int p_72665_2_) {
      return 0 > p_72665_2_ - p_72665_1_ - 4?0:p_72665_0_[p_72665_1_ + 3] << 24 | (p_72665_0_[p_72665_1_ + 2] & 255) << 16 | (p_72665_0_[p_72665_1_ + 1] & 255) << 8 | p_72665_0_[p_72665_1_] & 255;
   }

   public static int func_72664_c(byte[] p_72664_0_, int p_72664_1_, int p_72664_2_) {
      return 0 > p_72664_2_ - p_72664_1_ - 4?0:p_72664_0_[p_72664_1_] << 24 | (p_72664_0_[p_72664_1_ + 1] & 255) << 16 | (p_72664_0_[p_72664_1_ + 2] & 255) << 8 | p_72664_0_[p_72664_1_ + 3] & 255;
   }

   public static String func_72663_a(byte p_72663_0_) {
      return "" + field_72666_a[(p_72663_0_ & 240) >>> 4] + field_72666_a[p_72663_0_ & 15];
   }
}
