/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.HashMap;
import java.util.HashSet;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class PathFinder {
    final BlockPos start;
    final Goal goal;
    final HashMap<BlockPos, Node> map;
    public PathFinder(BlockPos start, Goal goal) {
        this.start = start;
        this.goal = goal;
        this.map = new HashMap<>();
    }
    public Path calculatePath() {
        final Node startNode = getNodeAtPosition(start);
        startNode.cost = 0;
        Node bestSoFar = null;
        double bestHeuristicSoFar = Double.MAX_VALUE;
        OpenSet openSet = new OpenSet();
        openSet.insert(startNode);
        HashSet<Node> hashSet = new HashSet<>();
        hashSet.add(startNode);
        long startTime = System.currentTimeMillis();
        long timeoutTime = startTime + 10000;
        while (openSet.first != null) {
            Node me = openSet.removeFirst();
            hashSet.remove(me);
            BlockPos myPos = me.pos;
            System.out.println("searching... " + myPos);
            if (goal.isInGoal(myPos)) {
                return new Path(startNode, me, goal);
            }
            Action[] connected = getConnectedPositions(myPos);
            for (Action actionToGetToNeighbor : connected) {
                Node neighbor = getNodeAtPosition(actionToGetToNeighbor.to);
                double tentativeCost = me.cost + actionToGetToNeighbor.calculateCost();
                if (tentativeCost < neighbor.cost) {
                    neighbor.previous = me;
                    neighbor.previousAction = actionToGetToNeighbor;
                    neighbor.cost = tentativeCost;
                    if (!hashSet.contains(neighbor)) {
                        openSet.insert(neighbor);
                        hashSet.add(neighbor);
                    }
                }
                double sum = neighbor.estimatedCostToGoal + neighbor.cost / 2;
                if (sum < bestHeuristicSoFar) {
                    bestHeuristicSoFar = sum;
                    bestSoFar = neighbor;
                }
            }
            if (System.currentTimeMillis() > timeoutTime) {
                System.out.println("Stopping");
                return new Path(startNode, bestSoFar, goal);
            }
        }
        throw new IllegalStateException("bad");
    }
    private Node getNodeAtPosition(BlockPos pos) {
        if (map.get(pos) == null) {
            map.put(pos, new Node(pos, goal));
        }
        return map.get(pos);
    }
    public static Action[] getConnectedPositions(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        BlockPos[] positions = new BlockPos[13];
        positions[0] = new BlockPos(x, y + 1, z);//pillar
        positions[1] = new BlockPos(x + 1, y, z);//bridge
        positions[2] = new BlockPos(x - 1, y, z);//bridge
        positions[3] = new BlockPos(x, y, z + 1);//bridge
        positions[4] = new BlockPos(x, y, z - 1);//bridge
        positions[5] = new BlockPos(x + 1, y + 1, z);//climb
        positions[6] = new BlockPos(x - 1, y + 1, z);//climb
        positions[7] = new BlockPos(x, y + 1, z + 1);//climb
        positions[8] = new BlockPos(x, y + 1, z - 1);//climb
        positions[9] = new BlockPos(x + 1, y - 1, z);//fall
        positions[10] = new BlockPos(x - 1, y - 1, z);//fall
        positions[11] = new BlockPos(x, y - 1, z + 1);//fall
        positions[12] = new BlockPos(x, y - 1, z - 1);//fall
        Action[] actions = new Action[13];
        for (int i = 0; i < 13; i++) {
            actions[i] = Action.getAction(pos, positions[i]);
        }
        return actions;
    }

    public static class OpenSet {
        ListNode first = null;
        public Node removeFirst() {
            if (first == null) {
                return null;
            }
            if (first.next == null) {
                Node n = first.element;
                first = null;
                return n;
            }
            ListNode current = first;
            ListNode previous = null;
            double bestValue = -5021;
            Node bestNode = null;
            ListNode beforeBest = null;
            while (current != null) {
                Node e = current.element;
                if (bestValue == -5021 || e.comparison() < bestValue) {
                    bestValue = e.comparison();
                    bestNode = e;
                    beforeBest = previous;
                }
                previous = current;
                current = current.next;
            }
            if (beforeBest == null) {
                first = first.next;
                return bestNode;
            }
            beforeBest.next = beforeBest.next.next;
            return bestNode;
        }
        public void insert(Node node) {
            ListNode n = new ListNode(node);
            n.next = first;
            first = n;
        }
        /*public String toString() {
         ListNode current = first;
         while (current != null) {
         if (current.next != null) {
         System.out.println(current.element.compareTo(current.next.element));
         }
         current = current.next;
         }
         return "";
         }
         public void insert(Node node) {
         if (first == null) {
         first = new ListNode(node);
         return;
         }
         if (node.compareTo(first.element) <= 0) {
         ListNode toInsert = new ListNode(node);
         toInsert.next = first;
         first = toInsert;
         }
         ListNode ln = first.next;
         ListNode previous = first;
         while (ln != null) {
         if (node.compareTo(ln.element) <= 0) {
         ListNode toInsert = new ListNode(node);
         toInsert.next = ln;
         previous.next = toInsert;
         return;
         }
         previous = ln;
         ln = ln.next;
         }
         previous.next = new ListNode(node);
         }*/

        public static class ListNode {
            ListNode next = null;
            Node element = null;
            public ListNode(Node element) {
                this.element = element;
            }
        }
    }
}
