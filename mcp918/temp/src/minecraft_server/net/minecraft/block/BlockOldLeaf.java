package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockOldLeaf extends BlockLeaves {
   public static final PropertyEnum<BlockPlanks.EnumType> field_176239_P = PropertyEnum.<BlockPlanks.EnumType>func_177708_a("variant", BlockPlanks.EnumType.class, new Predicate<BlockPlanks.EnumType>() {
      public boolean apply(BlockPlanks.EnumType p_apply_1_) {
         return p_apply_1_.func_176839_a() < 4;
      }
   });

   public BlockOldLeaf() {
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176239_P, BlockPlanks.EnumType.OAK).func_177226_a(field_176236_b, Boolean.valueOf(true)).func_177226_a(field_176237_a, Boolean.valueOf(true)));
   }

   protected void func_176234_a(World p_176234_1_, BlockPos p_176234_2_, IBlockState p_176234_3_, int p_176234_4_) {
      if(p_176234_3_.func_177229_b(field_176239_P) == BlockPlanks.EnumType.OAK && p_176234_1_.field_73012_v.nextInt(p_176234_4_) == 0) {
         func_180635_a(p_176234_1_, p_176234_2_, new ItemStack(Items.field_151034_e, 1, 0));
      }

   }

   protected int func_176232_d(IBlockState p_176232_1_) {
      return p_176232_1_.func_177229_b(field_176239_P) == BlockPlanks.EnumType.JUNGLE?40:super.func_176232_d(p_176232_1_);
   }

   protected ItemStack func_180643_i(IBlockState p_180643_1_) {
      return new ItemStack(Item.func_150898_a(this), 1, ((BlockPlanks.EnumType)p_180643_1_.func_177229_b(field_176239_P)).func_176839_a());
   }

   public IBlockState func_176203_a(int p_176203_1_) {
      return this.func_176223_P().func_177226_a(field_176239_P, this.func_176233_b(p_176203_1_)).func_177226_a(field_176237_a, Boolean.valueOf((p_176203_1_ & 4) == 0)).func_177226_a(field_176236_b, Boolean.valueOf((p_176203_1_ & 8) > 0));
   }

   public int func_176201_c(IBlockState p_176201_1_) {
      int i = 0;
      i = i | ((BlockPlanks.EnumType)p_176201_1_.func_177229_b(field_176239_P)).func_176839_a();
      if(!((Boolean)p_176201_1_.func_177229_b(field_176237_a)).booleanValue()) {
         i |= 4;
      }

      if(((Boolean)p_176201_1_.func_177229_b(field_176236_b)).booleanValue()) {
         i |= 8;
      }

      return i;
   }

   public BlockPlanks.EnumType func_176233_b(int p_176233_1_) {
      return BlockPlanks.EnumType.func_176837_a((p_176233_1_ & 3) % 4);
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176239_P, field_176236_b, field_176237_a});
   }

   public int func_180651_a(IBlockState p_180651_1_) {
      return ((BlockPlanks.EnumType)p_180651_1_.func_177229_b(field_176239_P)).func_176839_a();
   }

   public void func_180657_a(World p_180657_1_, EntityPlayer p_180657_2_, BlockPos p_180657_3_, IBlockState p_180657_4_, TileEntity p_180657_5_) {
      if(!p_180657_1_.field_72995_K && p_180657_2_.func_71045_bC() != null && p_180657_2_.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         p_180657_2_.func_71029_a(StatList.field_75934_C[Block.func_149682_b(this)]);
         func_180635_a(p_180657_1_, p_180657_3_, new ItemStack(Item.func_150898_a(this), 1, ((BlockPlanks.EnumType)p_180657_4_.func_177229_b(field_176239_P)).func_176839_a()));
      } else {
         super.func_180657_a(p_180657_1_, p_180657_2_, p_180657_3_, p_180657_4_, p_180657_5_);
      }
   }
}
