import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Tetris extends JPanel {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 600;
    private static final int BLOCK_SIZE = 30;
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;

    private boolean[][] board = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
    private Tetromino currentPiece;
    private Timer timer;

    public Tetris() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        currentPiece = new Tetromino();
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        timer.start();
    }

    private void update() {
        if (canMove(currentPiece, 0, 1)) {
            currentPiece.move(0, 1);
        } else {
            placePiece();
            clearLines();
            currentPiece = new Tetromino();
        }
        repaint();
    }

    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT && canMove(currentPiece, -1, 0)) {
            currentPiece.move(-1, 0);
        } else if (key == KeyEvent.VK_RIGHT && canMove(currentPiece, 1, 0)) {
            currentPiece.move(1, 0);
        } else if (key == KeyEvent.VK_DOWN) {
            while (canMove(currentPiece, 0, 1)) {
                currentPiece.move(0, 1);
            }
            placePiece();
            clearLines();
            currentPiece = new Tetromino();
        } else if (key == KeyEvent.VK_UP) {
            currentPiece.rotate();
            if (!canMove(currentPiece, 0, 0)) {
                currentPiece.rotateBack();
            }
        }
        repaint();
    }

    private boolean canMove(Tetromino piece, int dx, int dy) {
        for (int i = 0; i < 4; i++) {
            int x = piece.getX() + piece.shape[i][0] + dx;
            int y = piece.getY() + piece.shape[i][1] + dy;
            if (x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT || (y >= 0 && board[y][x])) {
                return false;
            }
        }
        return true;
    }

    private void placePiece() {
        for (int i = 0; i < 4; i++) {
            int x = currentPiece.getX() + currentPiece.shape[i][0];
            int y = currentPiece.getY() + currentPiece.shape[i][1];
            if (y >= 0) {
                board[y][x] = true;
            }
        }
    }

    private void clearLines() {
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (!board[y][x]) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int yy = y; yy > 0; yy--) {
                    System.arraycopy(board[yy - 1], 0, board[yy], 0, BOARD_WIDTH);
                }
                y++;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
        drawPiece(g);
    }

    private void drawBoard(Graphics g) {
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x]) {
                    g.setColor(Color.BLUE);
                    g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }
    }

    private void drawPiece(Graphics g) {
        g.setColor(Color.RED);
        for (int i = 0; i < 4; i++) {
            int x = currentPiece.getX() + currentPiece.shape[i][0];
            int y = currentPiece.getY() + currentPiece.shape[i][1];
            g.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tetris");
        Tetris tetris = new Tetris();
        frame.add(tetris);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    class Tetromino {
        private int[][] shape;
        private int x, y;

        public Tetromino() {
            shape = TetrominoShapes.getRandomShape();
            x = BOARD_WIDTH / 2 - 2;
            y = -2;
        }

        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }

        public void rotate() {
            int[][] rotated = new int[4][2];
            for (int i = 0; i < 4; i++) {
                rotated[i][0] = -shape[i][1];
                rotated[i][1] = shape[i][0];
            }
            shape = rotated;
        }

        public void rotateBack() {
            int[][] rotated = new int[4][2];
            for (int i = 0; i < 4; i++) {
                rotated[i][0] = shape[i][1];
                rotated[i][1] = -shape[i][0];
            }
            shape = rotated;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int[][] getShape() {
            return shape;
        }
    }

    static class TetrominoShapes {
        private static final int[][][] SHAPES = {
            { {0, -1}, {0, 0}, {0, 1}, {0, 2} }, // I
            { {0, -1}, {0, 0}, {1, 0}, {1, 1} }, // Z
            { {1, -1}, {0, -1}, {0, 0}, {1, 0} }, // S
            { {0, -1}, {0, 0}, {0, 1}, {1, 1} }, // L
            { {0, -1}, {0, 0}, {0, 1}, {1, -1} }, // J
            { {0, -1}, {0, 0}, {0, 1}, {1, 0} }, // T
            { {0, 0}, {1, 0}, {0, 1}, {1, 1} }  // O
        };

        public static int[][] getRandomShape() {
            return SHAPES[(int) (Math.random() * SHAPES.length)];
        }
    }
}
