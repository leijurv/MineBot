/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.Objects;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class Node implements Comparable<Node> {
    final BlockPos pos;
    int cost;
    Node previous;
    final Goal goal;
    final int estimatedCostToGoal;
    Action previousAction;
    public Node(BlockPos pos, Goal goal) {
        this.pos = pos;
        this.previous = null;
        this.cost = Short.MAX_VALUE;
        this.goal = goal;
        this.estimatedCostToGoal = goal.heuristic(pos);
        this.previousAction = null;
    }
    @Override
    public int compareTo(Node otherNode) {
        return new Integer(estimatedCostToGoal + cost).compareTo(otherNode.estimatedCostToGoal + otherNode.cost);
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.pos);
        hash = 53 * hash + Objects.hashCode(this.goal);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!Objects.equals(this.pos, other.pos)) {
            return false;
        }
        if (!Objects.equals(this.goal, other.goal)) {
            return false;
        }
        return true;
    }
}
