package model;

public class NPC {
    private int x, y;
    private String dialog;

    public NPC(int x, int y, String dialog) {
        this.x = x;
        this.y = y;
        this.dialog = dialog;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getDialog() {
        return dialog;
    }

    public boolean isNear(int playerX, int playerY) {
        int distance = 50;
        return Math.abs(playerX - x) < distance && Math.abs(playerY - y) < distance;
    }
}
