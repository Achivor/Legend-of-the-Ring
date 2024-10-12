package main;

import javax.swing.*;

public class RPGGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("RPG Game");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // 游戏主循环
        while (true) {
            gamePanel.update();
            gamePanel.repaint();
            try {
                Thread.sleep(16); // 大约60FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
