/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.HashMap;
import java.util.LinkedList;
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
        PriorityList openList = new PriorityList();
        openList.addNode(startNode);
        int numNodes = 0;
        long startTime = System.currentTimeMillis();
        long timeoutTime = startTime + 20000;
        while (!openList.isEmpty()) {
            Node me = openList.removeFirst();
            BlockPos myPos = me.pos;
            System.out.println("searching... " + myPos);
            if (goal.isInGoal(me.pos)) {
                return new Path(startNode, me, goal);
            }
            Action[] connected = getConnectedPositions(me.pos);
            for (Action actionToGetToNeighbor : connected) {
                Node neighbor = getNodeAtPosition(actionToGetToNeighbor.to);
                double tentativeCost = me.cost + actionToGetToNeighbor.calculateCost();
                if (tentativeCost < neighbor.cost) {
                    neighbor.previous = me;
                    neighbor.previousAction = actionToGetToNeighbor;
                    neighbor.cost = tentativeCost;
                    if (!openList.contains(neighbor)) {
                        openList.addNode(neighbor);
                    }
                }
                double sum = neighbor.estimatedCostToGoal + neighbor.cost;
                if (sum < bestHeuristicSoFar) {
                    bestHeuristicSoFar = sum;
                    bestSoFar = neighbor;
                }
            }
            numNodes++;
            if (numNodes > 5000 || System.currentTimeMillis() > timeoutTime) {
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

    public static class PriorityList extends LinkedList<Node> {
        public void addNode(Node object) {
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
