package net.minecraft.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockEnchantmentTable extends BlockContainer {
   protected BlockEnchantmentTable() {
      super(Material.field_151576_e, MapColor.field_151645_D);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
      this.func_149713_g(0);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public int func_149645_b() {
      return 3;
   }

   public TileEntity func_149915_a(World p_149915_1_, int p_149915_2_) {
      return new TileEntityEnchantmentTable();
   }

   public boolean func_180639_a(World p_180639_1_, BlockPos p_180639_2_, IBlockState p_180639_3_, EntityPlayer p_180639_4_, EnumFacing p_180639_5_, float p_180639_6_, float p_180639_7_, float p_180639_8_) {
      if(p_180639_1_.field_72995_K) {
         return true;
      } else {
         TileEntity tileentity = p_180639_1_.func_175625_s(p_180639_2_);
         if(tileentity instanceof TileEntityEnchantmentTable) {
            p_180639_4_.func_180468_a((TileEntityEnchantmentTable)tileentity);
         }

         return true;
      }
   }

   public void func_180633_a(World p_180633_1_, BlockPos p_180633_2_, IBlockState p_180633_3_, EntityLivingBase p_180633_4_, ItemStack p_180633_5_) {
      super.func_180633_a(p_180633_1_, p_180633_2_, p_180633_3_, p_180633_4_, p_180633_5_);
      if(p_180633_5_.func_82837_s()) {
         TileEntity tileentity = p_180633_1_.func_175625_s(p_180633_2_);
         if(tileentity instanceof TileEntityEnchantmentTable) {
            ((TileEntityEnchantmentTable)tileentity).func_145920_a(p_180633_5_.func_82833_r());
         }
      }

   }
}
