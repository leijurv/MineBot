/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.LinkedList;

/**
 *
 * @author leijurv
 */
public class PathFinder {
    final BlockPos start;
    final Goal goal;
    public PathFinder(BlockPos start, Goal goal) {
        this.start = start;
        this.goal = goal;
    }
    public void calculatePath() {
        final Node startNode = new Node(start, goal);
        startNode.cost = 0;
        PriorityList openList = new PriorityList();
        LinkedList closedList = new LinkedList();
        openList.addComparable(startNode);
        while (!openList.isEmpty()) {
            Node node = (Node) openList.removeFirst();
        }
    }

    public static class PriorityList extends LinkedList {
        public void addComparable(Comparable object) {
            for (int i = 0; i < size(); i++) {
                if (object.compareTo(get(i)) <= 0) {
                    add(i, object);
                    return;
                }
            }
            addLast(object);
        }
    }
}
