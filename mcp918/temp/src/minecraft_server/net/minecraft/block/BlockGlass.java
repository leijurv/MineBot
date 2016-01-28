package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockGlass extends BlockBreakable {
   public BlockGlass(Material p_i45408_1_, boolean p_i45408_2_) {
      super(p_i45408_1_, p_i45408_2_);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public int func_149745_a(Random p_149745_1_) {
      return 0;
   }

   public boolean func_149686_d() {
      return false;
   }

   protected boolean func_149700_E() {
      return true;
   }
}
