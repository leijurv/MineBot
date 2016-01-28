package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemCoal extends Item {
   public ItemCoal() {
      this.func_77627_a(true);
      this.func_77656_e(0);
      this.func_77637_a(CreativeTabs.field_78035_l);
   }

   public String func_77667_c(ItemStack p_77667_1_) {
      return p_77667_1_.func_77960_j() == 1?"item.charcoal":"item.coal";
   }
}
