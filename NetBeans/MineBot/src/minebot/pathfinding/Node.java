/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

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
    public Node(BlockPos pos, Goal goal) {
        this.pos = pos;
        this.previous = null;
        this.cost = Short.MAX_VALUE;
        this.goal = goal;
        this.estimatedCostToGoal = goal.heuristic(pos);
    }
    @Override
    public int compareTo(Node o) {
        return new Integer(estimatedCostToGoal).compareTo(((Node) o).estimatedCostToGoal);
    }
}
