package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockBreakable extends Block {
   private boolean field_149996_a;

   protected BlockBreakable(Material p_i45712_1_, boolean p_i45712_2_) {
      this(p_i45712_1_, p_i45712_2_, p_i45712_1_.func_151565_r());
   }

   protected BlockBreakable(Material p_i46393_1_, boolean p_i46393_2_, MapColor p_i46393_3_) {
      super(p_i46393_1_, p_i46393_3_);
      this.field_149996_a = p_i46393_2_;
   }

   public boolean func_149662_c() {
      return false;
   }
}
