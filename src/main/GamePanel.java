package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.Item;
import model.NPC;
import model.Player; // 确保导入Item类

public class GamePanel extends JPanel {
    private Player player;
    private HashMap<String, BufferedImage> worldBackgrounds; // 存储多个世界的背景
    private HashMap<String, ArrayList<Rectangle>> worldWalls; // 存储多个世界的空气墙
    private String currentWorld; // 当前所在的世界
    private BufferedImage backgroundImage;
    private ArrayList<Rectangle> walls; // 当前世界的空气墙

    // 用于追踪玩家可以返回的世界
    private String previousWorld;

    private NPC npc;
    private boolean showDialogue;
    private String currentDialogue;
    private long lastInteractionTime;
    private static final long INTERACTION_COOLDOWN = 500; // 500 milliseconds cooldown

    private ArrayList<Item> items; // 存储当前世界的物品
    private Item key; // 东部世界的Key

    public GamePanel() {
        player = new Player();
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.WHITE);

        // 初始化世界数据
        initWorlds();
        
        // 初始化物品
        items = new ArrayList<>(); // 确保在此处初始化items
        key = new Item(500, 300, "src/resources/images/key.png", "Key");

        loadWorld("world_1"); // 初始世界加载为 "world_1"

        // 添加键盘输入监听器
        KeyInputHandler keyInputHandler = new KeyInputHandler(player);
        this.addKeyListener(keyInputHandler);
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Initialize NPC with the new dialogue
        String[] npcDialogue = {
            "My lord, you finally woke up!",
            "May we start on our journey?"
        };
        npc = new NPC(325, 210, "src/resources/images/npc.png", npcDialogue);
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
        walls1.add(new Rectangle(0, 0, 360, 260));
        walls1.add(new Rectangle(440, 0, 360, 260));
        walls1.add(new Rectangle(0, 340, 360, 260));
        walls1.add(new Rectangle(440, 340, 360, 260));

        ArrayList<Rectangle> wallsNorth = new ArrayList<>();
        wallsNorth.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        //wallsNorth.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        wallsNorth.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsNorth.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsSouth = new ArrayList<>();
        //wallsSouth.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsSouth.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        wallsSouth.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsSouth.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsEast = new ArrayList<>();
        wallsEast.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsEast.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        //wallsEast.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsEast.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsWest = new ArrayList<>();
        wallsWest.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsWest.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        wallsWest.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        //wallsWest.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        // 将每个世界的空气墙与其对应的世界关联
        worldWalls.put("world_1", walls1);
        worldWalls.put("world_north", wallsNorth);
        worldWalls.put("world_south", wallsSouth);
        worldWalls.put("world_east", wallsEast);
        worldWalls.put("world_west", wallsWest);
    }

    // 加载指定的世界
    private void loadWorld(String world) {
        previousWorld = currentWorld; // 记录上一个世界
        currentWorld = world;
        backgroundImage = worldBackgrounds.get(world);
        walls = worldWalls.get(world);
        if (backgroundImage == null || walls == null) {
            System.out.println("Error: Could not load world data.");
        }

        // 根据世界加载物品
        items.clear();
        if ("world_east".equals(world)) {
            items.add(key);
        }
    }

    public void update() {
        player.update(walls, npc != null ? npc.getCollisionBox() : null);
        checkWorldSwitch();
        checkNPCInteraction();
        checkItemPickup();
    }

    private void checkWorldSwitch() {
        // 检查是否到达当前世界边缘
        if (player.getX() <= 0) { // 到达左边缘
            if (currentWorld.equals("world_1")){
                loadWorld("world_west");
            }
            else if (currentWorld.equals("world_east")){
                loadWorld("world_1");
            }
            player.setPosition(790, player.getY()); // 设置玩家传送到新世界右边
        }
        else if (player.getX() >= 790) { // 到达右边缘
            if (currentWorld.equals("world_1")){
                loadWorld("world_east");
            }
            else if (currentWorld.equals("world_west")){
                loadWorld("world_1");
            }
            player.setPosition(10, player.getY()); // 设置玩家传送到新世界左边
        }
        else if (player.getY() <= 0) { // 到达上边缘
            if (currentWorld.equals("world_1")) {
                loadWorld("world_north");
            }
            else if (currentWorld.equals("world_south")){
                loadWorld("world_1");
            }
            player.setPosition(player.getX(), 590); // 设置玩家传送到新世界底部
        }
        else if (player.getY() >= 590) { // 到达下边缘
            if (currentWorld.equals("world_north")) {
                loadWorld("world_1");
                player.setPosition(player.getX(), 10); // 返回到原始世界顶部
            }
            else if (currentWorld.equals("world_1")) {
                loadWorld("world_south");
                player.setPosition(player.getX(), 10); // 设置玩家传送到新世界顶部
            }
        }
    }

    private void checkNPCInteraction() {
        if (!currentWorld.equals("world_1")) {
            return; // Skip NPC interaction if not in world_1
        }

        long currentTime = System.currentTimeMillis();
        if (npc.isPlayerNear(player) && KeyInputHandler.isInteractPressed() && 
            (currentTime - lastInteractionTime > INTERACTION_COOLDOWN)) {
            showDialogue = true;
            currentDialogue = npc.getNextDialogue();
            lastInteractionTime = currentTime;
            KeyInputHandler.resetInteractPressed(); // Reset the interact flag
        } else if (!npc.isPlayerNear(player)) {
            showDialogue = false;
        }
    }

    private void checkItemPickup() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (player.getCollisionBox().intersects(item.getCollisionBox()) && KeyInputHandler.isInteractPressed()) {
                player.addItem(item);
                items.remove(i);
                KeyInputHandler.resetInteractPressed();
                break;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null); // 绘制当前世界背景
        }

        player.draw(g);
        
        // Only draw NPC in world_1
        if (currentWorld.equals("world_1")) {
            npc.draw(g);
        }

        if (showDialogue) {
            drawDialogueBox(g);
        }

        // 绘制物品
        for (Item item : items) {
            item.draw(g);
        }
    }

    private void drawDialogueBox(Graphics g) {
        // Draw heading
        g.setColor(new Color(50, 50, 50, 200)); // Dark gray, semi-transparent
        g.fillRect(50, 370, 700, 30); // Heading panel
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Suspicious man", 60, 390);

        // Draw dialogue box
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 400, 700, 150); // Main dialogue box
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(currentDialogue, 70, 440);
    }
}
