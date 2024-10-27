/**
 *
 * @author Xiangjie Dong
 * @id 2072645
 * @author Yanhang Luo
 * @id 2123061
 */

package model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * The player class, representing the main character in the game.
 * Handles the player's movement, animation, and item collection.
 */
public class Player {
    /** The x coordinate of the player in the game world. */
    private int playerX = 384;
    /** The y coordinate of the player in the game world. */
    private int playerY = 268;
    /** The speed of the player. */
    private final int speed = 3;
    
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
    private final int animationSpeed = 10;
    private int animationCounter = 0;

    private double scaleFactor = 1.2;

    private final int originalWidth;
    private final int originalHeight;

    private final ArrayList<Item> inventory; 

    private boolean hasKey = false; 
    private boolean hasAxe = false;


    /**
     * Initialize the player object, load all necessary image resources and set the initial state.
     */
    public Player() {
        // Load walking animation images
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

        // Load standing pose images
        playerUpStandImage = loadImage("src/resources/images/player_up_stand.png");
        playerDownStandImage = loadImage("src/resources/images/player_down_stand.png");
        playerLeftStandImage = loadImage("src/resources/images/player_left_stand.png");
        playerRightStandImage = loadImage("src/resources/images/player_right_stand.png");

        // Set the original width and height of the image (assuming the first frame is the same)
        originalWidth = playerDownImages[0].getWidth(null);
        originalHeight = playerDownImages[0].getHeight(null);

        inventory = new ArrayList<>();
    }

    private Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));  // Load image directly from the file system
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

    /**
     * Update the player's position and animation state.
     * @param walls The list of wall collision boxes in the game.
     * @param npcCollisionBox The collision box of the NPC.
     */
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
        Rectangle playerBounds = new Rectangle(x, y, 
            (int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));
        
        for (Rectangle wall : walls) {
            if (playerBounds.intersects(wall)) {
                return false;
            }
        }

        return !(npcCollisionBox != null && playerBounds.intersects(npcCollisionBox));
    }

    private boolean canMove(int x, int y, List<Rectangle> walls) {
        Rectangle playerBounds = new Rectangle(x, y, 
            (int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));
        for (Rectangle wall : walls) {
            if (playerBounds.intersects(wall)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Move the player up.
     * @param walls The list of wall collision boxes in the game.
     */
    public void moveUp(List<Rectangle> walls) {
        if (canMove(playerX, playerY - speed, walls)) {
            playerY -= speed;
        }
        currentDirection = "up";
    }

    /**
     * Move the player down.
     * @param walls The list of wall collision boxes in the game.
     */
    public void moveDown(List<Rectangle> walls) {
        if (canMove(playerX, playerY + speed, walls)) {
            playerY += speed;
        }
        currentDirection = "down";
    }

    /**
     * Move the player left.
     * @param walls The list of wall collision boxes in the game.
     */
    public void moveLeft(List<Rectangle> walls) {
        if (canMove(playerX - speed, playerY, walls)) {
            playerX -= speed;
        }
        currentDirection = "left";
    }

    /**
     * Move the player right.
     * @param walls The list of wall collision boxes in the game.
     */
    public void moveRight(List<Rectangle> walls) {
        if (canMove(playerX + speed, playerY, walls)) {
            playerX += speed;
        }
        currentDirection = "right";
    }

    /**
     * Update the player's animation state.
     * Update the animation frame when the player is moving, reset the animation when stationary.
     */
    public void updateAnimation() {
        if (isMoving) {
            animationCounter++;
            if (animationCounter >= animationSpeed) {
                animationCounter = 0;
                animationFrame = (animationFrame + 1) % 2; // Switch animation frame (0 or 1)
            }
        } else {
            animationFrame = 0; // Draw the first frame when stationary
        }
    }

    /**
     * Draw the player character to the game screen.
     * @param g Graphics object, used to draw the player sprite.
     */
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

        int width;
        int height;
        if (!isMoving) {
            width = (int) (originalWidth * (scaleFactor - 0.2));
            height = (int) (originalHeight * scaleFactor);
        } else {
            width = (int) (originalWidth * scaleFactor);
            height = (int) (originalHeight * scaleFactor);
        }


        g.drawImage(imageToDraw, playerX, playerY, width, height, null);
    }

    /**
     * Set the scale factor for the player.
     * @param scaleFactor The scale factor to set.
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Set the position of the player.
     * @param x The x coordinate to set.
     * @param y The y coordinate to set.
     */
    public void setPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
    }

    /**
     * Get the x coordinate of the player.
     * @return The x coordinate of the player.
     */
    public int getX() {
        return playerX;
    }

    /**
     * Get the y coordinate of the player.
     * @return The y coordinate of the player.
     */
    public int getY() {
        return playerY;
    }

    /**
     * Get the collision box of the player.
     * @return The collision box of the player.
     */
    public Rectangle getCollisionBox() {
        return new Rectangle(playerX, playerY, 
            (int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor));
    }

    /**
     * Add an item to the player's inventory.
     * @param item The item to add.
     */
    public void addItem(Item item) {
        if (item.getName().equals("Key")) {
            hasKey = true;
        } else if (item.getName().equals("Axe")) {
            hasAxe = true;
        }
        inventory.add(item);
        System.out.println("Picked up: " + item.getName());
    }

    /**
     * Check if the player has an item.
     * @param itemName The name of the item to check.
     * @return True if the player has the item, false otherwise.
     */
    public boolean hasItem(String itemName) {
        if (itemName.equals("Key")) {
            return hasKey;
        } else if (itemName.equals("Axe")) {
            return hasAxe;
        }
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the player's inventory.
     * @return The player's inventory.
     */
    public ArrayList<Item> getInventory() {
        return inventory;
    }

    /**
     * Remove an item from the player's inventory.
     * @param itemName The name of the item to remove.
     */
    public void removeItem(String itemName) {
        inventory.removeIf(item -> item.getName().equals(itemName));
    }
}
