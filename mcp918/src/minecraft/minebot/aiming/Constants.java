/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.aiming;

/**
 *
 * @author galdara
 */
public class Constants {
    public static class PhysicsConstants {
        public static final double gravityAirArrow = -0.05;
        public static final double gravityWaterArrow = -0.6;
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
    
    public static class Conversion {
        public static double degreesToRadians(double degrees) {
            return (Math.PI / 180) * degrees;
        }
        
        public static double radiansToDegrees(double radians) {
            return (180 / Math.PI) * radians;
        }
        
        public static double logOfBase(double base, double num) {
            return Math.log(num) / Math.log(base);
        }
    }
}
