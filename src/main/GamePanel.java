package main;

import javax.swing.*;
import java.awt.*;
import model.NPC;
import model.Item;
import model.Inventory;

public class GamePanel extends JPanel {
    private final Image backgroundImage;
    private int playerX = 100, playerY = 100;
    private NPC npc;
    private boolean showDialog = false;
    private String currentDialog = "";
    private Item potion;
    private int itemX = 200, itemY = 150;
    private boolean itemPickedUp = false;
    private Inventory inventory;

    public GamePanel(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
        npc = new NPC(300, 200, "你好，勇士！欢迎来到这片神秘的大陆。");
        potion = new Item("药水", "可以恢复20点生命值");
        inventory = new Inventory();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.RED);
        g.fillRect(playerX, playerY, 30, 30);
        g.setColor(Color.BLUE);
        g.fillRect(npc.getX(), npc.getY(), 30, 30);

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

    public void movePlayer(int dx, int dy) {
        playerX += dx;
        playerY += dy;
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
