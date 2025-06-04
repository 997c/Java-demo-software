import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Sphere3D extends JPanel {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    
    private BufferedImage buffer;
    private List<Point3D> points;
    private double rotationX = 0;
    private double rotationY = 0;
    
    public Sphere3D() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        // Generate sphere points
        points = generateSphere(150, 30, 30);
        
        // Add mouse listener for rotation
        addMouseMotionListener(new MouseMotionAdapter() {
            private int lastX, lastY;
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastX;
                int dy = e.getY() - lastY;
                
                rotationY += dx * 0.01;
                rotationX += dy * 0.01;
                
                lastX = e.getX();
                lastY = e.getY();
                
                repaint();
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });
    }
    
    private List<Point3D> generateSphere(double radius, int slices, int stacks) {
        List<Point3D> points = new ArrayList<>();
        
        for (int i = 0; i <= stacks; i++) {
            double phi = Math.PI * i / stacks;
            double y = radius * Math.cos(phi);
            double r = radius * Math.sin(phi);
            
            for (int j = 0; j <= slices; j++) {
                double theta = 2 * Math.PI * j / slices;
                double x = r * Math.cos(theta);
                double z = r * Math.sin(theta);
                
                points.add(new Point3D(x, y, z));
            }
        }
        
        return points;
    }
    
    private Point project(Point3D p) {
        // Apply rotations
        Point3D rotated = rotateX(p, rotationX);
        rotated = rotateY(rotated, rotationY);
        
        // Simple perspective projection
        double distance = 1000;
        double z = rotated.z + 500;
        double factor = distance / z;
        
        int x2d = (int) (rotated.x * factor) + WIDTH / 2;
        int y2d = (int) (rotated.y * factor) + HEIGHT / 2;
        
        return new Point(x2d, y2d);
    }
    
    private Point3D rotateX(Point3D p, double angle) {
        double y = p.y * Math.cos(angle) - p.z * Math.sin(angle);
        double z = p.y * Math.sin(angle) + p.z * Math.cos(angle);
        return new Point3D(p.x, y, z);
    }
    
    private Point3D rotateY(Point3D p, double angle) {
        double x = p.x * Math.cos(angle) + p.z * Math.sin(angle);
        double z = -p.x * Math.sin(angle) + p.z * Math.cos(angle);
        return new Point3D(x, p.y, z);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Clear buffer
        Graphics2D g2d = buffer.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Draw sphere points
        g2d.setColor(Color.WHITE);
        for (Point3D p : points) {
            Point point2d = project(p);
            if (point2d.x >= 0 && point2d.x < WIDTH && point2d.y >= 0 && point2d.y < HEIGHT) {
                // Simple shading based on z-coordinate
                int intensity = (int) (255 * (p.z + 150) / 300);
                intensity = Math.max(0, Math.min(255, intensity));
                g2d.setColor(new Color(intensity, intensity, intensity));
                g2d.fillRect(point2d.x, point2d.y, 2, 2);
            }
        }
        
        // Draw buffer to screen
        g.drawImage(buffer, 0, 0, null);
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Sphere without JavaFX");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new Sphere3D());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static class Point3D {
        double x, y, z;
        
        Point3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}