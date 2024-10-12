package main;

import javax.swing.*;

public class RPGGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("RPG Game");
        GamePanel gamePanel = new GamePanel(
                "src/resources/images/background.jpg",
                "src/resources/images/player.png",
                "src/resources/images/npc.png"
        );
        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        frame.addKeyListener(new KeyInputHandler(gamePanel));
    }
}
