package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockLeavesBase extends Block {
   protected boolean field_150121_P;

   protected BlockLeavesBase(Material p_i45433_1_, boolean p_i45433_2_) {
      super(p_i45433_1_);
      this.field_150121_P = p_i45433_2_;
   }

   public boolean func_149662_c() {
      return false;
   }
}
