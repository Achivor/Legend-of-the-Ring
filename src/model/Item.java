package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The class representing items in the game.
 * Each item has a position, image, name, description, and collision box.
 */
public class Item {
    private int x;
    private int y;
    private BufferedImage image;
    private String name;
    private Rectangle collisionBox;
    private String description;
    private double scaleFactor; // New: scale factor

    /**
     * Create a new game item.
     * @param x The x coordinate of the item.
     * @param y The y coordinate of the item.
     * @param imagePath The path to the item image.
     * @param name The name of the item.
     * @param description The description of the item.
     */
    public Item(int x, int y, String imagePath, String name, String description) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.description = description;
        this.scaleFactor = 1.0; // Default scale factor is 1.0

        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Error: Could not load item image.");
        }

        this.collisionBox = new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    /**
     * Create a game item with a scale factor.
     * @param x The x coordinate of the item.
     * @param y The y coordinate of the item.
     * @param imagePath The path to the item image.
     * @param name The name of the item.
     * @param description The description of the item.
     * @param scaleFactor The scale factor of the image.
     */
    public Item(int x, int y, 
        String imagePath, String name, String description, double scaleFactor) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.description = description;
        this.scaleFactor = scaleFactor; // Set scale factor

        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Error: Could not load item image.");
        }

        // Update collision box size
        int scaledWidth = (int) (image.getWidth() * scaleFactor);
        int scaledHeight = (int) (image.getHeight() * scaleFactor);
        this.collisionBox = new Rectangle(x, y, scaledWidth, scaledHeight);
    }

    /**
     * Draw the item in the specified graphics context.
     * @param g The graphics context to draw on.
     */
    public void draw(Graphics g) {
        int scaledWidth = (int) (image.getWidth() * scaleFactor);
        int scaledHeight = (int) (image.getHeight() * scaleFactor);
        g.drawImage(image, x, y, scaledWidth, scaledHeight, null);
    }

    /**
     * Get the collision box of the item.
     * @return The collision box of the item.
     */
    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    /**
     * Get the name of the item.
     * @return The name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of the item.
     * @return The description of the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the image of the item.
     * @return The image of the item.
     */
    public BufferedImage getImage() {
        return image;
    }
}
