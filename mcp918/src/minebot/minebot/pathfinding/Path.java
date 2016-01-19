/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding;

import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Path {
    final BlockPos start;
    final BlockPos end;
    final Goal goal;
    final ArrayList<BlockPos> path;
    Path(Node start, Node end, Goal goal) {
        this.start = start.pos;
        this.end = end.pos;
        this.goal = goal;
        path = new ArrayList<>();
        Node current = end;
        while (!current.equals(start)) {
            path.add(0, current.pos);
            current = current.previous;
        }
        path.add(0, start.pos);
    }
}
