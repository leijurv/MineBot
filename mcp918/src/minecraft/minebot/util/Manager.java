/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

/**
 *
 * @author avecowa
 */
public abstract class Manager {

    private static boolean enabled = false;

    public void tick() {
        tick(null);
    }
    
    public void tick(Boolean prepost){
        if(!enabled()){
            return;
        }
        if(prepost==null){
            onTick();
        } else if(prepost){
            onTickPre();
        } else {
            onTickPost();
        }
    }

    public boolean enabled() {
        return onEnabled(enabled);
    }

    public void cancel() {
        enabled = false;
        onCancel();
    }

    public void start() {
        enabled = true;
        onStart();
    }

    public void toggle() {
        if (enabled()) {
            cancel();
        } else {
            start();
        }
    }

    protected void onTickPre(){}
    protected void onTickPost(){}
    
    protected boolean onEnabled(boolean enabled) {
        return enabled;
    }

    protected abstract void onTick();

    protected abstract void onCancel();

    protected abstract void onStart();

}
