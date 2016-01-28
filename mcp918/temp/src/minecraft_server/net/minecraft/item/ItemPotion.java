package net.minecraft.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item {
   private Map<Integer, List<PotionEffect>> field_77836_a = Maps.<Integer, List<PotionEffect>>newHashMap();
   private static final Map<List<PotionEffect>, Integer> field_77835_b = Maps.<List<PotionEffect>, Integer>newLinkedHashMap();

   public ItemPotion() {
      this.func_77625_d(1);
      this.func_77627_a(true);
      this.func_77656_e(0);
      this.func_77637_a(CreativeTabs.field_78038_k);
   }

   public List<PotionEffect> func_77832_l(ItemStack p_77832_1_) {
      if(p_77832_1_.func_77942_o() && p_77832_1_.func_77978_p().func_150297_b("CustomPotionEffects", 9)) {
         List<PotionEffect> list1 = Lists.<PotionEffect>newArrayList();
         NBTTagList nbttaglist = p_77832_1_.func_77978_p().func_150295_c("CustomPotionEffects", 10);

         for(int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
            PotionEffect potioneffect = PotionEffect.func_82722_b(nbttagcompound);
            if(potioneffect != null) {
               list1.add(potioneffect);
            }
         }

         return list1;
      } else {
         List<PotionEffect> list = (List)this.field_77836_a.get(Integer.valueOf(p_77832_1_.func_77960_j()));
         if(list == null) {
            list = PotionHelper.func_77917_b(p_77832_1_.func_77960_j(), false);
            this.field_77836_a.put(Integer.valueOf(p_77832_1_.func_77960_j()), list);
         }

         return list;
      }
   }

   public List<PotionEffect> func_77834_f(int p_77834_1_) {
      List<PotionEffect> list = (List)this.field_77836_a.get(Integer.valueOf(p_77834_1_));
      if(list == null) {
         list = PotionHelper.func_77917_b(p_77834_1_, false);
         this.field_77836_a.put(Integer.valueOf(p_77834_1_), list);
      }

      return list;
   }

   public ItemStack func_77654_b(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_) {
      if(!p_77654_3_.field_71075_bZ.field_75098_d) {
         --p_77654_1_.field_77994_a;
      }

      if(!p_77654_2_.field_72995_K) {
         List<PotionEffect> list = this.func_77832_l(p_77654_1_);
         if(list != null) {
            for(PotionEffect potioneffect : list) {
               p_77654_3_.func_70690_d(new PotionEffect(potioneffect));
            }
         }
      }

      p_77654_3_.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
      if(!p_77654_3_.field_71075_bZ.field_75098_d) {
         if(p_77654_1_.field_77994_a <= 0) {
            return new ItemStack(Items.field_151069_bo);
         }

         p_77654_3_.field_71071_by.func_70441_a(new ItemStack(Items.field_151069_bo));
      }

      return p_77654_1_;
   }

   public int func_77626_a(ItemStack p_77626_1_) {
      return 32;
   }

   public EnumAction func_77661_b(ItemStack p_77661_1_) {
      return EnumAction.DRINK;
   }

   public ItemStack func_77659_a(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_) {
      if(func_77831_g(p_77659_1_.func_77960_j())) {
         if(!p_77659_3_.field_71075_bZ.field_75098_d) {
            --p_77659_1_.field_77994_a;
         }

         p_77659_2_.func_72956_a(p_77659_3_, "random.bow", 0.5F, 0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F));
         if(!p_77659_2_.field_72995_K) {
            p_77659_2_.func_72838_d(new EntityPotion(p_77659_2_, p_77659_3_, p_77659_1_));
         }

         p_77659_3_.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
         return p_77659_1_;
      } else {
         p_77659_3_.func_71008_a(p_77659_1_, this.func_77626_a(p_77659_1_));
         return p_77659_1_;
      }
   }

   public static boolean func_77831_g(int p_77831_0_) {
      return (p_77831_0_ & 16384) != 0;
   }

   public String func_77653_i(ItemStack p_77653_1_) {
      if(p_77653_1_.func_77960_j() == 0) {
         return StatCollector.func_74838_a("item.emptyPotion.name").trim();
      } else {
         String s = "";
         if(func_77831_g(p_77653_1_.func_77960_j())) {
            s = StatCollector.func_74838_a("potion.prefix.grenade").trim() + " ";
         }

         List<PotionEffect> list = Items.field_151068_bn.func_77832_l(p_77653_1_);
         if(list != null && !list.isEmpty()) {
            String s2 = ((PotionEffect)list.get(0)).func_76453_d();
            s2 = s2 + ".postfix";
            return s + StatCollector.func_74838_a(s2).trim();
         } else {
            String s1 = PotionHelper.func_77905_c(p_77653_1_.func_77960_j());
            return StatCollector.func_74838_a(s1).trim() + " " + super.func_77653_i(p_77653_1_);
         }
      }
   }
}
