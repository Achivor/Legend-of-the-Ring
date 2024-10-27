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
        settingsButton = new JButton("Instruction");

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

        // 添加游戏标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.BOLD, 40)); // 使用大号字体
        FontMetrics fm = g.getFontMetrics();
        String title = "Legend of the Ring";
        int titleWidth = fm.stringWidth(title);
        g.drawString(title, (getWidth() - titleWidth) / 2, 100); // 居中显示标题

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
            
            // 修改这里以实现文本换行
            String[] instructions = {
                "Press E to interact with NPCs/pick up items",
                "Press W/A/S/D to move",
                "Hold R to check inventory"
            };
            int y = 250;
            for (String line : instructions) {
                g.drawString(line, 250, y);
                y += 30; // 每行之间的间距
            }
            
            g.drawString("exit", 380, 520);
        }
    }

    public void setGameStartCallback(Runnable callback) {
        this.gameStartCallback = callback;
    }
}
