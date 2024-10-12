package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputHandler implements KeyListener {
    private GamePanel gamePanel;

    public KeyInputHandler(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                gamePanel.movePlayer(0, -10);
                break;
            case KeyEvent.VK_DOWN:
                gamePanel.movePlayer(0, 10);
                break;
            case KeyEvent.VK_LEFT:
                gamePanel.movePlayer(-10, 0);
                break;
            case KeyEvent.VK_RIGHT:
                gamePanel.movePlayer(10, 0);
                break;
            case KeyEvent.VK_E: // 拾取物品
                gamePanel.checkInteraction();
                break;
            case KeyEvent.VK_I: // 显示背包
                gamePanel.showInventory();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
