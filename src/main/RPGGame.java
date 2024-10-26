package main;

import javax.swing.*;

public class RPGGame {
    private static JFrame frame;
    private static GamePanel gamePanel;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("RPG Game");
            StartScreen startScreen = new StartScreen();
            gamePanel = new GamePanel();

            frame.add(startScreen);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            startScreen.setGameStartCallback(RPGGame::startGame);
        });
    }

    private static void startGame() {
        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();
        gamePanel.requestFocusInWindow();
        
        // 使用 SwingWorker 来运行游戏循环
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                while (!isCancelled()) {
                    gamePanel.update();
                    gamePanel.repaint();
                    try {
                        Thread.sleep(16); // 约60FPS
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        worker.execute();
    }
}
