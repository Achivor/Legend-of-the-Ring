package model;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Player {
    private int playerX = 100;  // 主角的初始 X 坐标
    private int playerY = 100;  // 主角的初始 Y 坐标
    private int speed = 2; // 修改速度值，降低移动速度

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

    private double scaleFactor = 1.0; // 你可以调整这个因子来改变大小，保持比例

    // 存储图像的原始宽度和高度
    private final int originalWidth;
    private final int originalHeight;

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

    public void update(List<Rectangle> walls) {
        // 根据移动状态更新角色位置
        if (movingUp) {
            moveUp(walls);
        }
        if (movingDown) {
            moveDown(walls);
        }
        if (movingLeft) {
            moveLeft(walls);
        }
        if (movingRight) {
            moveRight(walls);
        }
        updateAnimation();
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

        int width, height;
        // 根据缩放因子绘制主角，保持比例
        if (!isMoving){
            width = (int)(originalWidth * (scaleFactor - 0.1));
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
}
