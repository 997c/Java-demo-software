import java.awt.Color;
import java.awt.Graphics;

public class Player {
    private int x, y, dx;
    private int width = 30, height = 30;
    private int dy = 0;
    private boolean falling = true;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (falling) {
            dy += 1;
        } else {
            dy = -10;
            falling = true;
        }
        y += dy;
        x += dx;
        if (x < 0) x = 0;
        if (x > 370) x = 370;
    }

    public void paint(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isFalling() {
        return falling;
    }

    public void jump() {
        falling = false;
    }

    public void manualJump() {
        dy = -15;
    }
}
