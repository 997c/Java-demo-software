import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Snake3DGame extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
    private static final int TILE_SIZE = 20;
    private static final int WIDTH = 15;
    private static final int HEIGHT = 15;
    private static final int DEPTH = 15;
    private static final int DELAY = 150;
    private static final int CUBE_SIZE = 300;
    
    private ArrayList<Point3D> snake;
    private Point3D food;
    private int directionX, directionY, directionZ;
    private boolean running = false;
    private Timer timer;
    private Random random;
    private double rotationAngleX = 0;
    private double rotationAngleY = 0;
    private boolean showControls = true;
    private Point lastMousePoint;
    private boolean mouseDragging = false;
    
    // Colors
    private final Color CUBE_COLOR = new Color(50, 50, 50, 150);
    private final Color GRID_COLOR = new Color(100, 100, 100, 100);
    private final Color SNAKE_HEAD_COLOR = Color.GREEN;
    private final Color SNAKE_BODY_COLOR = new Color(0, 150, 0);
    private final Color FOOD_COLOR = Color.RED;
    private final Color TEXT_COLOR = Color.WHITE;
    
    public Snake3DGame() {
        random = new Random();
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        
        initGame();
    }
    
    private void initGame() {
        snake = new ArrayList<>();
        snake.add(new Point3D(WIDTH / 2, HEIGHT / 2, DEPTH / 2));
        
        directionX = 1;
        directionY = 0;
        directionZ = 0;
        
        spawnFood();
        
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    private void spawnFood() {
        int x, y, z;
        boolean onSnake;
        
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
            z = random.nextInt(DEPTH);
            
            onSnake = false;
            for (Point3D segment : snake) {
                if (segment.x == x && segment.y == y && segment.z == z) {
                    onSnake = true;
                    break;
                }
            }
        } while (onSnake);
        
        food = new Point3D(x, y, z);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rotating cube
        drawRotatingCube(g2d);
        
        if (running) {
            // Draw food
            draw3DPoint(g2d, food, FOOD_COLOR);
            
            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point3D segment = snake.get(i);
                Color color = (i == 0) ? SNAKE_HEAD_COLOR : SNAKE_BODY_COLOR;
                draw3DPoint(g2d, segment, color);
            }
            
            // Draw score
            g2d.setColor(TEXT_COLOR);
            g2d.drawString("Score: " + (snake.size() - 1), 20, 30);
            
            // Draw controls help if enabled
            if (showControls) {
                drawControls(g2d);
            }
        } else {
            gameOver(g2d);
        }
    }
    
    private void drawRotatingCube(Graphics2D g2d) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Save original transform
        AffineTransform oldTransform = g2d.getTransform();
        
        // Rotate around center (X and Y axes)
        g2d.rotate(rotationAngleX, centerX, centerY);
        g2d.rotate(rotationAngleY, centerX, centerY);
        
        // Draw cube edges
        g2d.setColor(CUBE_COLOR);
        int halfSize = CUBE_SIZE / 2;
        
        // Front face
        g2d.drawRect(centerX - halfSize, centerY - halfSize, CUBE_SIZE, CUBE_SIZE);
        
        // Back face (smaller to simulate perspective)
        int backSize = (int)(CUBE_SIZE * 0.8);
        int backOffset = (CUBE_SIZE - backSize) / 2;
        g2d.drawRect(centerX - halfSize + backOffset, centerY - halfSize + backOffset, backSize, backSize);
        
        // Connecting lines
        g2d.drawLine(centerX - halfSize, centerY - halfSize, 
                    centerX - halfSize + backOffset, centerY - halfSize + backOffset);
        g2d.drawLine(centerX + halfSize, centerY - halfSize, 
                    centerX + halfSize - backOffset, centerY - halfSize + backOffset);
        g2d.drawLine(centerX - halfSize, centerY + halfSize, 
                    centerX - halfSize + backOffset, centerY + halfSize - backOffset);
        g2d.drawLine(centerX + halfSize, centerY + halfSize, 
                    centerX + halfSize - backOffset, centerY + halfSize - backOffset);
        
        // Draw grid lines inside cube
        g2d.setColor(GRID_COLOR);
        int gridStep = CUBE_SIZE / WIDTH;
        
        // X-axis lines (front face)
        for (int i = 1; i < WIDTH; i++) {
            int x = centerX - halfSize + i * gridStep;
            g2d.drawLine(x, centerY - halfSize, x, centerY + halfSize);
        }
        
        // Y-axis lines (front face)
        for (int i = 1; i < HEIGHT; i++) {
            int y = centerY - halfSize + i * gridStep;
            g2d.drawLine(centerX - halfSize, y, centerX + halfSize, y);
        }
        
        // Restore original transform
        g2d.setTransform(oldTransform);
    }
    
    private void draw3DPoint(Graphics2D g2d, Point3D point, Color color) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int halfSize = CUBE_SIZE / 2;
        int gridStep = CUBE_SIZE / WIDTH;
        
        // Calculate position in 3D space (with perspective)
        double perspective = 1.0 - (point.z * 0.05);
        
        int x = centerX - halfSize + (int)(point.x * gridStep * perspective);
        int y = centerY - halfSize + (int)(point.y * gridStep * perspective);
        int size = (int)(TILE_SIZE * perspective);
        
        // Apply rotation
        AffineTransform oldTransform = g2d.getTransform();
        g2d.rotate(rotationAngleX, centerX, centerY);
        g2d.rotate(rotationAngleY, centerX, centerY);
        
        // Draw the point
        g2d.setColor(color);
        g2d.fillOval(x - size/2, y - size/2, size, size);
        
        // Add highlight for 3D effect
        g2d.setColor(color.brighter());
        g2d.drawArc(x - size/2, y - size/2, size, size, 45, 180);
        
        // Restore transform
        g2d.setTransform(oldTransform);
    }
    
    private void drawControls(Graphics2D g2d) {
        int panelWidth = 220;
        int panelHeight = 140;
        int x = getWidth() - panelWidth - 20;
        int y = 20;
        
        // Draw control panel background
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x, y, panelWidth, panelHeight, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(x, y, panelWidth, panelHeight, 10, 10);
        
        // Draw controls text
        g2d.setColor(TEXT_COLOR);
        g2d.drawString("Controls:", x + 10, y + 20);
        g2d.drawString("X-Axis: LEFT/RIGHT Arrow", x + 10, y + 40);
        g2d.drawString("Y-Axis: UP/DOWN Arrow", x + 10, y + 60);
        g2d.drawString("Z-Axis: W/S Keys", x + 10, y + 80);
        g2d.drawString("Rotate Cube: Mouse Drag", x + 10, y + 100);
        g2d.drawString("Toggle Help: H", x + 10, y + 120);
    }
    
    private void gameOver(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics = g2d.getFontMetrics();
        g2d.drawString("Game Over", (getWidth() - metrics.stringWidth("Game Over")) / 2, getHeight() / 2);
        
        g2d.setColor(TEXT_COLOR);
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        metrics = g2d.getFontMetrics();
        g2d.drawString("Score: " + (snake.size() - 1), (getWidth() - metrics.stringWidth("Score: " + (snake.size() - 1))) / 2, getHeight() / 2 + 40);
        
        g2d.drawString("Press SPACE to restart", (getWidth() - metrics.stringWidth("Press SPACE to restart")) / 2, getHeight() / 2 + 70);
    }
    
    private void move() {
        Point3D head = snake.get(0);
        Point3D newHead = new Point3D(
            (head.x + directionX + WIDTH) % WIDTH,
            (head.y + directionY + HEIGHT) % HEIGHT,
            (head.z + directionZ + DEPTH) % DEPTH
        );
        
        // Check for collisions with self
        for (Point3D segment : snake) {
            if (segment.x == newHead.x && segment.y == newHead.y && segment.z == newHead.z) {
                running = false;
                timer.stop();
                repaint();
                return;
            }
        }
        
        snake.add(0, newHead);
        
        // Check if snake ate food
        if (newHead.x == food.x && newHead.y == food.y && newHead.z == food.z) {
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            repaint();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_SPACE && !running) {
            initGame();
            repaint();
        }
        
        if (key == KeyEvent.VK_H) {
            showControls = !showControls;
            repaint();
        }
        
        if (running) {
            // X-axis movement (LEFT/RIGHT)
            if (key == KeyEvent.VK_RIGHT && directionX != -1) {
                directionX = 1;
                directionY = 0;
                directionZ = 0;
            } else if (key == KeyEvent.VK_LEFT && directionX != 1) {
                directionX = -1;
                directionY = 0;
                directionZ = 0;
            }
            // Y-axis movement (UP/DOWN)
            else if (key == KeyEvent.VK_UP && directionY != 1) {
                directionX = 0;
                directionY = -1;
                directionZ = 0;
            } else if (key == KeyEvent.VK_DOWN && directionY != -1) {
                directionX = 0;
                directionY = 1;
                directionZ = 0;
            }
            // Z-axis movement (W/S)
            else if (key == KeyEvent.VK_W && directionZ != -1) {
                directionX = 0;
                directionY = 0;
                directionZ = 1;
            } else if (key == KeyEvent.VK_S && directionZ != 1) {
                directionX = 0;
                directionY = 0;
                directionZ = -1;
            }
        }
    }
    
    // Mouse control implementations
    @Override
    public void mousePressed(MouseEvent e) {
        lastMousePoint = e.getPoint();
        mouseDragging = true;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDragging = false;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseDragging) {
            Point current = e.getPoint();
            int dx = current.x - lastMousePoint.x;
            int dy = current.y - lastMousePoint.y;
            
            // Adjust rotation based on mouse movement
            rotationAngleX += dx * 0.01;
            rotationAngleY += dy * 0.01;
            
            lastMousePoint = current;
            repaint();
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {}
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    private static class Point3D {
        int x, y, z;
        
        Point3D(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Snake Game - Mouse Controlled Cube");
        Snake3DGame game = new Snake3DGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}