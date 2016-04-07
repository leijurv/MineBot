/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.aiming;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;

/**
 *
 * @author galdara
 */
public class Arrow {
    private final double initVerticalVelocity;
    private final double initHorizontalVelocity;
    public boolean isInAir = true;
    public Arrow(EntityPlayer shooter, double velocity) {
        float rotationYaw = shooter.rotationYaw;
        float rotationPitch = shooter.rotationPitch;
        double x = (double) (-MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
        double y = (double) (MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI));
        double z = (double) (-MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI));
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        //x = x + this.rand.nextGaussian() * (double) (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) inaccuracy;
        //y = y + this.rand.nextGaussian() * (double) (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) inaccuracy;
        //z = z + this.rand.nextGaussian() * (double) (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) inaccuracy;
        x = x * (double) velocity;
        y = y * (double) velocity;
        z = z * (double) velocity;
        float f1 = MathHelper.sqrt_double(x * x + z * z);
        rotationYaw = (float) (MathHelper.func_181159_b(x, z) * 180.0D / Math.PI);
        rotationPitch = (float) (MathHelper.func_181159_b(y, (double) f1) * 180.0D / Math.PI);
        this.initVerticalVelocity = y;
        this.initHorizontalVelocity = Math.sqrt(x * x + z * z);
    }
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
    public double getVerticalVelocityAtTick(int tick) {
        double finalReturn = 0;
        finalReturn = Math.pow(Constants.ArrowConstants.drag, tick) * this.initVerticalVelocity;
        finalReturn += (isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow) * ((1 - Math.pow(Constants.ArrowConstants.drag, tick)) / (1 - Constants.ArrowConstants.drag));
        return finalReturn;
    }
    public double getHorizontalVelocityAtTick(int tick) {
        double finalReturn = 0;
        finalReturn = Math.pow(Constants.ArrowConstants.drag, tick) * this.initHorizontalVelocity;
        return finalReturn;
    }
    public double getVerticalPositionAtTick(int tick) {
        double finalReturn = 0;
        finalReturn = (((this.initVerticalVelocity - ((isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow) / (1 - Constants.ArrowConstants.drag)))
                * (1 - Math.pow(Constants.ArrowConstants.drag, tick))
                + (isInAir ? Constants.PhysicsConstants.gravityAirArrow : Constants.PhysicsConstants.gravityWaterArrow) * tick)
                / (1 - Constants.ArrowConstants.drag));
        return finalReturn;
    }
    public double getHorizontalPositionAtTick(int tick) {
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
    public Tuple<Double, Double> getPositionAtTick(int tick) {
        return new Tuple(getHorizontalPositionAtTick(tick), getVerticalPositionAtTick(tick));
    }
    public void setIsInAir(boolean isInAir) {
        this.isInAir = isInAir;
    }
}
