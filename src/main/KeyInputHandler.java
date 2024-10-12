package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import model.Player;

public class KeyInputHandler implements KeyListener {
    private Player player;

    public KeyInputHandler(Player player) {
        this.player = player;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // 设置相应方向的键被按下
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // 停止相应方向的键
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
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 不需要实现
    }
}
