package main;

import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {
    private Player player;
    private BufferedImage backgroundImage; // 背景图片

    public GamePanel() {
        player = new Player();
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.WHITE);

        // 加载背景图片
        loadBackgroundImage();

        // 添加键盘输入监听器
        KeyInputHandler keyInputHandler = new KeyInputHandler(player);
        this.addKeyListener(keyInputHandler);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("src/resources/images/background.png")); // 背景图片路径
        } catch (IOException e) {
            System.out.println("Error: Could not load background image.");
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null); // 绘制背景
        }
        player.draw(g);
    }

    public void update() {
        player.update();
    }
}
