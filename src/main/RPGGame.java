package main;

import javax.swing.*;

/**
 * The main class for the RPG game, responsible for initializing 
 * the game window and managing game state.
 */
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
            frame.setLocationRelativeTo(null);
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
        
        // Use SwingWorker to run the game loop
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                while (!isCancelled()) {
                    gamePanel.update();
                    gamePanel.repaint();
                    try {
                        Thread.sleep(16); // Approximately 60 FPS
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); 
                        cancel(true); 
                        break;     
                    }
                }
                return null;
            }
        };
        worker.execute();
    }
}
