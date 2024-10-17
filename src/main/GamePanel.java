package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import model.NPC;
import model.Player;

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

    private String[] dialogueSequence;
    private String[] dialogueSpeakers;
    private int currentDialogueIndex;

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

        // Initialize NPC with the new dialogue
        String[] npcDialogue = {
            "My lord, you finally woke up!",
            "May we start on our journey?"
        };
        npc = new NPC(325, 210, "src/resources/images/npc.png", npcDialogue);

        // Initialize dialogue sequence with speakers
        dialogueSequence = new String[]{
            "My lord, you finally woke up!",
            "May we start on our journey?",
            "The man was tied tightly by ropes, deep red marks reflecting on his skin. The person who did this must be hating him so much.",
            "(Shakes your head) I do not know you.",
            "(Stunned) You lost your memory? How is that possible? Check your pocket and put on that ring right now!",
            "You feel uncomfortable about the attitude of the man in front of you, is he your friend?",
            "I would not follow your command.",
            "(laughs viciously) Haha, you are still our honorable KING THORIAN. Fair, I am not in the position to tell you what to do. By the way, my name is Veldric, Veldric Shawdowbane. That is a fake name of course, you just need to know that I am your loyal magician!",
            "Now that you have lost your memory, I will explain a bit. \nYou were the hero of humankind, (Snorts) the most respected man in human history, as you defeated the most sinister demon in the world. After the battle, you fell in love with an elvish princess whose beauty is as radiant as the most precious pearl — I'm getting jealous (says contemptuously). \nHowever, she disappeared the day before your wedding, with your opponent. You chased them and went on to fight.",
            "You really dislike him now. You decide to leave immediately after he reveals all the useful information.",
            "Did i lose?",
            "(Smirks) Of course not. With that ring you would never encounter failure and Vivian loves you, she would never make you hurt.",
            "The familiar name invokes a sense of nostalgia and warmth in your heart. Even now, you still remember her lovely voice and noble heart.",
            "You should not mention her name like that.",
            "This is me, you highness. I am even more surprised now, you remember her even when you forget your name. Then it should be easy for me to explain what we are supposed to do. Your opponent trapped you here, a special place sheltered with magic. From my observation, there are three puzzles in this area. I am not very sure where it would lead us to after solving the puzzles, but unfortunately, this is our only clue. Shall we start now?",
            "You nod, then walk away.",
            "Can you not see I am also trapped here?! Come back and release me!"
        };

        dialogueSpeakers = new String[]{
            "Suspicious man", "Suspicious man", "Narrative", "You", "Suspicious man",
            "Narrative", "You", "Veldric", "Veldric", "Narrative", "You", "Veldric",
            "Narrative", "You", "Veldric", "Narrative", "Veldric"
        };

        currentDialogueIndex = 0;
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
    }

    public void update() {
        player.update(walls, npc.getCollisionBox());
        checkWorldSwitch();
        checkNPCInteraction();
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
            currentDialogue = dialogueSequence[currentDialogueIndex];
            currentDialogueIndex = (currentDialogueIndex + 1) % dialogueSequence.length;
            lastInteractionTime = currentTime;
            KeyInputHandler.resetInteractPressed(); // Reset the interact flag
        } else if (!npc.isPlayerNear(player)) {
            showDialogue = false;
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
    }

    private void drawDialogueBox(Graphics g) {
        // Draw heading
        g.setColor(new Color(50, 50, 50, 200)); // Dark gray, semi-transparent
        g.fillRect(50, 370, 700, 30); // Heading panel
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        
        String speaker = dialogueSpeakers[currentDialogueIndex];
        g.drawString(speaker, 60, 390);

        // Draw dialogue box
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(50, 400, 700, 150); // Main dialogue box
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawWrappedText(g, dialogueSequence[currentDialogueIndex], 70, 440, 660);
    }

    private void drawWrappedText(Graphics g, String text, int x, int y, int maxWidth) {
        FontMetrics fm = g.getFontMetrics();
        String[] words = text.split("\\s+");
        String line = "";
        for (String word : words) {
            if (fm.stringWidth(line + word) < maxWidth) {
                line += word + " ";
            } else {
                g.drawString(line, x, y);
                y += fm.getHeight();
                line = word + " ";
            }
        }
        if (line.trim().length() > 0) {
            g.drawString(line, x, y);
        }
    }
}
