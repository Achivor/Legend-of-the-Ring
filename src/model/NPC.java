/**
 *
 * @author Xiangjie Dong
 * @id 2072645
 * @author Yanhang Luo
 * @id 2123061
 */

package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * NPC class, used to represent NPC objects in the game.
 */
public class NPC {
    /** The x coordinate of the NPC in the game world. */
    private int x;
    /** The y coordinate of the NPC in the game world. */
    private int y;
    /** The image of the NPC. */
    private BufferedImage image;
    /** The collision detection area of the NPC. */
    private Rectangle collisionBox;
    /** The dialogue list of the NPC, each element contains a title and content. */
    private ArrayList<String[]> dialogues;
    /** The current dialogue index. */
    private int currentDialogueIndex;
    /** The scale of the NPC image. */
    private double scale;

    // Add a default constructor, using the default scale of 1.0
    public NPC(int x, int y, String imagePath, ArrayList<String[]> dialogues) {
        this(x, y, imagePath, dialogues, 1.5);
    }

    /**
     * Create a new NPC instance.
     * @param x The x coordinate of the NPC.
     * @param y The y coordinate of the NPC.
     * @param imagePath The file path of the NPC image.
     * @param dialogues The dialogue list of the NPC.
     * @param scale The scale of the NPC image.
     */
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

    /**
     * Draw the NPC on the screen.
     * @param g The graphics object to draw on.
     */
    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    /**
     * Get the collision detection area of the NPC.
     * @return The collision detection area of the NPC.
     */
    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    /**
     * Check if the player is near the NPC.
     * @param player The player object.
     * @return Whether the player is near the NPC.
     */
    public boolean isPlayerNear(Player player) {
        Rectangle expandedBox = new Rectangle(
            x - 10, y - 5, 
            image.getWidth() + 20, image.getHeight() + 10
        );
        return expandedBox.intersects(player.getCollisionBox());
    }

    /**
     * Get the next dialogue.
     * @param index The index of the dialogue.
     * @return The next dialogue.
     */
    public String[] getNextDialogue(int index) {
        if (index < dialogues.size()) {
            return dialogues.get(index);
        }
        return null;
    }

    /**
     * Get the number of dialogues.
     * @return The number of dialogues.
     */
    public int getDialogueCount() {
        return dialogues.size();
    }

    /**
     * Get the dialogue list of the NPC.
     * @return The dialogue list of the NPC.
     */
    public ArrayList<String[]> getDialogues() {
        return dialogues;
    }
}
