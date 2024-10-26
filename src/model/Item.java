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
    private String description;
    private double scaleFactor; // 新增：缩放因子

    public Item(int x, int y, String imagePath, String name, String description) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.description = description;
        this.scaleFactor = 1.0; // 默认缩放因子为1.0

        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Error: Could not load item image.");
        }

        this.collisionBox = new Rectangle(x, y, image.getWidth(), image.getHeight());
    }

    public Item(int x, int y, String imagePath, String name, String description, double scaleFactor) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.description = description;
        this.scaleFactor = scaleFactor; // 设置缩放因子

        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            System.out.println("Error: Could not load item image.");
        }

        // 更新碰撞箱大小
        int scaledWidth = (int) (image.getWidth() * scaleFactor);
        int scaledHeight = (int) (image.getHeight() * scaleFactor);
        this.collisionBox = new Rectangle(x, y, scaledWidth, scaledHeight);
    }

    public void draw(Graphics g) {
        int scaledWidth = (int) (image.getWidth() * scaleFactor);
        int scaledHeight = (int) (image.getHeight() * scaleFactor);
        g.drawImage(image, x, y, scaledWidth, scaledHeight, null);
    }

    public Rectangle getCollisionBox() {
        return collisionBox;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BufferedImage getImage() {
        return image;
    }
}
