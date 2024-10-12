package main;

import javax.swing.*;
import java.awt.*;
import model.NPC;
import model.Item;
import model.Inventory;

public class GamePanel extends JPanel {
    private final Image backgroundImage;
    private Image playerImage;
    private Image npcImage;
    private int playerX = 100, playerY = 100;
    private NPC npc;
    private boolean showDialog = false;
    private String currentDialog = "";
    private Item potion;
    private int itemX = 200, itemY = 150;
    private boolean itemPickedUp = false;
    private Inventory inventory;
    private int[][] map = {  // 地图数组：1表示墙，0表示可通过
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 1, 0, 0, 0, 1, 0, 1},
            {1, 0, 0, 1, 1, 1, 0, 0, 0, 1},
            {1, 1, 0, 0, 0, 0, 0, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    private final int tileSize = 50;

    public GamePanel(String backgroundPath, String playerImagePath, String npcImagePath) {
        backgroundImage = new ImageIcon(backgroundPath).getImage();
        playerImage = new ImageIcon(playerImagePath).getImage();  // 加载主角图像
        npcImage = new ImageIcon(npcImagePath).getImage();        // 加载NPC图像
        npc = new NPC(300, 200, "你好，勇士！欢迎来到这片神秘的大陆。");
        potion = new Item("药水", "可以恢复20点生命值");
        inventory = new Inventory();
    }

    private Point scaledSize(Image image, double scale){
        int originalWidth = image.getWidth(this);
        int originalHeight = image.getHeight(this);

        Point p = new Point();
        p.x = (int)(originalWidth * scale);
        p.y = (int)(originalHeight * scale);

        return p;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        drawMap(g); // 绘制地图

        g.drawImage(playerImage, playerX, playerY, this.scaledSize(playerImage, 1.5).x, this.scaledSize(playerImage, 1.5).y, this);          // 绘制主角

        g.drawImage(npcImage, npc.getX(), npc.getY(), this.scaledSize(npcImage, 1.5).x, this.scaledSize(npcImage, 1.5).y, this);       // 绘制NPC

        if (!itemPickedUp) {
            g.setColor(Color.GREEN);
            g.fillRect(itemX, itemY, 20, 20);
        }

        if (showDialog) {
            g.setColor(Color.WHITE);
            g.fillRect(50, 400, 700, 100);
            g.setColor(Color.BLACK);
            g.drawRect(50, 400, 700, 100);
            g.drawString(currentDialog, 60, 450);
        }
    }

    // 绘制地图
    public void drawMap(Graphics g) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                if (map[y][x] == 1) {
                    g.setColor(Color.GRAY); // 用灰色表示隐形墙
                    g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }
    }

    public void movePlayer(int dx, int dy) {
        int newPlayerX = playerX + dx;
        int newPlayerY = playerY + dy;

        // 计算主角当前位置在地图中的格子坐标
        int playerTileX = newPlayerX / tileSize;
        int playerTileY = newPlayerY / tileSize;

        // 检查是否撞到墙
        if (map[playerTileY][playerTileX] != 1) {
            playerX = newPlayerX;
            playerY = newPlayerY;
        }

        checkInteraction();
        repaint();
    }

    public void checkInteraction() {
        if (npc.isNear(playerX, playerY)) {
            currentDialog = npc.getDialog();
            showDialog = true;
        } else {
            showDialog = false;
        }
        if (Math.abs(playerX - itemX) < 30 && Math.abs(playerY - itemY) < 30 && !itemPickedUp) {
            itemPickedUp = true;
            inventory.addItem(potion);
            System.out.println("你拾取了：" + potion.getName());
        }
    }

    public void showInventory() {
        inventory.showInventory();
    }
}
