package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class NPC {
    private int x, y;
    private BufferedImage image;
    private Rectangle collisionBox;
    private ArrayList<String[]> dialogues; // 每个元素是一个包含标题和内容的数组
    private int currentDialogueIndex;
    private double scale;

    // 添加一个默认的构造函数，使用默认缩放比例 1.0
    public NPC(int x, int y, String imagePath, ArrayList<String[]> dialogues) {
        this(x, y, imagePath, dialogues, 1.5);
    }

    // 保留带缩放参数的构造函数
    public NPC(int x, int y, String imagePath, ArrayList<String[]> dialogues, double scale) {
        this.x = x;
        this.y = y;
        this.dialogues = dialogues;
        this.scale = scale;
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            int newWidth = (int) (originalImage.getWidth() * scale);
            int newHeight = (int) (originalImage.getHeight() * scale);
            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
        collisionBox = new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public boolean isPlayerNear(Player player) {
        Rectangle expandedBox = new Rectangle(
            x - 20, y - 20, 
            image.getWidth() + 40, image.getHeight() + 40
        );
        return expandedBox.intersects(player.getCollisionBox());
    }

    public String[] getNextDialogue(int index) {
        if (index < dialogues.size()) {
            return dialogues.get(index);
        }
        return null;
    }

    public int getDialogueCount() {
        return dialogues.size();
    }

    public ArrayList<String[]> getDialogues() {
        return dialogues;
    }
}
