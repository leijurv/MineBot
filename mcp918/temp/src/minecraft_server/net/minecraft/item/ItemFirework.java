package net.minecraft.item;

import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemFirework extends Item {
   public boolean func_180614_a(ItemStack p_180614_1_, EntityPlayer p_180614_2_, World p_180614_3_, BlockPos p_180614_4_, EnumFacing p_180614_5_, float p_180614_6_, float p_180614_7_, float p_180614_8_) {
      if(!p_180614_3_.field_72995_K) {
         EntityFireworkRocket entityfireworkrocket = new EntityFireworkRocket(p_180614_3_, (double)((float)p_180614_4_.func_177958_n() + p_180614_6_), (double)((float)p_180614_4_.func_177956_o() + p_180614_7_), (double)((float)p_180614_4_.func_177952_p() + p_180614_8_), p_180614_1_);
         p_180614_3_.func_72838_d(entityfireworkrocket);
         if(!p_180614_2_.field_71075_bZ.field_75098_d) {
            --p_180614_1_.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }
}
