package net.minecraft.util;

import minebot.MineBot;
import minebot.movement.MovementManager;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;
    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;
        if (this.gameSettings.keyBindForward.isKeyDown() || MovementManager.forward) {
            ++this.moveForward;
        }
        if (this.gameSettings.keyBindBack.isKeyDown() || MovementManager.backward) {
            --this.moveForward;
        }
        if (this.gameSettings.keyBindLeft.isKeyDown() || MovementManager.left) {
            ++this.moveStrafe;
        }
        if (this.gameSettings.keyBindRight.isKeyDown() || MovementManager.right) {
            --this.moveStrafe;
        }
        this.jump = this.gameSettings.keyBindJump.isKeyDown() || MovementManager.jumping;
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown() || MovementManager.sneak;
        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
