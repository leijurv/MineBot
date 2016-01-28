package net.minecraft.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.stats.StatList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemPotion extends Item
{
    private Map<Integer, List<PotionEffect>> effectCache = Maps.<Integer, List<PotionEffect>>newHashMap();
    private static final Map<List<PotionEffect>, Integer> SUB_ITEMS_CACHE = Maps.<List<PotionEffect>, Integer>newLinkedHashMap();

    public ItemPotion()
    {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    public List<PotionEffect> getEffects(ItemStack stack)
    {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9))
        {
            List<PotionEffect> list1 = Lists.<PotionEffect>newArrayList();
            NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

            for (int i = 0; i < nbttaglist.tagCount(); ++i)
            {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);

                if (potioneffect != null)
                {
                    list1.add(potioneffect);
                }
            }

            return list1;
        }
        else
        {
            List<PotionEffect> list = (List)this.effectCache.get(Integer.valueOf(stack.getMetadata()));

            if (list == null)
            {
                list = PotionHelper.getPotionEffects(stack.getMetadata(), false);
                this.effectCache.put(Integer.valueOf(stack.getMetadata()), list);
            }

            return list;
        }
    }

    public List<PotionEffect> getEffects(int meta)
    {
        List<PotionEffect> list = (List)this.effectCache.get(Integer.valueOf(meta));

        if (list == null)
        {
            list = PotionHelper.getPotionEffects(meta, false);
            this.effectCache.put(Integer.valueOf(meta), list);
        }

        return list;
    }

    /**
     * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
     * the Item before the action is complete.
     */
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn)
    {
        if (!playerIn.capabilities.isCreativeMode)
        {
            --stack.stackSize;
        }

        if (!worldIn.isRemote)
        {
            List<PotionEffect> list = this.getEffects(stack);

            if (list != null)
            {
                for (PotionEffect potioneffect : list)
                {
                    playerIn.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }

        playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);

        if (!playerIn.capabilities.isCreativeMode)
        {
            if (stack.stackSize <= 0)
            {
                return new ItemStack(Items.glass_bottle);
            }

            playerIn.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
        }

        return stack;
    }

    /**
     * How long it takes to use or consume an item
     */
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
        if (isSplash(itemStackIn.getMetadata()))
        {
            if (!playerIn.capabilities.isCreativeMode)
            {
                --itemStackIn.stackSize;
            }

            worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!worldIn.isRemote)
            {
                worldIn.spawnEntityInWorld(new EntityPotion(worldIn, playerIn, itemStackIn));
            }

            playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
            return itemStackIn;
        }
        else
        {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
            return itemStackIn;
        }
    }

    /**
     * returns wether or not a potion is a throwable splash potion based on damage value
     */
    public static boolean isSplash(int meta)
    {
        return (meta & 16384) != 0;
    }

    public String getItemStackDisplayName(ItemStack stack)
    {
        if (stack.getMetadata() == 0)
        {
            return StatCollector.translateToLocal("item.emptyPotion.name").trim();
        }
        else
        {
            String s = "";

            if (isSplash(stack.getMetadata()))
            {
                s = StatCollector.translateToLocal("potion.prefix.grenade").trim() + " ";
            }

            List<PotionEffect> list = Items.potionitem.getEffects(stack);

            if (list != null && !list.isEmpty())
            {
                String s2 = ((PotionEffect)list.get(0)).getEffectName();
                s2 = s2 + ".postfix";
                return s + StatCollector.translateToLocal(s2).trim();
            }
            else
            {
                String s1 = PotionHelper.getPotionPrefix(stack.getMetadata());
                return StatCollector.translateToLocal(s1).trim() + " " + super.getItemStackDisplayName(stack);
            }
        }
    }
}
