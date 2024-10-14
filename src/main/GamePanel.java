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
import java.util.HashMap;

public class GamePanel extends JPanel {
    private Player player;
    private HashMap<String, BufferedImage> worldBackgrounds; // 存储多个世界的背景
    private HashMap<String, ArrayList<Rectangle>> worldWalls; // 存储多个世界的空气墙
    private String currentWorld; // 当前所在的世界
    private BufferedImage backgroundImage;
    private ArrayList<Rectangle> walls; // 当前世界的空气墙

    public GamePanel() {
        player = new Player();
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.WHITE);

        // 初始化世界数据
        initWorlds();
        loadWorld("world_1"); // 初始世界加载为 "world_1"

        // 添加键盘输入监听器
        KeyInputHandler keyInputHandler = new KeyInputHandler(player);
        this.addKeyListener(keyInputHandler);
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    // 初始化多个世界和它们的空气墙
    private void initWorlds() {
        worldBackgrounds = new HashMap<>();
        worldWalls = new HashMap<>();

        try {
            worldBackgrounds.put("world_1", ImageIO.read(new File("src/resources/images/background.png")));
            worldBackgrounds.put("world_north", ImageIO.read(new File("src/resources/images/world_north.png")));
            worldBackgrounds.put("world_south", ImageIO.read(new File("src/resources/images/world_south.png")));
            worldBackgrounds.put("world_east", ImageIO.read(new File("src/resources/images/world_east.png")));
            worldBackgrounds.put("world_west", ImageIO.read(new File("src/resources/images/world_west.png")));
        } catch (IOException e) {
            System.out.println("Error: Could not load world backgrounds.");
        }

        // 初始化每个世界的空气墙
        ArrayList<Rectangle> walls1 = new ArrayList<>();
        //walls1.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        walls1.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        walls1.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        walls1.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsNorth = new ArrayList<>();
        // 定义北世界的墙壁...

        ArrayList<Rectangle> wallsSouth = new ArrayList<>();
        // 定义南世界的墙壁...

        ArrayList<Rectangle> wallsEast = new ArrayList<>();
        // 定义东世界的墙壁...

        ArrayList<Rectangle> wallsWest = new ArrayList<>();
        // 定义西世界的墙壁...

        worldWalls.put("world_1", walls1);
        worldWalls.put("world_north", wallsNorth);
        worldWalls.put("world_south", wallsSouth);
        worldWalls.put("world_east", wallsEast);
        worldWalls.put("world_west", wallsWest);
    }

    // 加载指定的世界
    private void loadWorld(String world) {
        currentWorld = world;
        backgroundImage = worldBackgrounds.get(world);
        walls = worldWalls.get(world);
        if (backgroundImage == null || walls == null) {
            System.out.println("Error: Could not load world data.");
        }
    }

    public void update() {
        player.update(walls); // 更新玩家并进行碰撞检测

        // 检测主角是否到达边缘，并切换世界
        checkWorldSwitch();
    }

    private void checkWorldSwitch() {
        // 检查是否到达当前世界边缘
        if (player.getX() <= 0) { // 到达左边缘
            loadWorld("world_west");
            player.setPosition(790, player.getY()); // 设置玩家传送到新世界右边
        } else if (player.getX() >= 790) { // 到达右边缘
            loadWorld("world_east");
            player.setPosition(10, player.getY()); // 设置玩家传送到新世界左边
        } else if (player.getY() <= 0) { // 到达上边缘
            loadWorld("world_north");
            player.setPosition(player.getX(), 590); // 设置玩家传送到新世界底部
        } else if (player.getY() >= 590) { // 到达下边缘
            loadWorld("world_south");
            player.setPosition(player.getX(), 10); // 设置玩家传送到新世界顶部
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null); // 绘制当前世界背景
        }
        player.draw(g);
    }
}
