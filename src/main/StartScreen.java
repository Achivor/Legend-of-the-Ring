package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class StartScreen extends JPanel {
    private BufferedImage playerImage;
    private JButton startButton;
    private JButton settingsButton;
    private Runnable gameStartCallback;
    private boolean showSettings = false;

    public StartScreen() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        try {
            playerImage = ImageIO.read(new File("src/resources/images/player_down_stand.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        startButton = new JButton("Start Game");
        settingsButton = new JButton("Game Setting");

        startButton.setBounds(350, 400, 100, 30);
        settingsButton.setBounds(350, 450, 100, 30);

        add(startButton);
        add(settingsButton);

        startButton.addActionListener(e -> {
            if (gameStartCallback != null) {
                gameStartCallback.run();
            }
        });

        settingsButton.addActionListener(e -> {
            showSettings = true;
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (showSettings) {
                    if (e.getX() >= 350 && e.getX() <= 450 && e.getY() >= 500 && e.getY() <= 530) {
                        showSettings = false;
                        repaint();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (!showSettings) {
            if (playerImage != null) {
                g.drawImage(playerImage, 400 - playerImage.getWidth() / 2, 300 - playerImage.getHeight() / 2, null);
            }
            startButton.setVisible(true);
            settingsButton.setVisible(true);
        } else {
            startButton.setVisible(false);
            settingsButton.setVisible(false);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("sssss", 350, 300);
            g.drawString("exit", 380, 520);
        }
    }

    public void setGameStartCallback(Runnable callback) {
        this.gameStartCallback = callback;
    }
}
