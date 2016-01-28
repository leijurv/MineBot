package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class BlockBreakable extends Block
{
    private boolean ignoreSimilarity;

    protected BlockBreakable(Material materialIn, boolean ignoreSimilarityIn)
    {
        this(materialIn, ignoreSimilarityIn, materialIn.getMaterialMapColor());
    }

    protected BlockBreakable(Material p_i46393_1_, boolean p_i46393_2_, MapColor p_i46393_3_)
    {
        super(p_i46393_1_, p_i46393_3_);
        this.ignoreSimilarity = p_i46393_2_;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
}
