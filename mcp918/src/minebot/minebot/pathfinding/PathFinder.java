/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.HashMap;
import net.minecraft.client.gui.GuiScreen;
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
    static final int NUM_HEURISTICS = 5;
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
        Node[] bestSoFar = new Node[NUM_HEURISTICS];
        double[] bestHeuristicSoFar = new double[bestSoFar.length];
        for (int i = 0; i < bestHeuristicSoFar.length; i++) {
            bestHeuristicSoFar[i] = Double.MAX_VALUE;
        }
        OpenSet openSet = new OpenSet();
        openSet.insert(startNode);
        long startTime = System.currentTimeMillis();
        long timeoutTime = startTime + 10000;
        int numNodes = 0;
        while (openSet.first != null) {
            Node me = openSet.removeLowest();
            me.isOpen = false;
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
                    if (!neighbor.isOpen) {
                        openSet.insert(neighbor);//dont double count, dont insert into open set if it's already there
                        neighbor.isOpen = true;
                    }
                    for (int i = 0; i < bestSoFar.length; i++) {
                        double sum = neighbor.estimatedCostToGoal + neighbor.cost / (i + 1);
                        if (sum < bestHeuristicSoFar[i]) {
                            bestHeuristicSoFar[i] = sum;
                            bestSoFar[i] = neighbor;
                        }
                    }
                }
            }
            numNodes++;
            if (System.currentTimeMillis() > timeoutTime) {
                System.out.println("Stopping");
                double bestDist = 0;
                for (int i = 0; i < bestSoFar.length; i++) {
                    double dist = dist(bestSoFar[i]);
                    if (dist > bestDist) {
                        bestDist = dist;
                    }
                    if (dist > MIN_DIST_PATH) {
                        if (i != 0) {
                            GuiScreen.sendChatMessage("A* cost coefficient " + (i + 1), true);
                            GuiScreen.sendChatMessage("Path goes for " + dist + " blocks", true);
                            if (i > 2) {
                                GuiScreen.sendChatMessage("Warning: cost coefficient is greater than three! Probably means that", true);
                                GuiScreen.sendChatMessage("the path I found is pretty terrible (like sneak-bridging for dozens of blocks)", true);
                                GuiScreen.sendChatMessage("But I'm going to do it anyway, because yolo", true);
                            }
                        }
                        return new Path(startNode, bestSoFar[i], goal);
                    }
                }
                GuiScreen.sendChatMessage("Even with a cost coefficient of " + bestSoFar.length + ", I couldn't get more than " + bestDist + " blocks =(", true);
                GuiScreen.sendChatMessage("No path found =(", true);
                return null;
            }
        }
        throw new IllegalStateException("bad");
    }
    private double dist(Node n) {
        int xDiff = n.pos.getX() - start.getX();
        int yDiff = n.pos.getY() - start.getY();
        int zDiff = n.pos.getZ() - start.getZ();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
    }
    private final double MIN_DIST_PATH = 5;
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
        /*BlockPos[] positions = new BlockPos[13];
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
         }*/
        //new implementation should have exact same effect
        Action[] actions = new Action[13];
        actions[0] = new ActionPillar(pos, new BlockPos(x, y + 1, z));
        actions[1] = new ActionBridge(pos, new BlockPos(x + 1, y, z));
        actions[2] = new ActionBridge(pos, new BlockPos(x - 1, y, z));
        actions[3] = new ActionBridge(pos, new BlockPos(x, y, z + 1));
        actions[4] = new ActionBridge(pos, new BlockPos(x, y, z - 1));
        actions[5] = new ActionClimb(pos, new BlockPos(x + 1, y + 1, z));
        actions[6] = new ActionClimb(pos, new BlockPos(x - 1, y + 1, z));
        actions[7] = new ActionClimb(pos, new BlockPos(x, y + 1, z + 1));
        actions[8] = new ActionClimb(pos, new BlockPos(x, y + 1, z - 1));
        actions[9] = new ActionFall(pos, new BlockPos(x + 1, y - 1, z));
        actions[10] = new ActionFall(pos, new BlockPos(x - 1, y - 1, z));
        actions[11] = new ActionFall(pos, new BlockPos(x, y - 1, z + 1));
        actions[12] = new ActionFall(pos, new BlockPos(x, y - 1, z - 1));
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
