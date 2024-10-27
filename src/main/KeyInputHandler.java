/**
 *
 * @author Xiangjie Dong
 * @id 2072645
 * @author Yanhang Luo
 * @id 2123061
 */

package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import model.Player;

/**
 * Handle keyboard input events in the game.
 */
public class KeyInputHandler implements KeyListener {
    /** Player instance. */
    private final Player player;
    /** Whether the interact key is pressed. */
    private static boolean interactPressed = false;
    /** Whether the inventory key is pressed. */
    private static boolean inventoryPressed = false;
    /** Whether the quit key is pressed. */
    private static boolean quitPressed = false;
    /** For tracking whether to show coordinates. */
    private static boolean showCoordinates = false;

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
        if (key == KeyEvent.VK_P) { // Show coordinates (for DEBUG)
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
        if (key == KeyEvent.VK_P) { // Show coordinates (for DEBUG)
            showCoordinates = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No implementation needed
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

    public static boolean isShowCoordinates() { // Show coordinates (for DEBUG)
        return showCoordinates;
    }
}
