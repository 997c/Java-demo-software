import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Painting App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            PaintingApp paintingApp = new PaintingApp();
            frame.add(paintingApp, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            frame.add(controlPanel, BorderLayout.SOUTH);

            JButton redButton = new JButton("Red");
            redButton.addActionListener(e -> paintingApp.setColor(Color.RED));
            controlPanel.add(redButton);

            JButton blueButton = new JButton("Blue");
            blueButton.addActionListener(e -> paintingApp.setColor(Color.BLUE));
            controlPanel.add(blueButton);

            JButton greenButton = new JButton("Green");
            greenButton.addActionListener(e -> paintingApp.setColor(Color.GREEN));
            controlPanel.add(greenButton);

            JSlider strokeSlider = new JSlider(1, 10, 1);
            strokeSlider.setMajorTickSpacing(1);
            strokeSlider.setPaintTicks(true);
            strokeSlider.setPaintLabels(true);
            strokeSlider.addChangeListener(e -> {
                int value = strokeSlider.getValue();
                paintingApp.setStroke(new BasicStroke(value));
            });
            controlPanel.add(strokeSlider);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
