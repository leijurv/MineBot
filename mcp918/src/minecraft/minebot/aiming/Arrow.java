/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.aiming;

import net.minecraft.util.Tuple;

/**
 *
 * @author galdara
 */
public class Arrow {
    private final double initVerticalVelocity;
    private final double initHorizontalVelocity;
    public boolean isInAir = true;
    public Arrow(double initialVelocity, double angleFired) {
        this.initVerticalVelocity = verticalVelocityFromTotal(initialVelocity, angleFired);
        this.initHorizontalVelocity = horizontalVelocityFromTotal(initialVelocity, angleFired);
    }
    protected static double verticalVelocityFromTotal(double velocity, double angleFired) {
        return velocity * Math.sin(angleFired);
    }
    protected static double horizontalVelocityFromTotal(double velocity, double angleFired) {
        return velocity * Math.cos(angleFired);
    }
    public double getVerticalVelocityAtTick(double tick) {
        double finalReturn = 0;
        finalReturn = Math.pow(Constants.ArrowConstants.drag, tick) * this.initVerticalVelocity;
        finalReturn += (isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow) * ((1 - Math.pow(Constants.ArrowConstants.drag, tick)) / (1 - Constants.ArrowConstants.drag));
        return finalReturn;
    }
    public double getHorizontalVelocityAtTick(double tick) {
        double finalReturn = 0;
        finalReturn = Math.pow(Constants.ArrowConstants.drag, tick) * this.initHorizontalVelocity;
        return finalReturn;
    }
    public double getVerticalPositionAtTick(double tick) {
        double finalReturn = 0;
        finalReturn = (((this.initVerticalVelocity - ((isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow) / (1 - Constants.ArrowConstants.drag)))
                * (1 - Math.pow(Constants.ArrowConstants.drag, tick))
                + (isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow) * tick)
                / (1 - Constants.ArrowConstants.drag));
        return finalReturn;
    }
    public double getHorizontalPositionAtTick(double tick) {
        double finalReturn = 0;
        finalReturn = (((1 - Math.pow(Constants.ArrowConstants.drag, tick))
                / (1 - Constants.ArrowConstants.drag))
                * this.initHorizontalVelocity);
        return finalReturn;
    }
    public double getTickAtHorizontalPosition(double position) {
        double finalReturn = 0;
        finalReturn = (Constants.Conversion.logOfBase(
                Constants.ArrowConstants.drag,
                (1
                - ((position * (1 - Constants.ArrowConstants.drag))
                / (this.initHorizontalVelocity)))
        ));
        return finalReturn;
    }
    public double getVerticalPositionAtHorizontalPosition(double position) {
        double finalReturn = 0;
        finalReturn = (((this.initVerticalVelocity
                - ((isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow)
                / (1 - Constants.ArrowConstants.drag)))
                * (1
                - Math.pow(Constants.ArrowConstants.drag, getTickAtHorizontalPosition(position)))
                + (isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow)
                * getTickAtHorizontalPosition(position))
                / (1 - Constants.ArrowConstants.drag));
        return finalReturn;
    }
    public Tuple<Double, Double> getPositionAtTick(double tick) {
        return new Tuple(getHorizontalPositionAtTick(tick), getVerticalPositionAtTick(tick));
    }
    public void setIsInAir(boolean isInAir) {
        this.isInAir = isInAir;
    }
}
