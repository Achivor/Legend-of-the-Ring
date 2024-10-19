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

    public NPC(int x, int y, String imagePath, ArrayList<String[]> dialogues) {
        this.x = x;
        this.y = y;
        this.dialogues = dialogues;
        this.currentDialogueIndex = 0;

        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Error: Could not load NPC image.");
        }

        // Create a slightly smaller collision box than the image
        int collisionBoxWidth = image.getWidth() - 10;
        int collisionBoxHeight = image.getHeight() - 10;
        this.collisionBox = new Rectangle(x + 5, y + 5, collisionBoxWidth, collisionBoxHeight);
    }

    public void draw(Graphics g) {
        // 计算新的宽度和高度以保持比例
        double scaleFactor = 1.5; // 你可以调整这个因子来改变大小
        int newWidth = (int) (image.getWidth() * scaleFactor);
        int newHeight = (int) (image.getHeight() * scaleFactor);

        // 使用新的宽度和高度绘制图像
        g.drawImage(image, x, y, newWidth, newHeight, null);

        // Uncomment the following line to see the collision box (for debugging)
        // g.drawRect(collisionBox.x, collisionBox.y, collisionBox.width, collisionBox.height);
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
}
