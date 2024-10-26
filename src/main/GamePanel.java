package main;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*; // 确保导入Item类
import model.Item;
import model.NPC;
import model.Player;

public class GamePanel extends JPanel implements MouseListener {
    private Player player;
    private HashMap<String, BufferedImage> worldBackgrounds; // 存储多个世界的背景
    private HashMap<String, ArrayList<Rectangle>> worldWalls; // 存储多个世界的空气墙
    private String currentWorld; // 当前所在的世界
    private BufferedImage backgroundImage;
    private ArrayList<Rectangle> walls; // 当前世界的空气墙

    // 用于追踪玩家可以返回的世界
    private String previousWorld;

    private NPC npc;
    private List<NPC> northElves;
    private boolean showDialogue = false;
    private String[] currentDialogue;
    private int currentDialogueIndex = 0;
    private NPC currentInteractingNPC;
    private boolean showInventory = false;

    private long lastInteractionTime;
    private static final long INTERACTION_COOLDOWN = 500; // 500 milliseconds cooldown

    private ArrayList<Item> items; // 存储当前世界的物品
    private Item key; // 东部世界的Key
    private Item axe; // 隐藏世界的Axe
    private Item ring; // 新增的Ring物品
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

    private static final double ELF_SCALE = 0.8; // 精灵贴图缩放比例，可以根需要调整

    private NPC signalNPC; // 新增的指示牌NPC
    private boolean showSignalDialogue = false;

    private NPC closedBox;
    private NPC openedBox;
    private boolean isBoxOpened = false;
    private Item shabbyRing;
    private boolean showBoxMessage = false;
    private String boxMessage = "";
    private Timer boxMessageTimer;

    private NPC statue;
    private boolean showStatueDialogue = false;
    private String[] statueOptions = {"Scarlet", "Amber", "Sapphire", "Emerald"};
    private Rectangle[] optionRectangles;

    private Item elvenFeather;
    private boolean showStatueMessage = false;
    private String statueMessage = "";
    private Timer statueMessageTimer;

    private boolean isStatueInteractable = true;
    private boolean hasCollectedElvenFeather = false;

    private Rectangle specialBottomWall;
    private boolean isSpecialBottomWallActive = true;
    private String specialBottomWallMessage = "";
    private boolean showSpecialBottomWallMessage = false;
    private Timer specialBottomWallMessageTimer;

    private static final String FINAL_WORLD = "final_world";

    private NPC jean;
    private boolean showJeanQuestion = false;
    private Item goldenKey;
    private String[] jeanOptions = {"1", "infinity", "1/2", "0", "Use fire"};
    private Rectangle[] jeanOptionRectangles;
    private boolean jeanDialogueCompleted = false;
    private boolean jeanBurned = false;

    private NPC teleport;
    private boolean showTeleportMessage = false;
    private String teleportMessage = "";

    private int kill_count = 0; // 新增的 kill_count 变量

    private NPC vivian0;
    private NPC soldier;
    private NPC vivian;
    private NPC hank;
    private boolean showFinalDialogue = false;
    private String finalDialogueContent = "";
    private ArrayList<String[]> currentDialogues;

    private boolean isFinalWorldDialogue = false;

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
        ring = new Item(0, 0, "src/resources/images/ring.png", "Ring", "It is definitely not a gorgeous ring. However, it has a familiar vibe that seems to tempt you to put it on."); // 创建Ring物品

        // 将Ring物品添加到玩家的背包中
        player.addItem(ring);

        loadWorld("world_1"); // 初始世界加载为 "world_1"

        // 添加键盘输入监听器
        KeyInputHandler keyInputHandler = new KeyInputHandler(player);
        this.addKeyListener(keyInputHandler);
        this.setFocusable(true);
        this.requestFocusInWindow();

        // Initialize NPC with the new dialogue
        ArrayList<String[]> npcDialogues = new ArrayList<>();
        npcDialogues.add(new String[]{"Suspicious man", "My lord, you finally woke up! May we start on our journey?"});
        npcDialogues.add(new String[]{"Narration", "The man was tied tightly by ropes, deep red marks reflecting on his skin. The person who did this must be hating him so much."});
        npcDialogues.add(new String[]{"You", "(Shakes your head) I do not know you."});
        npcDialogues.add(new String[]{"Suspicious man", "(Stunned) You lost your memory? How is that possible? Check your pocket and put on that ring right now!"});
        npcDialogues.add(new String[]{"Narration", "You feel uncomfortable about the attitude of the man in front of you, is he your friend?"});
        npcDialogues.add(new String[]{"You", "I would not follow your command."});
        npcDialogues.add(new String[]{"Suspicious man", "(laughs viciously) Haha, you are still our honorable KING THORIAN!"});
        npcDialogues.add(new String[]{"Suspicious man", "Fair, I am not in the position to tell you what to do. By the way, my name is Veldric, Veldric Shawdowbane. That is a fake name of course, you just need to know that I am your loyal magician!"});
        npcDialogues.add(new String[]{"You", "......"});
        npcDialogues.add(new String[]{"Veldric", "Now that you have lost your memory, I will explain a bit. \nYou were the hero of humankind, (Snorts) the most respected man in human history, as you defeated the most sinister demon in the world. After the battle, you fell in love with an elvish princess whose beauty is as radiant as the most precious pearl — I'm getting jealous (says contemptuously)."});
        npcDialogues.add(new String[]{"Veldric", "However, she disappeared the day before your wedding, with your opponent. You chased them and went on to fight."});
        npcDialogues.add(new String[]{"Narration", "You really dislike him now. You decide to leave immediately after he reveals all the useful information."});
        npcDialogues.add(new String[]{"You", "Did i lose?"});
        npcDialogues.add(new String[]{"Veldric", "(Smirks) Of course not. With that ring you would never encounter failure and Vivian loves you, she would never make you hurt."});
        npcDialogues.add(new String[]{"Narration", "The familiar name invokes a sense of nostalgia and warmth in your heart. Even now, you still remember her lovely voice and noble heart."});
        npcDialogues.add(new String[]{"You", "You should not mention her name like that."});
        npcDialogues.add(new String[]{"Veldric", "This is me, you highness. I am even more surprised now, you remember her even when you forget your name. Then it should be easy for me to explain what we are supposed to do. Your opponent trapped you here, a special place sheltered with magic. From my observation, there are three puzzles in this area. I am not very sure where it would lead us to after solving the puzzles, but unfortunately, this is our only clue. Shall we start now?"});
        npcDialogues.add(new String[]{"Narration", "You nod, then walk away."});
        npcDialogues.add(new String[]{"Veldric", "Can you not see I am also trapped here?! Come back and release me!"});

        // 初始化NPC，使用默认构造函数（不缩放）
        npc = new NPC(325, 210, "src/resources/images/npc.png", npcDialogues);

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

        // 初始化北部世界的精灵
        initNorthElves();

        // 初始化指示牌NPC
        initSignalNPC();

        initTreasureBox();

        boxMessageTimer = new Timer(3000, e -> {
            showBoxMessage = false;
            ((Timer)e.getSource()).stop();
            repaint();
        });
        boxMessageTimer.setRepeats(false);

        initStatue();
        initElvenFeather();
        addMouseListener(this);
        
        statueMessageTimer = new Timer(3000, e -> {
            showStatueMessage = false;
            isStatueInteractable = true; // 重置雕像可交互状态
            statueMessageTimer.stop();
            repaint();
        });

        initSpecialBottomWall();
        
        specialBottomWallMessageTimer = new Timer(3000, e -> {
            showSpecialBottomWallMessage = false;
            ((Timer)e.getSource()).stop();
            repaint();
        });
        specialBottomWallMessageTimer.setRepeats(false);

        initJean();
    }

    // 初始化多��世界和它们的空气墙
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
            worldBackgrounds.put(FINAL_WORLD, ImageIO.read(new File("src/resources/images/final_world.png")));
        } catch (IOException e) {
            System.out.println("Error: Could not load world backgrounds.");
        }

        // 初始化每个世界的空气墙
        ArrayList<Rectangle> walls1 = new ArrayList<>();
        walls1.add(new Rectangle(0, 0, 360, 240));
        walls1.add(new Rectangle(440, 0, 360, 240));
        walls1.add(new Rectangle(0, 340, 360, 260));
        walls1.add(new Rectangle(440, 340, 360, 260));

        ArrayList<Rectangle> wallsNorth = new ArrayList<>();
        wallsNorth.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsNorth.add(new Rectangle(0, 590, 360, 10)); // 底部墙1
        wallsNorth.add(new Rectangle(440, 590, 360, 10)); // 底部墙1
        wallsNorth.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsNorth.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsSouth = new ArrayList<>();
        //wallsSouth.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsSouth.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        wallsSouth.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsSouth.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsEast = new ArrayList<>();
        wallsEast.add(new Rectangle(0, 0, 10, 240)); // left wall1
        wallsEast.add(new Rectangle(0, 340, 10, 260)); //left wall2
        wallsEast.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsEast.add(new Rectangle(0, 590, 800, 10)); // 底部墙

        ArrayList<Rectangle> wallsWest = new ArrayList<>();
        //wallsWest.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        //wallsEast.add(new Rectangle(790, 0, 10, 240));
        //wallsEast.add(new Rectangle(790, 340, 10, 260));
        wallsWest.add(new Rectangle(0, 590, 800, 10)); // 底墙
        wallsWest.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        //wallsWest.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsHidden = new ArrayList<>();
        wallsHidden.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        wallsHidden.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        //wallsHidden.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsHidden.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        ArrayList<Rectangle> wallsFinal = new ArrayList<>();
        wallsFinal.add(new Rectangle(0, 0, 800, 10)); // 顶部墙
        //wallsFinal.add(new Rectangle(0, 590, 800, 10)); // 底部墙
        wallsFinal.add(new Rectangle(0, 0, 10, 600)); // 左侧墙
        wallsFinal.add(new Rectangle(790, 0, 10, 600)); // 右侧墙

        // 将每个世界的空气墙与其对应的世界关联
        worldWalls.put("world_1", walls1);
        worldWalls.put("world_north", wallsNorth);
        worldWalls.put("world_south", wallsSouth);
        worldWalls.put("world_east", wallsEast);
        worldWalls.put("world_west", wallsWest);
        worldWalls.put(HIDDEN_WORLD, wallsHidden); // 添加隐藏世界的空气墙
        worldWalls.put(FINAL_WORLD, wallsFinal);

        initTeleport();
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

        // 重置玩家位置（如果需要）
        if (world.equals(FINAL_WORLD)) {
            player.setPosition(400, 550); // 设置玩家在最终世界的初始位置
            initFinalWorldNPCs(); // 初始化最终世界的 NPC
        }
    }

    public void update() {
        ArrayList<Rectangle> currentWalls = new ArrayList<>(walls);
        if (currentWorld.equals("world_1") && isSpecialWallActive) {
            currentWalls.add(specialWall);
        }
        if (currentWorld.equals("world_1") && isSpecialBottomWallActive) {
            currentWalls.add(specialBottomWall);
        }

        if (currentWorld.equals("world_1") && npc != null) {
            player.update(currentWalls, npc.getCollisionBox());
        } else {
            player.update(currentWalls, null);
        }
        checkWorldSwitch();
        if (!isFinalWorldDialogue) {
            checkNPCInteraction();
        }
        checkKeyInteraction();
        checkAxeInteraction();
        checkSpecialWallInteraction();
        checkInventoryDisplay();
        checkBoxInteraction();
        checkStatueInteraction();
        checkSpecialBottomWallInteraction();
        checkTeleportInteraction();
        checkFinalWorldInteractions();
        repaint(); // 确保每更新后重绘面板

        if (showFinalDialogue && KeyInputHandler.isQuitPressed()) {
            System.exit(0);
        }
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
            /*else if (currentWorld.equals("world_west")){
                loadWorld(FINAL_WORLD);
            }*/
            player.setPosition(player.getX(), 590); // 设置玩家传送到新世界底部
        }
        else if (player.getY() >= 590) { // 到达下边缘
            if (currentWorld.equals("world_north")) {
                loadWorld("world_1");
            }
            else if (currentWorld.equals("world_1")) {
                loadWorld("world_south");
            }
            /*else if (currentWorld.equals(FINAL_WORLD)){
                loadWorld("world_west");
            }*/
            player.setPosition(player.getX(), 10); // 设置玩家传送到新世界顶部
        }
    }

    private void checkNPCInteraction() {
        long currentTime = System.currentTimeMillis();
        if (currentWorld.equals("world_1") && npc != null) {
            checkSingleNPCInteraction(npc, currentTime);
        } else if (currentWorld.equals("world_north")) {
            for (NPC elf : northElves) {
                if (checkSingleNPCInteraction(elf, currentTime)) {
                    break;
                }
            }
            // 检查指示牌NPC的交互，使用新的方法
            checkSignalInteraction();
        } else if (currentWorld.equals("world_south") && jean.isPlayerNear(player)) {
            if (KeyInputHandler.isInteractPressed()) {
                if (jeanDialogueCompleted) {
                    showDialogue = true;
                    currentDialogue = new String[]{"Jean", jeanBurned ? "......" : "Vivian is waiting for you."};
                } else if (!showDialogue || currentInteractingNPC != jean) {
                    showDialogue = true;
                    currentInteractingNPC = jean;
                    currentDialogueIndex = 0;
                    currentDialogue = jean.getNextDialogue(currentDialogueIndex);
                } else {
                    currentDialogueIndex++;
                    if (currentDialogueIndex < jean.getDialogueCount()) {
                        currentDialogue = jean.getNextDialogue(currentDialogueIndex);
                    } else if (currentDialogueIndex == jean.getDialogueCount()) {
                        showJeanQuestion = true;
                        showDialogue = false;
                    }
                }
                KeyInputHandler.resetInteractPressed();
            }
        } else {
            showDialogue = false;
            showSignalDialogue = false;
        }
    }

    private boolean checkSingleNPCInteraction(NPC npc, long currentTime) {
        if (npc.isPlayerNear(player)) {
            if (KeyInputHandler.isInteractPressed() && 
                (currentTime - lastInteractionTime > INTERACTION_COOLDOWN)) {
                if (!showDialogue || currentInteractingNPC != npc) {
                    // 开始新的对话
                    currentDialogueIndex = 0;
                    currentDialogue = npc.getNextDialogue(currentDialogueIndex);
                    showDialogue = true;
                    currentInteractingNPC = npc;
                } else {
                    // 继续下一段对话
                    currentDialogueIndex++;
                    currentDialogue = npc.getNextDialogue(currentDialogueIndex);
                    if (currentDialogue == null) {
                        showDialogue = false;
                        currentInteractingNPC = null;
                    }
                }
                lastInteractionTime = currentTime;
                KeyInputHandler.resetInteractPressed();
                return true;
            }
        } else if (currentInteractingNPC == npc) {
            showDialogue = false;
            currentInteractingNPC = null;
        }
        return false;
    }

    private void checkSignalInteraction() {
        if (signalNPC.isPlayerNear(player)) {
            showSignalDialogue = true;
            currentDialogue = signalNPC.getNextDialogue(0);
        } else {
            showSignalDialogue = false;
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
                    showMessage("You are locked out!");
                }
                KeyInputHandler.resetInteractPressed();
            } else if (!showSpecialWallMessage) {
                showMessage("There is a heavy door in front of you, press E to try to open it.");
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

    private void checkBoxInteraction() {
        if (!currentWorld.equals("world_north")) {
            return;
        }

        NPC currentBox = isBoxOpened ? openedBox : closedBox;
        if (currentBox.isPlayerNear(player)) {
            if (KeyInputHandler.isInteractPressed()) {
                if (!isBoxOpened) {
                    if (!showBoxMessage) {
                        showBoxMessage("You found a treasure! Press E to open it.");
                    } else {
                        isBoxOpened = true;
                        showBoxMessage("You opened the treasure box, and something suddenly flow out and attached to you. (check your inventory)");
                        player.addItem(shabbyRing);
                        boxMessageTimer.start();
                    }
                }
                KeyInputHandler.resetInteractPressed();
            }
        } else {
            showBoxMessage = false;
        }
    }

    private void showBoxMessage(String message) {
        boxMessage = message;
        showBoxMessage = true;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        // 绘制物品
        for (Item item : items) {
            item.draw(g);
        }

        // 绘制所有空气墙
        g.setColor(Color.RED);
        for (Rectangle wall : walls) {
            g.fillRect(wall.x, wall.y, wall.width, wall.height);
        }

        // 调用 drawNPCs 方法
        drawNPCs(g);

        player.draw(g);

        // 绘制对话框
        if (showDialogue) {
            drawDialogueBox(g);
        }
        if (showSignalDialogue) {
            drawSignalDialogueBox(g);
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

        if (showBoxMessage) {
            drawBoxMessage(g);
        }

        if (showStatueDialogue) {
            drawStatueDialogue(g);
        }

        if (showStatueMessage) {
            drawStatueMessage(g);
        }

        if (showSpecialBottomWallMessage) {
            drawSpecialBottomWallMessage(g);
        }

        if (showJeanQuestion) {
            drawJeanQuestion(g);
        }

        if (showTeleportMessage) {
            drawTeleportMessage(g);
        }

        if (currentWorld.equals(FINAL_WORLD)) {
            if (kill_count == 0 && vivian0 != null) {
                vivian0.draw(g);
            } else if (kill_count == 1 && soldier != null) {
                soldier.draw(g);
            } else if (kill_count == 2) {
                if (vivian != null) {
                    vivian.draw(g);
                }
                if (hank != null) {
                    hank.draw(g);
                }
            }
        }

        if (showFinalDialogue) {
            drawFinalDialogue(g);
        }
    }

    private void drawDialogueBox(Graphics g) {
        if (currentDialogue == null) {
            return;
        }

        // Draw heading
        g.setColor(new Color(50, 50, 50, 200));
        g.fillRect(50, 370, 700, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(currentDialogue[0], 60, 390); // Draw title

        // Draw dialogue box
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 400, 700, 150);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawWrappedText(g, currentDialogue[1], 70, 430, 660, 20); // Draw content
    }

    private void drawSignalDialogueBox(Graphics g) {
        if (currentDialogue == null) {
            return;
        }

        // Draw heading
        g.setColor(new Color(50, 50, 50, 200));
        g.fillRect(50, 370, 700, 30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(currentDialogue[0], 60, 390); // Draw title

        // Draw dialogue box
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 400, 700, 150);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawWrappedText(g, currentDialogue[1], 70, 430, 660, 20); // Draw content
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
        g.setFont(new Font("Arial", Font.PLAIN, 16)); // 设置统一的字体
        ArrayList<Item> inventory = player.getInventory();
        int yOffset = 110; // 初始Y偏移量

        for (Item item : inventory) {
            // 绘制物品贴图
            BufferedImage itemImage = item.getImage();
            int imageSize = 30; // 设置贴图显示大小
            g.drawImage(itemImage, 70, yOffset, imageSize, imageSize, null);

            // 绘制物品名称（加上冒号）
            g.setColor(Color.WHITE);
            g.drawString(item.getName() + ":", 110, yOffset + 20);

            // 绘制物品描述
            String description = item.getDescription();
            int descriptionHeight = drawWrappedText(g, description, 110, yOffset + 40, 620, 20);

            yOffset += descriptionHeight + 40; // 更新Y偏移量，增加物品之间的间距
        }
    }

    // 修改 drawWrappedText 方法，返回绘制的文本高度
    private int drawWrappedText(Graphics g, String text, int x, int y, int width, int lineHeight) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int linesDrawn = 0;
        for (String word : words) {
            if (fm.stringWidth(line + word) < width) {
                line.append(word).append(" ");
            } else {
                g.drawString(line.toString(), x, y);
                y += lineHeight;
                linesDrawn++;
                line = new StringBuilder(word).append(" ");
            }
        }
        g.drawString(line.toString(), x, y);
        linesDrawn++;
        return linesDrawn * lineHeight;
    }

    private void drawBoxMessage(Graphics g) {
        if (!showBoxMessage) {
            return;
        }

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 450, 700, 100); // 增加对话框的高度
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));

        // 使用 drawWrappedText 方法来绘制换行的文本
        drawWrappedText(g, boxMessage, 70, 480, 660, 20);
    }

    private void initNorthElves() {
        northElves = new ArrayList<>();

        // 创建四个精灵NPC，每个都有独特的对话内容，并设置缩放比例
        NPC elf1 = createElf("Scarlet", 100, 100, "src/resources/images/Scarlet.png", "Hello, human! Help me! I am so angry at Emerald, she's such a liar. How can she charge me for stealing the key? To be honest, she is the thief! Amber did not steal by the way, look at her dumb face (rolls eyes), she is not smart enough to do that.", ELF_SCALE);
        NPC elf2 = createElf("Sapphire", 300, 200, "src/resources/images/Sapphire.png", "(coldly) Filthy human, back away from me! Ididnt steal.", ELF_SCALE);
        NPC elf3 = createElf("Emerald", 500, 300, "src/resources/images/Emerald.png", "Scarlet is the thief!!!!! She stole my cake (cries)...and whatever you seek.", ELF_SCALE);
        NPC elf4 = createElf("Amber", 700, 400, "src/resources/images/Amber.png", "(says nervously)…Sorryyyy, I have not been talking to guys for some time… I saw Sapphire taking the key away, she hates humans (sighs). Please do not hurt her, I know you are the human king, you must be a kind man, right?", ELF_SCALE);

        northElves.add(elf1);
        northElves.add(elf2);
        northElves.add(elf3);
        northElves.add(elf4);
    }

    private NPC createElf(String name, int x, int y, String imagePath, String dialogueContent, double scale) {
        ArrayList<String[]> elfDialogues = new ArrayList<>();
        elfDialogues.add(new String[]{name, dialogueContent});
        return new NPC(x, y, imagePath, elfDialogues, scale);
    }

    private void initSignalNPC() {
        ArrayList<String[]> signalDialogues = new ArrayList<>();
        signalDialogues.add(new String[]{"Signal", "Sprites are lovely creatures who always lie. It is observed that when four of them come together, there will only be one telling the truth."});
        signalNPC = new NPC(350, 500, "src/resources/images/signal.png", signalDialogues, 1.5); // 使用0.5作为缩放比例，您可以根据需要调整
    }

    private void initTreasureBox() {
        ArrayList<String[]> closedBoxDialogues = new ArrayList<>();
        closedBoxDialogues.add(new String[]{"Treasure Box", "You found a treasure! Press E to open it."});
        closedBox = new NPC(500, 400, "src/resources/images/closed_box.png", closedBoxDialogues, 1);

        ArrayList<String[]> openedBoxDialogues = new ArrayList<>();
        openedBoxDialogues.add(new String[]{"Treasure Box", "You opened the treasure box, and something suddenly flow out and attached to you. (check your inventory)"});
        openedBox = new NPC(500, 400, "src/resources/images/opened_box.png", openedBoxDialogues, 1);

        shabbyRing = new Item(0, 0, "src/resources/images/shabby_ring.png", "Shabby ring of fire",
                "This ring is the relic of the ancient fire god Vulcan after his death in the fateful battle, which could wield terrifying destructive power if used against a living creature.");
    }

    private void initStatue() {
        statue = new NPC(400, 300, "src/resources/images/statue.png", new ArrayList<>(), 1.0);
    }

    private void checkStatueInteraction() {
        if (currentWorld.equals("world_north") && statue.isPlayerNear(player)) {
            if (KeyInputHandler.isInteractPressed() && isStatueInteractable && !hasCollectedElvenFeather) {
                showStatueDialogue = true;
                KeyInputHandler.resetInteractPressed();
            }
        } else {
            showStatueDialogue = false;
        }
    }

    private void drawStatueDialogue(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(200, 100, 400, 400);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Who stole the Elven Feather?", 220, 140);
        g.drawString("Choose:", 220, 170);

        optionRectangles = new Rectangle[statueOptions.length];
        for (int i = 0; i < statueOptions.length; i++) {
            int y = 200 + i * 40;
            g.drawString(statueOptions[i], 220, y);
            optionRectangles[i] = new Rectangle(220, y - 20, 360, 30);
        }

        if (player.hasItem("Axe")) {
            g.drawString("Use axe", 220, 360);
            optionRectangles = Arrays.copyOf(optionRectangles, optionRectangles.length + 1);
            optionRectangles[optionRectangles.length - 1] = new Rectangle(220, 340, 360, 30);
        }
    }

    private void initElvenFeather() {
        elvenFeather = new Item(0, 0, "src/resources/images/elven_feather.png", "Elven Feather", "A part of elve's body");
    }

    private void handleStatueOption(int optionIndex) {
        String selectedOption = optionIndex < statueOptions.length ? statueOptions[optionIndex] : "Use axe";
        switch (selectedOption) {
            case "Scarlet":
            case "Amber":
            case "Emerald":
                showStatueMessage("Nothing happened...");
                isStatueInteractable = false;
                break;
            case "Sapphire":
                showStatueMessage("The statue emitted a blinding glow, and an Elven Feather fell from the center of its chest. You picked it up.");
                player.addItem(elvenFeather);
                hasCollectedElvenFeather = true;
                break;
            case "Use axe":
                showStatueMessage("You brutally hacked off the statue's wings with an axe and took the Elven Feather.");
                player.addItem(elvenFeather);
                hasCollectedElvenFeather = true;
                kill_count++; // 增加 kill_count
                break;
            default:
                break;
        }
        showStatueDialogue = false;
    }

    private void showStatueMessage(String message) {
        statueMessage = message;
        showStatueMessage = true;
        statueMessageTimer.restart();
    }

    private void drawStatueMessage(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 450, 700, 100);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawWrappedText(g, statueMessage, 70, 480, 660, 20);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (showStatueDialogue) {
            for (int i = 0; i < optionRectangles.length; i++) {
                if (optionRectangles[i].contains(e.getPoint())) {
                    handleStatueOption(i);
                    break;
                }
            }
        }
        if (showJeanQuestion) {
            for (int i = 0; i < jeanOptionRectangles.length; i++) {
                if (jeanOptionRectangles[i] != null && jeanOptionRectangles[i].contains(e.getPoint())) {
                    handleJeanAnswer(jeanOptions[i]);
                    break;
                }
            }
        }
    }

    // 实现其他 MouseListener 方法（保持为空）
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void initSpecialBottomWall() {
        specialBottomWall = new Rectangle(0, 590, 800, 10);
    }

    private void checkSpecialBottomWallInteraction() {
        if (!currentWorld.equals("world_1") || !isSpecialBottomWallActive) {
            return;
        }

        Rectangle expandedWall = new Rectangle(specialBottomWall.x, specialBottomWall.y - 20,
                specialBottomWall.width, specialBottomWall.height + 20);

        if (expandedWall.intersects(player.getCollisionBox())) {
            if (!showSpecialBottomWallMessage) {
                showSpecialBottomWallMessage("Ahead is the boundary of the elven world! No non-Elven creatures shall enter! (Press E to try to enter)");
            }
            if (KeyInputHandler.isInteractPressed()) {
                if (player.hasItem("Elven Feather")) {
                    isSpecialBottomWallActive = false;
                    showSpecialBottomWallMessage("The Elven Feather you carry slips through the unseen boundary, you successfully step into the elven world!");
                } else {
                    showSpecialBottomWallMessage("Ouch! You fall down to your knees as you are trying to enter. Is this the elven magic Vivian once told you? You definitely need something to obtain permission from the elves.");
                }
                KeyInputHandler.resetInteractPressed();
            }
        } else {
            showSpecialBottomWallMessage = false;
        }
    }

    private void showSpecialBottomWallMessage(String message) {
        specialBottomWallMessage = message;
        showSpecialBottomWallMessage = true;
        specialBottomWallMessageTimer.restart();
    }

    private void drawSpecialBottomWallMessage(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 450, 700, 100);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawWrappedText(g, specialBottomWallMessage, 70, 480, 660, 20);
    }

    private void initJean() {
        ArrayList<String[]> jeanDialogues = new ArrayList<>();
        jeanDialogues.add(new String[]{"Jean", "I would not let you pass…Vivian does not deserve this."});
        jeanDialogues.add(new String[]{"You", "This is not fair! Vivian must be missing me so much, I need to go to her side. Claire, you also carry the blood of an elf, do you betray your own kind?"});
        jeanDialogues.add(new String[]{"Jean", "(closes her eyes) Fine. This is my question, but I could not promise you anything."});
        jean = new NPC(400, 300, "src/resources/images/Jean.png", jeanDialogues, 1.5);

        goldenKey = new Item(0, 0, "src/resources/images/golden_key.png", "Golden key", "The permit to the palace");
    }

    private void drawJeanQuestion(Graphics g) {
        if (!showJeanQuestion) {
            return;
        }

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 50, 700, 500);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("(Jean shows on a piece of paper)", 70, 90);

        // 绘制 question.png 图片
        try {
            BufferedImage questionImage = ImageIO.read(new File("src/resources/images/question.png"));
            g.drawImage(questionImage, 70, 120, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        g.drawString("Choose:", 70, 400);

        jeanOptionRectangles = new Rectangle[jeanOptions.length];
        for (int i = 0; i < jeanOptions.length; i++) {
            if (i == 4 && !player.hasItem("Shabby ring of fire")) {
                continue;
            }
            g.drawString(jeanOptions[i], 70, 430 + i * 30);
            jeanOptionRectangles[i] = new Rectangle(70, 410 + i * 30, 150, 30);
        }
    }

    private void handleJeanAnswer(String answer) {
        showJeanQuestion = false;
        showDialogue = true;
        switch (answer) {
            case "0":
                currentDialogue = new String[]{"Jean", "(plainly) You win. No wonder you are the King Thorian. I shall follow my words and pass the golden key to you. Please, do not hurt Vivian anymore."};
                player.addItem(goldenKey);
                jeanDialogueCompleted = true;
                break;
            case "Use fire":
                currentDialogue = new String[]{"Jean", "I know it...Vivian, run..."};
                player.addItem(goldenKey);
                kill_count++; // 增加 kill_count
                jeanDialogueCompleted = true;
                jeanBurned = true;
                break;
            default:
                currentDialogue = new String[]{"Jean", "No."};
                break;
        }
    }

    private void drawNPCs(Graphics g) {
        if (currentWorld.equals("world_1")) {
            npc.draw(g);
        } else if (currentWorld.equals("world_north")) {
            for (NPC elf : northElves) {
                elf.draw(g);
            }
            signalNPC.draw(g);
            if (isBoxOpened) {
                openedBox.draw(g);
            } else {
                closedBox.draw(g);
            }
            statue.draw(g);
        } else if (currentWorld.equals("world_south")) {
            jean.draw(g);
        } else if (currentWorld.equals("world_west")) {
            teleport.draw(g);
        } else if (currentWorld.equals(FINAL_WORLD)) {
            if (kill_count == 0 && vivian0 != null) {
                vivian0.draw(g);
            } else if (kill_count == 1 && soldier != null) {
                soldier.draw(g);
            } else if (kill_count == 2) {
                if (vivian != null) {
                    vivian.draw(g);
                }
                if (hank != null) {
                    hank.draw(g);
                }
            }
        }
    }

    private void initTeleport() {
        ArrayList<String[]> teleportDialogues = new ArrayList<>();
        teleportDialogues.add(new String[]{"Teleport", "You know this magic, it is a teleport waypoint which will probably lead you to somewhere familiar. Press E to teleport."});
        teleport = new NPC(400, 300, "src/resources/images/teleport.png", teleportDialogues, 0.1);
    }

    private void checkTeleportInteraction() {
        if (currentWorld.equals("world_west") && teleport.isPlayerNear(player)) {
            if (!showTeleportMessage) {
                showTeleportMessage = true;
                teleportMessage = "Press E to enter the palace.";
            }
            if (KeyInputHandler.isInteractPressed()) {
                if (player.hasItem("Golden key")) {
                    loadWorld(FINAL_WORLD);
                    player.setPosition(370, 530); // 设置玩家在最终世界的初始位置
                } else {
                    teleportMessage = "You need a golden key!";
                }
                KeyInputHandler.resetInteractPressed();
            }
        } else {
            showTeleportMessage = false;
        }
    }

    private void drawTeleportMessage(Graphics g) {
        if (!showTeleportMessage) {
            return;
        }

        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 450, 700, 100);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawWrappedText(g, teleportMessage, 70, 480, 660, 20);
    }

    private void initFinalWorldNPCs() {
        if (kill_count == 0) {
            ArrayList<String[]> vivian0Dialogues = new ArrayList<>(Arrays.asList(
                    new String[]{"Narrative", "This palace feels familiar. It was supposed to be the place where your wedding was held."},
                    new String[]{"Vivian", "Thor, you are here."},
                    new String[]{"You", "(hold her hand) Yes, and I will never leave you, my love."},
                    new String[]{"Vivian", "(smiles) You have changed. I would forgive you for whatever you have done as I see glory building up inside you once again."},
                    new String[]{"You", "(kneel down) Vivian, will you marry…"},
                    new String[]{"Narrative", "You search through your pocket, but there is nothing. WHERE THE HELL IS YOUR RING?"},
                    new String[]{"Vivian", "(bursts into laughter) You are so cute. Forget about the ring, honestly, I hope it is never found."},
                    new String[]{"You", "Then what's your answer to my quest?"},
                    new String[]{"Vivian", "Yes, sure, i do."}
            ));
            vivian0 = new NPC(400, 300, "src/resources/images/Vivian0.png", vivian0Dialogues, 1.5);
        } else if (kill_count == 1) {
            ArrayList<String[]> soldierDialogues = new ArrayList<>(Arrays.asList(
                    new String[]{"Narrative", "This palace feels very familiar. You must have reached your final destination."},
                    new String[]{"Soldier", "Vivian is dead, she felt disappointed…"},
                    new String[]{"You", "(Rage swarms your heart) HOW DARE YOU?"},
                    new String[]{"Narrative", "You put on your ring, immediately feeling immense power accumulating inside you. This person must pay for what he has done, and you are never merciful towards betrayers."},
                    new String[]{"Soldier", "My lord, don't—--"},
                    new String[]{"Narrative", "Your enemy died. A sudden emptiness in your heart prevents you from feeling happy for whatever happened. Now that your love is gone, you should go to your people and fulfill your responsibility as the king, but you are just tired."},
                    new String[]{"You", "(kiss your ring) Good bye Vivian."}
            ));
            soldier = new NPC(400, 300, "src/resources/images/soldier.png", soldierDialogues, 1.5);
        } else if (kill_count == 2) {
            ArrayList<String[]> vivianDialogues = new ArrayList<>(Arrays.asList(
                    new String[]{"Narrative", "You enter your palace without hesitance. After all, you have been so relentless so far just to go to Vivian as fast as possible. There is no time to lose."},
                    new String[]{"You", "Vivian, come back to me."},
                    new String[]{"Narrative", "Vivian and the betrayer — Hank look very stunned. They are not expecting you to arrive so early, since you claimed to have lost your memory from the start."},
                    new String[]{"Vivian", "(trembling) You lied…"},
                    new String[]{"Hank", "My king, what turned you into this? Veldric lured you with the Magic Ring, which is why I trapped him. Our hero, our beloved king should have come back…"},
                    new String[]{"You", "Why would you assume I am controlled by the Ring? Claiming the throne, invading the elven land, even—taking her father, the elven king's life, are all my own decisions. I have my ambitions, Hank. I had been expecting you, my loyal guard to understand me and you never!"},
                    new String[]{"Hank", "(draws his sword) We shared the same dream and glory before, but no longer. Thorian, I shall challenge you to a duel!"},
                    new String[]{"Vivian", "(weeps) There is no turning back…I betrayed my own people."},
                    new String[]{"You", "That is why I dislike elvish creatures. You are always kind, docile, and naive! Are you programmed like this? Never meant to kill, hurt or hate. Vivian, it was only until I invaded your land that I realized we are never the same kind. When one does not hate, doesn't it mean there is also no love?"},
                    new String[]{"Vivian", "……"},
                    new String[]{"You", "It is alright, I don't care whether you love me or not. I need a queen, and your elvish ring. Your father passed that to you, right? I did not find it on his body. Come here, Vivian. I know you do not hate me."},
                    new String[]{"Hank", "…Thorian!"},
                    new String[]{"Narrative", "You ignore Hank as you are approaching Vivian. With the magic ring, no man can hurt you."},
                    new String[]{"Vivian", "(murmurs)……no…"},
                    new String[]{"You", "Say that again?"},
                    new String[]{"Vivian", "(loudly) No!"},
                    new String[]{"Narrative", "A sharp, cold pressure arises from your stomach, soon turning into hot pain. Agony radiates from your wound,spreading like wildfire through your body. YOU ARE STABBED."},
                    new String[]{"You", "Vivian…"},
                    new String[]{"Vivian", "(tears in her eyes) Your ring does not protect you from elven weapons. I do hate, Thor."},
                    new String[]{"You", "I am happy to know… Your hatred is real, then your love is also…"},
                    new String[]{"Hank", "Thorian, stop talking. I need to stop the bleeding."},
                    new String[]{"Narrative", "As more blood flows out of your body, your mind starts to wander off. Hank was not only your guard, he was once your friend when you were still nobody. You are not regretful, you never cry over spilt milk. You are just wondering, was Vivian really trying to give you a second chance? What if you make another choice…"}
            ));
            vivian = new NPC(400, 300, "src/resources/images/Vivian.png", vivianDialogues, 1.5);
            hank = new NPC(500, 300, "src/resources/images/Hank.png", new ArrayList<>(), 1.5);
        }
    }

    private void checkFinalWorldInteractions() {
        if (!currentWorld.equals(FINAL_WORLD)) {
            return;
        }

        if (KeyInputHandler.isInteractPressed()) {
            KeyInputHandler.resetInteractPressed();
            if (kill_count == 0 && vivian0 != null && vivian0.isPlayerNear(player)) {
                handleVivian0Dialogue();
            } else if (kill_count == 1 && soldier != null && soldier.isPlayerNear(player)) {
                handleSoldierDialogue();
            } else if (kill_count == 2 && vivian != null && vivian.isPlayerNear(player)) {
                handleVivianDialogue();
            }
        }
    }

    private void handleVivian0Dialogue() {
        if (currentDialogues == null) {
            currentDialogues = vivian0.getDialogues();
            currentDialogueIndex = 0;
            isFinalWorldDialogue = true;
        }

        if (currentDialogueIndex < currentDialogues.size()) {
            String[] dialogue = currentDialogues.get(currentDialogueIndex);
            showDialogue = true;
            currentDialogue = dialogue;
            currentDialogueIndex++;
        } else {
            showFinalDialogue("Good ending: I do\n\n" + 
                                "Congratulations! You have reached the best ending! \n" + 
                                "However, you must be holding onto many doubts about the truth.\n" +
                                "We personally do not recommend going further, as this is the \n"+
                                "only ending where Thorian and Vivian could live happily ever after.\n" +
                                "If you are really curious about what had happened in the past,\n" +
                                "try to explore more in the maze, there is a hidden room where\n" +
                                "you can find an axe.\n" + 
                                "Unfortunately, no one would be happy making that choice.\n" +
                                "When you stare into the abyss, the abyss stares into you.\n\n" +
                                "(Press Q to quit the game)");
            isFinalWorldDialogue = false;
        }
    }

    private void handleSoldierDialogue() {
        if (currentDialogues == null) {
            currentDialogues = soldier.getDialogues();
            currentDialogueIndex = 0;
            isFinalWorldDialogue = true;
        }

        if (currentDialogueIndex < currentDialogues.size()) {
            String[] dialogue = currentDialogues.get(currentDialogueIndex);
            showDialogue = true;
            currentDialogue = dialogue;
            currentDialogueIndex++;
        } else {
            showFinalDialogue("Normal end: Farewell.\n\n" +
                                "Hint: Vivian dies of heartbreaking.\n" + 
                                "The answer to Jean's question is 0. And the one who stole the\n" +
                                "Elven Feather is Sapphire. Try not to cheat next time, especially\n" +
                                "not to kill Jean, she is Vivian's best friend.\n" +
                                "(Jean's model and name is credited by Genshin Impact. It is a \n" +
                                "nice game, highly recommend to play it :) )\n\n" + 
                                "(Press Q to quit the game)");
            isFinalWorldDialogue = false;
        }
    }

    private void handleVivianDialogue() {
        if (currentDialogues == null) {
            currentDialogues = vivian.getDialogues();
            currentDialogueIndex = 0;
            isFinalWorldDialogue = true;
        }

        if (currentDialogueIndex < currentDialogues.size()) {
            String[] dialogue = currentDialogues.get(currentDialogueIndex);
            showDialogue = true;
            currentDialogue = dialogue;
            currentDialogueIndex++;
        } else {
            showFinalDialogue("True ending: Choice\n\n(Press Q to quit the game)");
            isFinalWorldDialogue = false;
        }
    }

    private void showFinalDialogue(String content) {
        showFinalDialogue = true;
        finalDialogueContent = content;
        repaint();
    }

    private void drawFinalDialogue(Graphics g) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));

        String[] lines = finalDialogueContent.split("\n");
        int y = 100;
        for (String line : lines) {
            g.drawString(line, 50, y);
            y += 30;
        }
    }

    private void showDialogue(String speaker, String content) {
        showDialogue = true;
        currentDialogue = new String[]{speaker, content};
        repaint();
    }
}
