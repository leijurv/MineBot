/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.aiming;

import java.util.ArrayList;
import net.minecraft.block.material.Material;

/**
 *
 * @author galdara
 */
public class Constants {
    public static class PhysicsConstants {
        public static final double gravityAirArrow = -0.05;
        public static final double gravityWaterArrow = -0.6;
    }

    public static class BlockConstants {
        public static final ArrayList<Material> materialsLiquid = new ArrayList<>();
        public static final ArrayList<Material> materialsPassable = new ArrayList<>();
        static {
            //Materials Passable Start
            materialsPassable.add(Material.air);
            materialsPassable.add(Material.lava);
            materialsPassable.add(Material.vine);
            materialsPassable.add(Material.water);
            materialsPassable.add(Material.plants);
            //Materials Passable End
            //Materials Liquid Start
            materialsLiquid.add(Material.water);
            materialsLiquid.add(Material.lava);
            //Materials Liquid End
        }
    }

    public static class BowConstants {
        public static final double optimalBowDraw = 38;
        public static final double bowFullDraw = velocityFromDraw(1);
        public static double velocityFromDraw(double draw) {
            return (draw * 2) * 1.5;
        }
        public static final int renderTrajectoryCutoff = 200;
        public static final double renderTrajectoryIncrement = 0.25;
    }

    public static class ArrowConstants {
        public static final double drag = 0.99;
    }
}
