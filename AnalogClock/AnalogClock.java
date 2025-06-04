import java.awt.*;
import java.util.Calendar;
import javax.swing.*;

public class AnalogClock extends JFrame {
    private ClockPanel clockPanel;

    public AnalogClock() {
        setTitle("Analog Clock");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        clockPanel = new ClockPanel();
        add(clockPanel);
        
        // Update the clock every second
        Timer timer = new Timer(1000, e -> clockPanel.repaint());
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AnalogClock clock = new AnalogClock();
            clock.setVisible(true);
        });
    }
}

class ClockPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Get current time
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        
        // Convert to 12-hour format for analog clock
        int hour12 = hour % 12;
        
        // Calculate angles for each hand
        double hourAngle = Math.toRadians((hour12 * 30) + (minute * 0.5) - 90);
        double minuteAngle = Math.toRadians((minute * 6) + (second * 0.1) - 90);
        double secondAngle = Math.toRadians((second * 6) - 90);
        
        // Center and radius
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 10;
        
        // Draw clock face
        g.setColor(Color.WHITE);
        g.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        g.setColor(Color.BLACK);
        g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        
        // Draw hour markers
        for (int i = 1; i <= 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            int x = centerX + (int)((radius - 20) * Math.cos(angle));
            int y = centerY + (int)((radius - 20) * Math.sin(angle));
            g.drawString(Integer.toString(i), x - 5, y + 5);
        }
        
        // Draw hour hand
        int hourHandLength = radius / 2;
        int hourX = centerX + (int)(hourHandLength * Math.cos(hourAngle));
        int hourY = centerY + (int)(hourHandLength * Math.sin(hourAngle));
        g.setColor(Color.BLACK);
        g.drawLine(centerX, centerY, hourX, hourY);
        
        // Draw minute hand
        int minuteHandLength = radius * 3 / 4;
        int minuteX = centerX + (int)(minuteHandLength * Math.cos(minuteAngle));
        int minuteY = centerY + (int)(minuteHandLength * Math.sin(minuteAngle));
        g.setColor(Color.BLUE);
        g.drawLine(centerX, centerY, minuteX, minuteY);
        
        // Draw second hand
        int secondHandLength = radius - 10;
        int secondX = centerX + (int)(secondHandLength * Math.cos(secondAngle));
        int secondY = centerY + (int)(secondHandLength * Math.sin(secondAngle));
        g.setColor(Color.RED);
        g.drawLine(centerX, centerY, secondX, secondY);
        
        // Draw center dot (last to appear on top)
        g.setColor(Color.RED);
        g.fillOval(centerX - 5, centerY - 5, 10, 10);
    }
}