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
    /**
     * Do the actual path calculation. The returned path might not actually go
     * to goal, but it will get as close as I could get
     *
     * @return
     */
    public Path calculatePath() {
        //a lot of these vars are local. that's because if someone tries to call this from multiple threads, they won't interfere (much)
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
        int numNodes = 0;
        while (openSet.first != null) {
            Node me = openSet.removeLowest();
            hashSet.remove(me);
            BlockPos myPos = me.pos;
            if (numNodes % 1000 == 0) {
                System.out.println("searching... at " + myPos + ", considered " + numNodes + " nodes so far");
            }
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
                        openSet.insert(neighbor);//dont double count, dont insert into open set if it's already there
                        hashSet.add(neighbor);
                    }
                }
                double sum = neighbor.estimatedCostToGoal + neighbor.cost / 2;
                if (sum < bestHeuristicSoFar) {
                    bestHeuristicSoFar = sum;
                    bestSoFar = neighbor;
                }
            }
            numNodes++;
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

    /**
     * My own implementation of a singly linked list
     */
    public static class OpenSet {
        ListNode first = null;
        public Node removeLowest() {
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
            double bestValue = Double.MAX_VALUE;
            Node bestNode = null;
            ListNode beforeBest = null;
            while (current != null) {
                Node element = current.element;
                double comp = element.comparison();
                if (bestNode == null || comp < bestValue) {
                    bestValue = comp;
                    bestNode = element;
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

        private static class ListNode {
            ListNode next = null;
            Node element = null;
            private ListNode(Node element) {
                this.element = element;
            }
        }
    }
}
