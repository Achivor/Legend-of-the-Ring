package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NPC {
    private int x, y;
    private BufferedImage image;
    private Rectangle collisionBox;
    private String[] dialogue;
    private int currentDialogueIndex;

    public NPC(int x, int y, String imagePath, String[] dialogue) {
        this.x = x;
        this.y = y;
        this.dialogue = dialogue;
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
        g.drawImage(image, x, y, null);
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

    public String getNextDialogue() {
        String currentDialogue = dialogue[currentDialogueIndex];
        currentDialogueIndex = (currentDialogueIndex + 1) % dialogue.length;
        return currentDialogue;
    }
}
