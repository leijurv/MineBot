package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Queue;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

public class BlockSponge extends Block {
   public static final PropertyBool field_176313_a = PropertyBool.func_177716_a("wet");

   protected BlockSponge() {
      super(Material.field_151583_m);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176313_a, Boolean.valueOf(false)));
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + ".dry.name");
   }

   public int func_180651_a(IBlockState p_180651_1_) {
      return ((Boolean)p_180651_1_.func_177229_b(field_176313_a)).booleanValue()?1:0;
   }

   public void func_176213_c(World p_176213_1_, BlockPos p_176213_2_, IBlockState p_176213_3_) {
      this.func_176311_e(p_176213_1_, p_176213_2_, p_176213_3_);
   }

   public void func_176204_a(World p_176204_1_, BlockPos p_176204_2_, IBlockState p_176204_3_, Block p_176204_4_) {
      this.func_176311_e(p_176204_1_, p_176204_2_, p_176204_3_);
      super.func_176204_a(p_176204_1_, p_176204_2_, p_176204_3_, p_176204_4_);
   }

   protected void func_176311_e(World p_176311_1_, BlockPos p_176311_2_, IBlockState p_176311_3_) {
      if(!((Boolean)p_176311_3_.func_177229_b(field_176313_a)).booleanValue() && this.func_176312_d(p_176311_1_, p_176311_2_)) {
         p_176311_1_.func_180501_a(p_176311_2_, p_176311_3_.func_177226_a(field_176313_a, Boolean.valueOf(true)), 2);
         p_176311_1_.func_175718_b(2001, p_176311_2_, Block.func_149682_b(Blocks.field_150355_j));
      }

   }

   private boolean func_176312_d(World p_176312_1_, BlockPos p_176312_2_) {
      Queue<Tuple<BlockPos, Integer>> queue = Lists.<Tuple<BlockPos, Integer>>newLinkedList();
      ArrayList<BlockPos> arraylist = Lists.<BlockPos>newArrayList();
      queue.add(new Tuple(p_176312_2_, Integer.valueOf(0)));
      int i = 0;

      while(!((Queue)queue).isEmpty()) {
         Tuple<BlockPos, Integer> tuple = (Tuple)queue.poll();
         BlockPos blockpos = (BlockPos)tuple.func_76341_a();
         int j = ((Integer)tuple.func_76340_b()).intValue();

         for(EnumFacing enumfacing : EnumFacing.values()) {
            BlockPos blockpos1 = blockpos.func_177972_a(enumfacing);
            if(p_176312_1_.func_180495_p(blockpos1).func_177230_c().func_149688_o() == Material.field_151586_h) {
               p_176312_1_.func_180501_a(blockpos1, Blocks.field_150350_a.func_176223_P(), 2);
               arraylist.add(blockpos1);
               ++i;
               if(j < 6) {
                  queue.add(new Tuple(blockpos1, Integer.valueOf(j + 1)));
               }
            }
         }

         if(i > 64) {
            break;
         }
      }

      for(BlockPos blockpos2 : arraylist) {
         p_176312_1_.func_175685_c(blockpos2, Blocks.field_150350_a);
      }

      return i > 0;
   }

   public IBlockState func_176203_a(int p_176203_1_) {
      return this.func_176223_P().func_177226_a(field_176313_a, Boolean.valueOf((p_176203_1_ & 1) == 1));
   }

   public int func_176201_c(IBlockState p_176201_1_) {
      return ((Boolean)p_176201_1_.func_177229_b(field_176313_a)).booleanValue()?1:0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176313_a});
   }
}
