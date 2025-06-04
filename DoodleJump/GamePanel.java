import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Player player;
    private ArrayList<Platform> platforms;
    private Timer timer;

    public GamePanel() {
        setFocusable(true);
        addKeyListener(this);

        // Initialize platforms
        platforms = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            platforms.add(new Platform((int) (Math.random() * 350), i * 100));
        }

        // Place the player on the first platform
        Platform firstPlatform = platforms.get(0);
        player = new Player(firstPlatform.getX() + firstPlatform.getWidth() / 2 - 15, firstPlatform.getY() - 30);

        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update();
        for (Platform platform : platforms) {
            if (player.isFalling() && platform.collidesWith(player)) {
                player.jump();
            }
        }
        if (player.getY() < 300) {
            for (Platform platform : platforms) {
                platform.setY(platform.getY() + 5);
                if (platform.getY() > 600) {
                    platform.setY(0);
                    platform.setX((int) (Math.random() * 350));
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.paint(g);
        for (Platform platform : platforms) {
            platform.paint(g);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            player.setDx(-5);
        } else if (key == KeyEvent.VK_RIGHT) {
            player.setDx(5);
        } else if (key == KeyEvent.VK_SPACE) {
            player.manualJump();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            player.setDx(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
