package model;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MiddleMap {
    private Image backgroundImage; // 地图的背景图
    private ArrayList<Rectangle> walls;
    private int spawnX; // 传送后主角的 X 坐标
    private int spawnY; // 传送后主角的 Y 坐标

    public MiddleMap(String imagePath, int spawnX, int spawnY) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        loadImage(imagePath);
        walls = new ArrayList<>();
        // 定义空气墙的位置和大小 (x, y, width, height)
        walls.add(new Rectangle(0, 0, 800, 1)); // 一条横墙
        walls.add(new Rectangle(0, 0, 1, 600)); // 一条竖墙
        walls.add(new Rectangle(0, 600, 800, 10)); // 另一条横墙
        walls.add(new Rectangle(800, 0, 10, 600));
    }

    private void loadImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Error: Could not load background image from path: " + path);
        }
    }

    public void addWalls(Rectangle newWall){
        walls.add(newWall);
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public ArrayList<Rectangle> getwalls() { return walls; }

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }
}
