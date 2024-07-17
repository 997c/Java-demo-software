import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Platform {
    private int x, y;
    private int width = 60, height = 10;

    public Platform(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(x, y, width, height);
    }

    public boolean collidesWith(Player player) {
        Rectangle platformRect = new Rectangle(x, y, width, height);
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        return platformRect.intersects(playerRect);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
