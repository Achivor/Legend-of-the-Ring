package model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Player {
    private int playerX = 285;  // 主角的初始 X 坐标
    private int playerY = 280;  // 主角的初始 Y 坐标
    private int speed = 3; // 修改速度值，降低移动速度

    // 用于跟踪移动状态
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    private String currentDirection = "down";
    private boolean isMoving = false;

    private final Image[] playerUpImages;
    private final Image[] playerDownImages;
    private final Image[] playerLeftImages;
    private final Image[] playerRightImages;

    private final Image playerUpStandImage;
    private final Image playerDownStandImage;
    private final Image playerLeftStandImage;
    private final Image playerRightStandImage;

    private int animationFrame = 0;
    private int animationSpeed = 10;
    private int animationCounter = 0;

    private double scaleFactor = 1.2; // 你可以调整这个因子来改变大小，保持比例

    // 存储图像的原始宽度和高度
    private final int originalWidth;
    private final int originalHeight;

    private ArrayList<Item> inventory; // 背包

    public Player() {
        // 加载行走动画图片
        playerUpImages = new Image[] {
                loadImage("src/resources/images/player_up_1.png"),
                loadImage("src/resources/images/player_up_2.png")
        };
        playerDownImages = new Image[] {
                loadImage("src/resources/images/player_down_1.png"),
                loadImage("src/resources/images/player_down_2.png")
        };
        playerLeftImages = new Image[] {
                loadImage("src/resources/images/player_left_1.png"),
                loadImage("src/resources/images/player_left_2.png")
        };
        playerRightImages = new Image[] {
                loadImage("src/resources/images/player_right_1.png"),
                loadImage("src/resources/images/player_right_2.png")
        };

        // 加载站立姿势图片
        playerUpStandImage = loadImage("src/resources/images/player_up_stand.png");
        playerDownStandImage = loadImage("src/resources/images/player_down_stand.png");
        playerLeftStandImage = loadImage("src/resources/images/player_left_stand.png");
        playerRightStandImage = loadImage("src/resources/images/player_right_stand.png");

        // 设置原始图像的宽度和高度（以第一帧为例，假设它们是相同的）
        originalWidth = playerDownImages[0].getWidth(null);
        originalHeight = playerDownImages[0].getHeight(null);

        inventory = new ArrayList<>();
    }

    private Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));  // 直接从文件系统加载图片
        } catch (IOException e) {
            System.out.println("Error: Could not load image from path: " + path);
            return null;
        }
    }

    public void setMovingUp(boolean moving) {
        this.movingUp = moving;
    }

    public void setMovingDown(boolean moving) {
        this.movingDown = moving;
    }

    public void setMovingLeft(boolean moving) {
        this.movingLeft = moving;
    }

    public void setMovingRight(boolean moving) {
        this.movingRight = moving;
    }

    public void update(List<Rectangle> walls, Rectangle npcCollisionBox) {
        int newX = playerX;
        int newY = playerY;

        isMoving = false;

        if (movingUp) {
            newY -= speed;
            currentDirection = "up";
            isMoving = true;
        }
        if (movingDown) {
            newY += speed;
            currentDirection = "down";
            isMoving = true;
        }
        if (movingLeft) {
            newX -= speed;
            currentDirection = "left";
            isMoving = true;
        }
        if (movingRight) {
            newX += speed;
            currentDirection = "right";
            isMoving = true;
        }

        if (canMove(newX, newY, walls, npcCollisionBox)) {
            playerX = newX;
            playerY = newY;
        }

        updateAnimation();
    }

    private boolean canMove(int x, int y, List<Rectangle> walls, Rectangle npcCollisionBox) {
        Rectangle playerBounds = new Rectangle(x, y, (int)(originalWidth * scaleFactor), (int)(originalHeight * scaleFactor));
        
        for (Rectangle wall : walls) {
            if (playerBounds.intersects(wall)) {
                return false;
            }
        }

        if (npcCollisionBox != null && playerBounds.intersects(npcCollisionBox)) {
            return false;
        }

        return true;
    }

    public void moveUp(List<Rectangle> walls) {
        if (canMove(playerX, playerY - speed, walls)) {
            playerY -= speed;
        }
        currentDirection = "up";
    }

    public void moveDown(List<Rectangle> walls) {
        if (canMove(playerX, playerY + speed, walls)) {
            playerY += speed;
        }
        currentDirection = "down";
    }

    public void moveLeft(List<Rectangle> walls) {
        if (canMove(playerX - speed, playerY, walls)) {
            playerX -= speed;
        }
        currentDirection = "left";
    }

    public void moveRight(List<Rectangle> walls) {
        if (canMove(playerX + speed, playerY, walls)) {
            playerX += speed;
        }
        currentDirection = "right";
    }

    private boolean canMove(int x, int y, List<Rectangle> walls) {
        Rectangle playerBounds = new Rectangle(x, y, (int)(originalWidth * scaleFactor), (int)(originalHeight * scaleFactor));
        for (Rectangle wall : walls) {
            if (playerBounds.intersects(wall)) {
                return false; // 如果碰撞，不能移动
            }
        }
        return true; // 没有碰撞，可以移动
    }

    public void updateAnimation() {
        if (isMoving) {
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                animationFrame = (animationFrame + 1) % 2;  // 动画帧切换
            }
        } else {
            animationFrame = 0; // 静止时显示第一帧
        }
    }

    public void draw(Graphics g) {
        Image imageToDraw;
        if (isMoving) {
            switch (currentDirection) {
                case "up":
                    imageToDraw = playerUpImages[animationFrame];
                    break;
                case "down":
                    imageToDraw = playerDownImages[animationFrame];
                    break;
                case "left":
                    imageToDraw = playerLeftImages[animationFrame];
                    break;
                case "right":
                    imageToDraw = playerRightImages[animationFrame];
                    break;
                default:
                    imageToDraw = playerDownImages[0];
                    break;
            }
        } else {
            switch (currentDirection) {
                case "up":
                    imageToDraw = playerUpStandImage;
                    break;
                case "down":
                    imageToDraw = playerDownStandImage;
                    break;
                case "left":
                    imageToDraw = playerLeftStandImage;
                    break;
                case "right":
                    imageToDraw = playerRightStandImage;
                    break;
                default:
                    imageToDraw = playerDownStandImage;
                    break;
            }
        }

        int width, height;
        // 根据缩放因子绘制主角，保持比例
        if (!isMoving){
            width = (int)(originalWidth * (scaleFactor - 0.2));
            height = (int)(originalHeight * scaleFactor);
        } else {
            width = (int) (originalWidth * scaleFactor);
            height = (int) (originalHeight * scaleFactor);
        }


        g.drawImage(imageToDraw, playerX, playerY, width, height, null);
    }

    // 新增方法：设置缩放因子
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    public void setPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
    }
    // 新增方法：获取主角的 X 坐标
    public int getX() {
        return playerX;
    }

    // 新增方法：获取主角的 Y 坐标
    public int getY() {
        return playerY;
    }

    public Rectangle getCollisionBox() {
        return new Rectangle(playerX, playerY, (int)(originalWidth * scaleFactor), (int)(originalHeight * scaleFactor));
    }

    public void addItem(Item item) {
        inventory.add(item);
        System.out.println("Picked up: " + item.getName());
    }

    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }
}
