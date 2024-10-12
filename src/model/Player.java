package model;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

public class Player {
    private int playerX = 100;  // 主角的初始 X 坐标
    private int playerY = 100;  // 主角的初始 Y 坐标
    private int speed = 2;

    // 用于跟踪移动状态
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    private String currentDirection = "down";
    private boolean isMoving = false;

    private Image[] playerUpImages;
    private Image[] playerDownImages;
    private Image[] playerLeftImages;
    private Image[] playerRightImages;

    private Image playerUpStandImage;
    private Image playerDownStandImage;
    private Image playerLeftStandImage;
    private Image playerRightStandImage;

    private int animationFrame = 0;
    private int animationSpeed = 10;
    private int animationCounter = 0;

    // 假设地图尺寸为1000x1000
    private final int mapWidth = 1000;
    private final int mapHeight = 1000;
    private final int tileSize = 50;

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

    public void update() {
        // 根据移动状态更新角色位置
        if (movingUp) {
            moveUp();
        }
        if (movingDown) {
            moveDown();
        }
        if (movingLeft) {
            moveLeft();
        }
        if (movingRight) {
            moveRight();
        }
        updateAnimation();
    }

    public void moveUp() {
        if (playerY - speed >= 0) {
            playerY -= speed;
            currentDirection = "up";
        }
    }

    public void moveDown() {
        if (playerY + speed + tileSize <= mapHeight) {
            playerY += speed;
            currentDirection = "down";
        }
    }

    public void moveLeft() {
        if (playerX - speed >= 0) {
            playerX -= speed;
            currentDirection = "left";
        }
    }

    public void moveRight() {
        if (playerX + speed + tileSize <= mapWidth) {
            playerX += speed;
            currentDirection = "right";
        }
    }

    public void updateAnimation() {
        if (movingUp || movingDown || movingLeft || movingRight) {
            isMoving = true;
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                animationFrame = (animationFrame + 1) % 2;  // 动画帧切换
            }
        } else {
            isMoving = false;
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
        g.drawImage(imageToDraw, playerX, playerY, null);
    }
}
