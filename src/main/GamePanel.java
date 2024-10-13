package main;

import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private Player player;
    private BufferedImage backgroundImage; // 背景图片
    private ArrayList<Rectangle> walls; // 存储空气墙

    public GamePanel() {
        player = new Player();
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.WHITE);

        // 加载背景图片
        loadBackgroundImage();
        // 初始化墙壁
        initWalls();

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

    private void initWalls() {
        walls = new ArrayList<>();
        // 定义空气墙的位置和大小 (x, y, width, height)
        walls.add(new Rectangle(300, 200, 200, 10)); // 一条横墙
        walls.add(new Rectangle(500, 100, 10, 300)); // 一条竖墙
        walls.add(new Rectangle(100, 400, 300, 10)); // 另一条横墙
        // 你可以继续添加更多的墙
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null); // 绘制背景
        }
        player.draw(g);
        drawWalls(g); // 绘制墙壁
    }

    private void drawWalls(Graphics g) {
        g.setColor(Color.RED); // 设置墙壁颜色
        for (Rectangle wall : walls) {
            g.fillRect(wall.x, wall.y, wall.width, wall.height); // 绘制每个墙壁
        }
    }

    public void update() {
        player.update(walls); // 传递墙壁列表给玩家进行碰撞检测
    }
}
