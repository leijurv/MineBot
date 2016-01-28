package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class ItemDoublePlant extends ItemMultiTexture
{
    public ItemDoublePlant(Block block, Block block2, Function<ItemStack, String> nameFunction)
    {
        super(block, block2, nameFunction);
    }
}
