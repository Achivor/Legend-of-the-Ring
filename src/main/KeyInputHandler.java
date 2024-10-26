package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import model.Player;

public class KeyInputHandler implements KeyListener {
    private Player player;
    private static boolean interactPressed = false;
    private static boolean inventoryPressed = false;
    private static boolean quitPressed = false;
    private static boolean showCoordinates = false; // 新增：用于跟踪是否显示坐标

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
        if (key == KeyEvent.VK_Q) {
            quitPressed = true;
        }
        if (key == KeyEvent.VK_P) { // 显示坐标（新增）
            showCoordinates = true;
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
        if (key == KeyEvent.VK_Q) {
            quitPressed = false;
        }
        if (key == KeyEvent.VK_P) { // 显示坐标（新增）
            showCoordinates = false;
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

    public static boolean isQuitPressed() {
        return quitPressed;
    }

    public static boolean isShowCoordinates() { // 显示坐标（新增）
        return showCoordinates;
    }
}
