/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.Objects;

/**
 *
 * @author leijurv
 */
public class Node implements Comparable {
    final BlockPos pos;
    int cost;
    Node previous;
    final Goal goal;
    final int estimatedCostToGoal;
    public Node(BlockPos pos, Goal goal) {
        this.pos = pos;
        this.previous = null;
        this.cost = Short.MAX_VALUE;
        this.goal = goal;
        this.estimatedCostToGoal = goal.heuristic(pos);
    }
    @Override
    public int compareTo(Object o) {
        return new Integer(estimatedCostToGoal + cost).compareTo(((Node) o).estimatedCostToGoal + ((Node) o).cost);
    }
    public boolean equals(Object o) {
        return true;
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.pos);
        hash = 53 * hash + Objects.hashCode(this.goal);
        return hash;
    }
}
