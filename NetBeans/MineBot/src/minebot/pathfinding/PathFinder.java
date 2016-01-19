/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.HashMap;
import java.util.LinkedList;

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
    public void calculatePath() {
        final Node startNode = getNodeAtPosition(start);
        startNode.cost = 0;
        PriorityList openList = new PriorityList();
        LinkedList<Node> closedList = new LinkedList<>();
        openList.addNode(startNode);
        while (!openList.isEmpty()) {
            Node me = openList.removeFirst();
            BlockPos myPos = me.pos;
            if (goal.isInGoal(me.pos)) {
                //done
                return;
            }
            closedList.add(me);
            BlockPos[] connected = getConnectedPositions(me.pos);
            for (BlockPos neighborPos : connected) {
                Node neighbor = getNodeAtPosition(neighborPos);
                if (closedList.contains(neighbor)) {
                    continue;//ignore the neighbor which is already evaluated
                }
                int tentativeCost = me.cost + getCost(myPos, neighborPos);
                if (!openList.contains(neighbor)) {
                    openList.addNode(neighbor);
                } else if (tentativeCost >= neighbor.cost) {
                    continue;//this is not a better path
                }
            }
        }
    }
    private Node getNodeAtPosition(BlockPos pos) {
        if (map.get(pos) == null) {
            map.put(pos, new Node(pos, goal));
        }
        return map.get(pos);
    }
    private int getCost(BlockPos from, BlockPos to) {
        return 1;
    }
    public BlockPos[] getConnectedPositions(BlockPos pos) {
        BlockPos[] positions = new BlockPos[13];
        positions[0] = new BlockPos(pos.x, pos.y + 1, pos.z);//pillar
        positions[1] = new BlockPos(pos.x + 1, pos.y, pos.z);//bridge
        positions[2] = new BlockPos(pos.x - 1, pos.y, pos.z);//bridge
        positions[3] = new BlockPos(pos.x, pos.y, pos.z + 1);//bridge
        positions[4] = new BlockPos(pos.x, pos.y, pos.z - 1);//bridge
        positions[5] = new BlockPos(pos.x + 1, pos.y + 1, pos.z);//climb
        positions[6] = new BlockPos(pos.x - 1, pos.y + 1, pos.z);//climb
        positions[7] = new BlockPos(pos.x, pos.y + 1, pos.z + 1);//climb
        positions[8] = new BlockPos(pos.x, pos.y + 1, pos.z - 1);//climb
        positions[9] = new BlockPos(pos.x + 1, pos.y - 1, pos.z);//fall
        positions[10] = new BlockPos(pos.x - 1, pos.y - 1, pos.z);//fall
        positions[11] = new BlockPos(pos.x, pos.y - 1, pos.z + 1);//fall
        positions[12] = new BlockPos(pos.x, pos.y - 1, pos.z - 1);//fall
        return positions;
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
