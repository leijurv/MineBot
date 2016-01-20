/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.pathfinding.execution;

/**
 *
 * @author avecowa
 */
public class ExecutableWalk extends ExecutableAction{

    @Override
    public boolean run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public ExecutableWalk(Direction direction){
        this.direction = direction;
        this.type = Type.WALK;
    }
    
}
