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
    private Item axe; // 隐藏世界的Axe
    private boolean showAxeMessage = false; // 控制Axe物品的消息显示
    private String axeMessage = ""; // Axe物品的消息内容
    private Timer axeMessageTimer; // 控制Axe物品消息的计时器

    private Rectangle specialWall;
    private boolean isSpecialWallActive = true;
    private boolean showSpecialWallMessage = false;
    private String specialWallMessage = "";
    private Timer messageTimer;
    private static final int MESSAGE_DURATION = 2000; // 消息显示时间（毫秒）

    private boolean showKeyMessage = false; // 控制Key物品的消息显示
    private String keyMessage = ""; // Key物品的消息内容
    private Timer keyMessageTimer; // 控制Key物品消息的计时器

    private static final String HIDDEN_WORLD = "hidden_world"; // 新的隐藏世界标识

    private boolean isKeyCollected = false; // 跟踪Key物品是否已被拾取

    private boolean showInventory = false;

    public GamePanel() {
        player = new Player();
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.WHITE);

        // 初始化世界数据
        initWorlds();
        
        // 初始化物品
        items = new ArrayList<>(); // 确保在此处初始化items
        key = new Item(500, 300, "src/resources/images/key.png", "Key", "A mysterious key that might unlock something important.");
        axe = new Item(400, 200, "src/resources/images/axe.png", "Axe", "A sturdy axe that could be useful for cutting things."); // 设置Axe的位置和图片

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

        specialWall = new Rectangle(360, 0, 80, 10); // 特殊空气墙的位置

        messageTimer = new Timer(MESSAGE_DURATION, e -> {
            showSpecialWallMessage = false;
            ((Timer)e.getSource()).stop();
            repaint();
        });
        messageTimer.setRepeats(false);

        keyMessageTimer = new Timer(MESSAGE_DURATION, e -> {
            showKeyMessage = false;
            ((Timer)e.getSource()).stop();
            repaint();
        });
        keyMessageTimer.setRepeats(false);

        axeMessageTimer = new Timer(MESSAGE_DURATION, e -> {
            showAxeMessage = false;
            ((Timer)e.getSource()).stop();
            repaint();
        });
        axeMessageTimer.setRepeats(false);
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
            worldBackgrounds.put(HIDDEN_WORLD, ImageIO.read(new File("src/resources/images/hidden_world.png"))); // 隐藏世界的背景图
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

        ArrayList<Rectangle> wallsWest = new ArrayList<>();
        wallsWest.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsWest.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        wallsWest.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        //wallsWest.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsHidden = new ArrayList<>();
        //wallsHidden.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsHidden.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        //wallsHidden.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsHidden.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        // 将每个世界的空气墙与其对应的世界关联
        worldWalls.put("world_1", walls1);
        worldWalls.put("world_north", wallsNorth);
        worldWalls.put("world_south", wallsSouth);
        worldWalls.put("world_east", wallsEast);
        worldWalls.put("world_west", wallsWest);
        worldWalls.put(HIDDEN_WORLD, wallsHidden); // 添加隐藏世界的空气墙
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
        if ("world_east".equals(world) && !player.hasItem("Key")) {
            items.add(key);
        } else if (HIDDEN_WORLD.equals(world) && !player.hasItem("Axe")) {
            items.add(axe);
        }
    }

    public void update() {
        ArrayList<Rectangle> currentWalls = new ArrayList<>(walls);
        if (currentWorld.equals("world_1") && isSpecialWallActive) {
            currentWalls.add(specialWall);
        }

        if (currentWorld.equals("world_1") && npc != null) {
            player.update(currentWalls, npc.getCollisionBox());
        } else {
            player.update(currentWalls, null);
        }
        checkWorldSwitch();
        checkNPCInteraction();
        checkKeyInteraction();
        checkAxeInteraction();
        checkSpecialWallInteraction();
        checkInventoryDisplay();
        repaint(); // 确保每次更新后重绘面板
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
            else if (currentWorld.equals(HIDDEN_WORLD)){
                loadWorld("world_east");
            }
            player.setPosition(790, player.getY()); // 设置玩家传送到新世界右边
        }
        else if (player.getX() >= 790) { // 到达右边缘
            if (currentWorld.equals("world_east")) {
                loadWorld(HIDDEN_WORLD); // 进入隐藏世界
            } else if (currentWorld.equals("world_1")) {
                loadWorld("world_east"); 
            } else if (currentWorld.equals("world_west")) {
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
        if (currentWorld.equals("world_1") && npc != null) { // 确保npc不为null且在始世界
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
        } else {
            showDialogue = false; // 如果不在初始世界，确保对话框不显示
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

    private void checkSpecialWallInteraction() {
        if (!currentWorld.equals("world_1") || !isSpecialWallActive) {
            return;
        }

        Rectangle expandedWall = new Rectangle(specialWall.x - 20, specialWall.y - 20, 
                                               specialWall.width + 40, specialWall.height + 40);
        
        if (expandedWall.intersects(player.getCollisionBox())) {
            if (KeyInputHandler.isInteractPressed()) {
                if (player.hasItem("Key")) {
                    isSpecialWallActive = false;
                    showMessage("You have opened the passage!");
                } else {
                    showMessage("You need a key!");
                }
                KeyInputHandler.resetInteractPressed();
            } else if (!showSpecialWallMessage) {
                showMessage("Press E to use the key");
            }
        } else {
            showSpecialWallMessage = false;
        }
    }

    private void showMessage(String message) {
        specialWallMessage = message;
        showSpecialWallMessage = true;
        messageTimer.restart();
    }

    private void checkKeyInteraction() {
        if (items.contains(key) && player.getCollisionBox().intersects(key.getCollisionBox())) {
            if (!showKeyMessage) {
                keyMessage = "Press E to pick up the key.";
                showKeyMessage = true;
                keyMessageTimer.restart(); // 重启计时器
            }

            if (KeyInputHandler.isInteractPressed()) {
                player.addItem(key); // 拾取Key物品
                items.remove(key); // 从物品列表中移除Key
                showKeyMessage = false; // 隐藏消息
                KeyInputHandler.resetInteractPressed();
            }
        } else {
            showKeyMessage = false; // 如果不在附近，隐藏消息
        }
    }

    private void checkAxeInteraction() {
        if (items.contains(axe) && player.getCollisionBox().intersects(axe.getCollisionBox())) {
            if (!showAxeMessage) {
                axeMessage = "Press E to pick up the axe.";
                showAxeMessage = true;
                axeMessageTimer.restart();
            }

            if (KeyInputHandler.isInteractPressed()) {
                player.addItem(axe);
                items.remove(axe);
                showAxeMessage = false;
                KeyInputHandler.resetInteractPressed();
            }
        } else {
            showAxeMessage = false;
        }
    }

    private void checkInventoryDisplay() {
        showInventory = KeyInputHandler.isInventoryPressed();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null); // 绘制当前世界背景
        }

        // 绘制物品
        for (Item item : items) {
            item.draw(g);
        }

        // Only draw NPC in world_1
        if (currentWorld.equals("world_1")) {
            npc.draw(g);
        }

        player.draw(g); // 确保主角在物品和NPC之上绘制

        if (showDialogue) {
            drawDialogueBox(g);
        }

        // 绘制特殊墙消息
        if (showSpecialWallMessage) {
            drawSpecialWallMessage(g);
        }

        // 绘制Key物品消息
        if (showKeyMessage) {
            drawKeyMessage(g);
        }

        // 绘制Axe物品消息
        if (showAxeMessage) {
            drawAxeMessage(g);
        }

        if (showInventory) {
            drawInventory(g);
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

    private void drawSpecialWallMessage(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 500, 700, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(specialWallMessage, 70, 530);
    }

    private void drawKeyMessage(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 500, 700, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(keyMessage, 70, 530);
    }

    private void drawAxeMessage(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 500, 700, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(axeMessage, 70, 530);
    }

    private void drawInventory(Graphics g) {
        // 设置背景
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 50, 700, 500);

        // 设置标题
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Inventory", 70, 90);

        // 列出物品
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        ArrayList<Item> inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            
            // 绘制物品贴图
            BufferedImage itemImage = item.getImage();
            int imageSize = 30; // 设置贴图显示大小
            g.drawImage(itemImage, 70, 110 + i * 40, imageSize, imageSize, null);
            
            // 绘制物品名称和描述
            g.drawString(item.getName() + ": " + item.getDescription(), 110, 130 + i * 40);
        }
    }
}
