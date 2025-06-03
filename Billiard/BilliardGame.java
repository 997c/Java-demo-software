import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Iterator;

public class BilliardGame extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int BALL_RADIUS = 15;
    private static final int CUE_LENGTH = 150; // Static length
    private static final int CUE_WIDTH = 8; // Thicker cue
    private static final int HOLE_RADIUS = 20;

    private Ball whiteBall;
    private ArrayList<Ball> balls = new ArrayList<>();
    private boolean aiming = false;
    private int cueX, cueY;
    private ArrayList<Hole> holes = new ArrayList<>();
    private int score = 0;

    public BilliardGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(0, 100, 0)); // Dark green
        initBalls();
        initHoles();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                aiming = true;
                cueX = e.getX();
                cueY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (aiming) {
                    int dx = cueX - e.getX();
                    int dy = cueY - e.getY();
                    double power = Math.sqrt(dx * dx + dy * dy) / 10;
                    whiteBall.dx = dx / 10.0;
                    whiteBall.dy = dy / 10.0;
                    aiming = false;
                }
            }
        });
    }

    private void initBalls() {
        whiteBall = new Ball(WIDTH / 2, HEIGHT / 2, BALL_RADIUS, Color.WHITE, 0);
        balls.add(whiteBall);
        balls.add(new Ball(WIDTH / 3, HEIGHT / 2, BALL_RADIUS, Color.RED, 1));
        balls.add(new Ball(WIDTH / 4, HEIGHT / 2, BALL_RADIUS, Color.BLUE, 2));
        // Add all 15 balls with colors and numbers
        Color[] colors = {
                new Color(255, 0, 0), new Color(255, 127, 0), new Color(255, 255, 0), 
                new Color(0, 255, 0), new Color(0, 255, 255), new Color(0, 0, 255), 
                new Color(139, 0, 255), new Color(255, 69, 0), new Color(255, 105, 180), 
                new Color(255, 215, 0), new Color(0, 128, 0), new Color(0, 191, 255), 
                new Color(75, 0, 130), new Color(128, 0, 128), new Color(178, 34, 34)
        };

        for (int i = 3; i <= 15; i++) {
            balls.add(new Ball(WIDTH / (4 + i % 4), HEIGHT / (2 + i % 3), BALL_RADIUS, colors[i - 1], i));
        }
    }

    private void initHoles() {
        holes.add(new Hole(HOLE_RADIUS, HOLE_RADIUS));
        holes.add(new Hole(WIDTH - HOLE_RADIUS, HOLE_RADIUS));
        holes.add(new Hole(WIDTH - HOLE_RADIUS, HEIGHT - HOLE_RADIUS));
        holes.add(new Hole(HOLE_RADIUS, HEIGHT - HOLE_RADIUS));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw table borders
        g.setColor(new Color(139, 69, 19)); // Brown wood color
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(new Color(0, 100, 0)); // Dark green table color
        g.fillRect(20, 20, WIDTH - 40, HEIGHT - 40);

        for (Ball ball : balls) {
            g.setColor(ball.color);
            g.fillOval((int) ball.x - BALL_RADIUS, (int) ball.y - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(ball.number), (int) ball.x - 5, (int) ball.y + 5);
        }
        for (Hole hole : holes) {
            g.setColor(Color.BLACK);
            g.fillOval((int) hole.x - HOLE_RADIUS, (int) hole.y - HOLE_RADIUS, HOLE_RADIUS * 2, HOLE_RADIUS * 2);
        }
        if (aiming) {
            g.setColor(Color.BLACK);
            double angle = Math.atan2(whiteBall.y - cueY, whiteBall.x - cueX);
            int endX = (int) (whiteBall.x + CUE_LENGTH * Math.cos(angle));
            int endY = (int) (whiteBall.y + CUE_LENGTH * Math.sin(angle));
            g.fillRect((int) whiteBall.x, (int) whiteBall.y, endX - (int) whiteBall.x, endY - (int) whiteBall.y);
        }
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, WIDTH / 2 - 30, 20); // Score in the upper middle
    }

    public void move() {
        Iterator<Ball> iterator = balls.iterator();
        while (iterator.hasNext()) {
            Ball ball = iterator.next();
            ball.move();
            checkCollisions(ball);
            checkHoles(ball, iterator);
        }
    }

    private void checkCollisions(Ball ball) {
        if (ball.x - BALL_RADIUS < 0 || ball.x + BALL_RADIUS > WIDTH) {
            ball.dx = -ball.dx;
        }
        if (ball.y - BALL_RADIUS < 0 || ball.y + BALL_RADIUS > HEIGHT) {
            ball.dy = -ball.dy;
        }

        for (Ball other : balls) {
            if (ball != other && ball.intersects(other)) {
                double dx = other.x - ball.x;
                double dy = other.y - ball.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                double overlap = 0.5 * (distance - 2 * BALL_RADIUS);

                ball.x -= overlap * (ball.x - other.x) / distance;
                ball.y -= overlap * (ball.y - other.y) / distance;
                other.x += overlap * (ball.x - other.x) / distance;
                other.y += overlap * (ball.y - other.y) / distance;

                double dvx = other.dx - ball.dx;
                double dvy = other.dy - ball.dy;
                double dotProduct = dx * dvx + dy * dvy;

                if (dotProduct > 0) {
                    double collisionScale = dotProduct / (distance * distance);
                    double fx = dx * collisionScale;
                    double fy = dy * collisionScale;

                    ball.dx += fx;
                    ball.dy += fy;
                    other.dx -= fx;
                    other.dy -= fy;
                }
            }
        }
    }

    private void checkHoles(Ball ball, Iterator<Ball> iterator) {
        for (Hole hole : holes) {
            if (hole.contains(ball)) {
                iterator.remove();
                score++;
                break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Billiard Game");
        BilliardGame billiardGame = new BilliardGame();
        frame.add(billiardGame);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        while (true) {
            billiardGame.move();
            billiardGame.repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Ball {
        double x, y, dx, dy;
        int radius;
        Color color;
        int number;

        Ball(double x, double y, int radius, Color color, int number) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.number = number;
        }

        void move() {
            x += dx;
            y += dy;
            dx *= 0.99;
            dy *= 0.99;
        }

        boolean intersects(Ball other) {
            double dx = other.x - x;
            double dy = other.y - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance < (radius + other.radius);
        }
    }

    class Hole {
        double x, y;

        Hole(double x, double y) {
            this.x = x;
            this.y = y;
        }

        boolean contains(Ball ball) {
            double dx = ball.x - x;
            double dy = ball.y - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance < (HOLE_RADIUS + BALL_RADIUS);
        }
    }
}
