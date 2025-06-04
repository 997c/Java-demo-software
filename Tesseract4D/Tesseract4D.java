import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Tesseract4D extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int SIZE = 100;
    private static final double ROTATION_SPEED = 0.01;
    
    private double angleXY = 0;
    private double angleXZ = 0;
    private double angleXW = 0;
    private double angleYW = 0;
    private double angleZW = 0;
    
    // 4D vertices of a tesseract (-1 to 1 in each dimension)
    private double[][] vertices = {
        {-1, -1, -1, -1}, {1, -1, -1, -1}, {1, 1, -1, -1}, {-1, 1, -1, -1},
        {-1, -1, 1, -1}, {1, -1, 1, -1}, {1, 1, 1, -1}, {-1, 1, 1, -1},
        {-1, -1, -1, 1}, {1, -1, -1, 1}, {1, 1, -1, 1}, {-1, 1, -1, 1},
        {-1, -1, 1, 1}, {1, -1, 1, 1}, {1, 1, 1, 1}, {-1, 1, 1, 1}
    };
    
    // Edges connecting the vertices
    private int[][] edges = {
        {0, 1}, {1, 2}, {2, 3}, {3, 0}, // Bottom face
        {4, 5}, {5, 6}, {6, 7}, {7, 4}, // Top face
        {0, 4}, {1, 5}, {2, 6}, {3, 7}, // Connecting edges
        {8, 9}, {9, 10}, {10, 11}, {11, 8}, // Bottom face (4D)
        {12, 13}, {13, 14}, {14, 15}, {15, 12}, // Top face (4D)
        {8, 12}, {9, 13}, {10, 14}, {11, 15}, // Connecting edges (4D)
        {0, 8}, {1, 9}, {2, 10}, {3, 11}, // Hyper-edges
        {4, 12}, {5, 13}, {6, 14}, {7, 15}  // Hyper-edges
    };
    
    public Tesseract4D() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        
        // Animation timer
        Timer timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                angleXY += ROTATION_SPEED;
                angleXW += ROTATION_SPEED * 0.5;
                angleZW += ROTATION_SPEED * 0.3;
                repaint();
            }
        });
        timer.start();
    }
    
    // Project 4D point to 3D (with perspective)
    private double[] project4Dto3D(double x, double y, double z, double w) {
        // 4D rotation matrices applied here
        // XY rotation
        double xyX = x * Math.cos(angleXY) - y * Math.sin(angleXY);
        double xyY = x * Math.sin(angleXY) + y * Math.cos(angleXY);
        double xyZ = z;
        double xyW = w;
        
        // XW rotation
        double xwX = xyX * Math.cos(angleXW) - xyW * Math.sin(angleXW);
        double xwY = xyY;
        double xwZ = xyZ;
        double xwW = xyX * Math.sin(angleXW) + xyW * Math.cos(angleXW);
        
        // ZW rotation
        double zwX = xwX;
        double zwY = xwY;
        double zwZ = xwZ * Math.cos(angleZW) - xwW * Math.sin(angleZW);
        double zwW = xwZ * Math.sin(angleZW) + xwW * Math.cos(angleZW);
        
        // Perspective projection from 4D to 3D
        double distance = 5;
        double perspective = distance / (distance - zwW);
        
        return new double[]{
            zwX * perspective,
            zwY * perspective,
            zwZ * perspective
        };
    }
    
    // Project 3D point to 2D (with perspective)
    private Point project3Dto2D(double x, double y, double z) {
        double distance = 3;
        double perspective = distance / (distance - z);
        int x2d = (int) (x * perspective * SIZE) + WIDTH / 2;
        int y2d = (int) (y * perspective * SIZE) + HEIGHT / 2;
        return new Point(x2d, y2d);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Project all vertices to 2D
        ArrayList<Point> points2D = new ArrayList<>();
        for (double[] vertex : vertices) {
            double[] vertex3D = project4Dto3D(vertex[0], vertex[1], vertex[2], vertex[3]);
            Point point2D = project3Dto2D(vertex3D[0], vertex3D[1], vertex3D[2]);
            points2D.add(point2D);
        }
        
        // Draw edges
        g2d.setColor(Color.WHITE);
        for (int[] edge : edges) {
            Point p1 = points2D.get(edge[0]);
            Point p2 = points2D.get(edge[1]);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        
        // Draw vertices
        g2d.setColor(Color.RED);
        for (Point p : points2D) {
            g2d.fillOval(p.x - 5, p.y - 5, 10, 10);
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("4D Tesseract Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Tesseract4D());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}