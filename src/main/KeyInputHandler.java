package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import model.Player;

public class KeyInputHandler implements KeyListener {
    private Player player;
    private static boolean interactPressed = false;
    private static boolean inventoryPressed = false;

    public KeyInputHandler(Player player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) {
            player.setMovingUp(true);
        }
        if (key == KeyEvent.VK_S) {
            player.setMovingDown(true);
        }
        if (key == KeyEvent.VK_A) {
            player.setMovingLeft(true);
        }
        if (key == KeyEvent.VK_D) {
            player.setMovingRight(true);
        }
        if (key == KeyEvent.VK_E) {
            interactPressed = true;
        }
        if (key == KeyEvent.VK_R) {
            inventoryPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) {
            player.setMovingUp(false);
        }
        if (key == KeyEvent.VK_S) {
            player.setMovingDown(false);
        }
        if (key == KeyEvent.VK_A) {
            player.setMovingLeft(false);
        }
        if (key == KeyEvent.VK_D) {
            player.setMovingRight(false);
        }
        if (key == KeyEvent.VK_E) {
            interactPressed = false;
        }
        if (key == KeyEvent.VK_R) {
            inventoryPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 不需要实现
    }

    public static boolean isInteractPressed() {
        return interactPressed;
    }

    public static void resetInteractPressed() {
        interactPressed = false;
    }

    public static boolean isInventoryPressed() {
        return inventoryPressed;
    }
}
