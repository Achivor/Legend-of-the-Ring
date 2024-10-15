package model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Item {
    private int x, y;
    private BufferedImage image;
    private String name;
    private Rectangle collisionBox;

    public Item(int x, int y, String imagePath, String name) {
        this.x = x;
        this.y = y;
        this.name = name;

        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Error: Could not load item image.");
        }

        this.collisionBox = new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public String getName() {
        return name;
    }
}
