import javax.swing.JFrame;

public class DoodleJump {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Doodle Jump");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.add(new GamePanel());
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
