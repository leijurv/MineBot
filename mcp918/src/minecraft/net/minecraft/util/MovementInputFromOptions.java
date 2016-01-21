package net.minecraft.util;

import minebot.MineBot;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;
    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;
        if (this.gameSettings.keyBindForward.isKeyDown() || MineBot.forward) {
            ++this.moveForward;
        }
        if (this.gameSettings.keyBindBack.isKeyDown() || MineBot.backward) {
            --this.moveForward;
        }
        if (this.gameSettings.keyBindLeft.isKeyDown() || MineBot.left) {
            ++this.moveStrafe;
        }
        if (this.gameSettings.keyBindRight.isKeyDown() || MineBot.right) {
            --this.moveStrafe;
        }
        this.jump = this.gameSettings.keyBindJump.isKeyDown() || MineBot.jumping;
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown() || MineBot.sneak;
        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
