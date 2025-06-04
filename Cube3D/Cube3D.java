import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Cube3D extends JPanel implements ActionListener {
    private final double[][] nodes = {
        {-50, -50, -50}, {-50, -50, 50}, {-50, 50, -50}, {-50, 50, 50},
        {50, -50, -50}, {50, -50, 50}, {50, 50, -50}, {50, 50, 50}
    };
    
    private final int[][] edges = {
        {0, 1}, {1, 3}, {3, 2}, {2, 0}, // back face
        {4, 5}, {5, 7}, {7, 6}, {6, 4}, // front face
        {0, 4}, {1, 5}, {2, 6}, {3, 7} // connecting edges
    };
    
    private double angleX = 0;
    private double angleY = 0;
    private final double angleZ = 0;
    
    public Cube3D() {
        Timer timer = new Timer(50, this);
        timer.start();
    }
    
    private double[][] rotateX(double[][] nodes, double angle) {
        double[][] rotated = new double[nodes.length][3];
        for (int i = 0; i < nodes.length; i++) {
            double y = nodes[i][1];
            double z = nodes[i][2];
            rotated[i][0] = nodes[i][0];
            rotated[i][1] = y * Math.cos(angle) - z * Math.sin(angle);
            rotated[i][2] = y * Math.sin(angle) + z * Math.cos(angle);
        }
        return rotated;
    }
    
    private double[][] rotateY(double[][] nodes, double angle) {
        double[][] rotated = new double[nodes.length][3];
        for (int i = 0; i < nodes.length; i++) {
            double x = nodes[i][0];
            double z = nodes[i][2];
            rotated[i][0] = x * Math.cos(angle) + z * Math.sin(angle);
            rotated[i][1] = nodes[i][1];
            rotated[i][2] = -x * Math.sin(angle) + z * Math.cos(angle);
        }
        return rotated;
    }
    
    private double[][] rotateZ(double[][] nodes, double angle) {
        double[][] rotated = new double[nodes.length][3];
        for (int i = 0; i < nodes.length; i++) {
            double x = nodes[i][0];
            double y = nodes[i][1];
            rotated[i][0] = x * Math.cos(angle) - y * Math.sin(angle);
            rotated[i][1] = x * Math.sin(angle) + y * Math.cos(angle);
            rotated[i][2] = nodes[i][2];
        }
        return rotated;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Apply rotations
        double[][] rotated = rotateX(nodes, angleX);
        rotated = rotateY(rotated, angleY);
        rotated = rotateZ(rotated, angleZ);
        
        // Project 3D to 2D
        int[][] projected = new int[rotated.length][2];
        for (int i = 0; i < rotated.length; i++) {
            double scale = 200 / (200 + rotated[i][2]); // simple perspective
            projected[i][0] = (int) (rotated[i][0] * scale) + getWidth() / 2;
            projected[i][1] = (int) (rotated[i][1] * scale) + getHeight() / 2;
        }
        
        // Draw edges
        g2d.setColor(Color.BLUE);
        for (int[] edge : edges) {
            int[] node1 = projected[edge[0]];
            int[] node2 = projected[edge[1]];
            g2d.drawLine(node1[0], node1[1], node2[0], node2[1]);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        angleX += 0.01;
        angleY += 0.02;
        repaint();
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("3D Cube in AWT");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(new Cube3D());
        frame.setVisible(true);
    }
}