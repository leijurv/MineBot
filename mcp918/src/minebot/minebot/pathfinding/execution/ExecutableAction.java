/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.execution;

/**
 *
 * @author avecowa
 */
public abstract class ExecutableAction {
    public abstract boolean run();
    public Type type;
    public Direction direction;
    public static enum Type{BREAK, CLIMB, FALL, PLACE, WALK};
    public static enum Direction{NORTH, SOUTH, EAST, WEST, UP, DOWN};
}
