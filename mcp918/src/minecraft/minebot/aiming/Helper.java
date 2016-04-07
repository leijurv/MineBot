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
public class Helper {
    
    public static double translateHalfAngle(double angle) {
        if(angle < 0) {
            return angle + (Math.PI * 2);
        } else {
            return angle;
        }
    }
    
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
