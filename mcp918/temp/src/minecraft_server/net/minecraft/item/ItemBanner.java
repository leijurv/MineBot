package net.minecraft.item;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock {
   public ItemBanner() {
      super(Blocks.field_180393_cK);
      this.field_77777_bU = 16;
      this.func_77637_a(CreativeTabs.field_78031_c);
      this.func_77627_a(true);
      this.func_77656_e(0);
   }

   public boolean func_180614_a(ItemStack p_180614_1_, EntityPlayer p_180614_2_, World p_180614_3_, BlockPos p_180614_4_, EnumFacing p_180614_5_, float p_180614_6_, float p_180614_7_, float p_180614_8_) {
      if(p_180614_5_ == EnumFacing.DOWN) {
         return false;
      } else if(!p_180614_3_.func_180495_p(p_180614_4_).func_177230_c().func_149688_o().func_76220_a()) {
         return false;
      } else {
         p_180614_4_ = p_180614_4_.func_177972_a(p_180614_5_);
         if(!p_180614_2_.func_175151_a(p_180614_4_, p_180614_5_, p_180614_1_)) {
            return false;
         } else if(!Blocks.field_180393_cK.func_176196_c(p_180614_3_, p_180614_4_)) {
            return false;
         } else if(p_180614_3_.field_72995_K) {
            return true;
         } else {
            if(p_180614_5_ == EnumFacing.UP) {
               int i = MathHelper.func_76128_c((double)((p_180614_2_.field_70177_z + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
               p_180614_3_.func_180501_a(p_180614_4_, Blocks.field_180393_cK.func_176223_P().func_177226_a(BlockStandingSign.field_176413_a, Integer.valueOf(i)), 3);
            } else {
               p_180614_3_.func_180501_a(p_180614_4_, Blocks.field_180394_cL.func_176223_P().func_177226_a(BlockWallSign.field_176412_a, p_180614_5_), 3);
            }

            --p_180614_1_.field_77994_a;
            TileEntity tileentity = p_180614_3_.func_175625_s(p_180614_4_);
            if(tileentity instanceof TileEntityBanner) {
               ((TileEntityBanner)tileentity).func_175112_a(p_180614_1_);
            }

            return true;
         }
      }
   }

   public String func_77653_i(ItemStack p_77653_1_) {
      String s = "item.banner.";
      EnumDyeColor enumdyecolor = this.func_179225_h(p_77653_1_);
      s = s + enumdyecolor.func_176762_d() + ".name";
      return StatCollector.func_74838_a(s);
   }

   private EnumDyeColor func_179225_h(ItemStack p_179225_1_) {
      NBTTagCompound nbttagcompound = p_179225_1_.func_179543_a("BlockEntityTag", false);
      EnumDyeColor enumdyecolor = null;
      if(nbttagcompound != null && nbttagcompound.func_74764_b("Base")) {
         enumdyecolor = EnumDyeColor.func_176766_a(nbttagcompound.func_74762_e("Base"));
      } else {
         enumdyecolor = EnumDyeColor.func_176766_a(p_179225_1_.func_77960_j());
      }

      return enumdyecolor;
   }
}
