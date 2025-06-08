package org.example;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Snake3D extends Application {
    private static int WIDTH = 800;
    private static int HEIGHT = 600;
    private static final int DEPTH = 800;
    private static final int CELL_SIZE = 30;
    private static final int BOARD_SIZE = 400;
    private static final long FRAME_DELAY = 100_000_000;
    
    private Group root = new Group();
    private Group gameGroup = new Group();
    private PerspectiveCamera camera;
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    
    private List<Box> snake = new ArrayList<>();
    private Sphere food;
    private Point3D direction = new Point3D(1, 0, 0);
    private AnimationTimer gameLoop;
    private Text scoreText;
    private Text gameOverText;
    private int score = 0;
    private boolean gameRunning = false;

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(root, WIDTH, HEIGHT, true);
        scene.setFill(Color.BLACK);
        
        setupCamera();
        createStartScreen();
        setupMouseControls(scene);
        
        primaryStage.setTitle("3D Snake Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void createStartScreen() {
        root.getChildren().clear();
        
        VBox menu = new VBox(20);
        menu.setAlignment(Pos.CENTER);
        menu.setTranslateX(WIDTH/2 - 100);
        menu.setTranslateY(HEIGHT/2 - 100);
        
        String style = "-fx-text-fill: white; -fx-font-weight: bold;";
        Label resolutionLabel = new Label("Select Resolution:");
        resolutionLabel.setStyle(style);
        
        ComboBox<String> resolutionBox = new ComboBox<>();
        resolutionBox.getItems().addAll(
            "800x600",
            "1024x768", 
            "1280x720",
            "1920x1080"
        );
        resolutionBox.setValue(WIDTH + "x" + HEIGHT);
        
        CheckBox fullscreenBox = new CheckBox("Fullscreen");
        
        Button startButton = new Button("Start Game");
        startButton.setFont(Font.font(20));
        startButton.setStyle("-fx-effect: null; -fx-background-radius: 0;");
        startButton.setOnAction(e -> {
            String[] res = resolutionBox.getValue().split("x");
            WIDTH = Integer.parseInt(res[0]);
            HEIGHT = Integer.parseInt(res[1]);
            startGame();
            if (fullscreenBox.isSelected()) {
                Stage stage = (Stage) startButton.getScene().getWindow();
                stage.setFullScreen(true);
            }
        });
        
        menu.getChildren().addAll(
            resolutionLabel,
            resolutionBox,
            fullscreenBox,
            startButton
        );
        resolutionBox.setStyle(style);
        fullscreenBox.setStyle(style);
        startButton.setStyle(style);
        
        root.getChildren().add(menu);
    }

    private void startGame() {
        gameRunning = true;
        score = 0;
        root.getChildren().clear();
        root.getChildren().add(gameGroup);
        setupScene();
    }

    private void setupCamera() {
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(-2000);
        camera.setTranslateX(300);
        camera.setTranslateY(-400);
        root.getChildren().add(camera);
    }

    private void setupScene() {
        gameGroup.getChildren().clear();
        snake.clear();
        
        Box board = new Box(BOARD_SIZE, 20, BOARD_SIZE);
        board.setTranslateY(0);
        board.setMaterial(new javafx.scene.paint.PhongMaterial(Color.LIMEGREEN));
        gameGroup.getChildren().add(board);
        
        gameGroup.setTranslateX(150);
        gameGroup.setTranslateY(200);
        gameGroup.setTranslateZ(0);
        
        createWalls();
        
        Box snakeHead = new Box(CELL_SIZE, CELL_SIZE, CELL_SIZE);
        snakeHead.setMaterial(new javafx.scene.paint.PhongMaterial(Color.DARKGREEN));
        snakeHead.setTranslateX(0);
        snakeHead.setTranslateY(CELL_SIZE/2);
        snakeHead.setTranslateZ(0);
        snake.add(snakeHead);
        gameGroup.getChildren().add(snakeHead);
        
        scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font(20));
        scoreText.setTranslateX(20);
        scoreText.setTranslateY(30);
        scoreText.setFill(Color.WHITE);
        gameGroup.getChildren().add(scoreText);
        
        spawnFood();
        
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (gameRunning && now - lastUpdate >= FRAME_DELAY) {
                    moveSnake();
                    checkCollisions();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void createWalls() {
        int thickness = 10;
        
        Box bottomWall = new Box(BOARD_SIZE, thickness, BOARD_SIZE);
        bottomWall.setTranslateY(-thickness/2);
        bottomWall.setMaterial(new javafx.scene.paint.PhongMaterial(Color.LIGHTCYAN));
        gameGroup.getChildren().add(bottomWall);
        
        Box[] sideWalls = {
            new Box(thickness, thickness, BOARD_SIZE),
            new Box(thickness, thickness, BOARD_SIZE),
            new Box(BOARD_SIZE, thickness, thickness),
            new Box(BOARD_SIZE, thickness, thickness)
        };
        
        double halfSize = BOARD_SIZE/2;
        sideWalls[0].setTranslateX(-halfSize - thickness/2);
        sideWalls[1].setTranslateX(halfSize + thickness/2);
        sideWalls[2].setTranslateZ(-halfSize - thickness/2);
        sideWalls[3].setTranslateZ(halfSize + thickness/2);
        
        for (Box wall : sideWalls) {
            wall.setMaterial(new javafx.scene.paint.PhongMaterial(Color.LIGHTCYAN));
            gameGroup.getChildren().add(wall);
        }
    }

    private void setupMouseControls(Scene scene) {
        scene.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                anchorX = event.getSceneX();
                anchorY = event.getSceneY();
                anchorAngleX = rotateX.getAngle();
                anchorAngleY = rotateY.getAngle();
            }
        });

        scene.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()) * 0.1);
                rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()) * 0.1);
            }
        });

        scene.setOnScroll(event -> {
            double zoomFactor = 1.05;
            double deltaZ = event.getDeltaY() > 0 ? 
                camera.getTranslateZ() * zoomFactor :
                camera.getTranslateZ() / zoomFactor;
            camera.setTranslateZ(Math.min(-500, Math.max(-3000, deltaZ)));
        });

        scene.setOnMouseClicked(event -> {
            if (event.isSecondaryButtonDown()) {
                rotateX.setAngle(0);
                rotateY.setAngle(0);
                camera.setTranslateZ(-2000);
                camera.setTranslateX(300);
                camera.setTranslateY(-400);
            }
        });
        
        scene.setOnKeyPressed(event -> {
            switch(event.getCode()) {
                case W:
                case UP:
                    direction = new Point3D(0, 0, -1);
                    break;
                case S:
                case DOWN:
                    direction = new Point3D(0, 0, 1);
                    break;
                case A:
                case LEFT:
                    direction = new Point3D(-1, 0, 0);
                    break;
                case D:
                case RIGHT:
                    direction = new Point3D(1, 0, 0);
                    break;
                case R:
                    if (!gameRunning) {
                        startGame();
                    }
                    break;
            }
        });
        
        root.getTransforms().addAll(rotateX, rotateY);
    }
    
    private void moveSnake() {
        for (int i = snake.size() - 1; i > 0; i--) {
            Box current = snake.get(i);
            Box next = snake.get(i-1);
            current.setTranslateX(next.getTranslateX());
            current.setTranslateY(next.getTranslateY());
            current.setTranslateZ(next.getTranslateZ());
        }
        
        Box head = snake.get(0);
        head.setTranslateX(head.getTranslateX() + direction.getX() * CELL_SIZE);
        head.setTranslateY(head.getTranslateY() + direction.getY() * CELL_SIZE);
        head.setTranslateZ(head.getTranslateZ() + direction.getZ() * CELL_SIZE);
    }
    
    private void spawnFood() {
        if (food != null) {
            gameGroup.getChildren().remove(food);
        }
        
        Sphere egg = new Sphere(CELL_SIZE/2);
        egg.setScaleX(1.3);
        egg.setScaleY(1.5);
        egg.setScaleZ(1.3);
        egg.setMaterial(new javafx.scene.paint.PhongMaterial(Color.CRIMSON));
        food = egg;
        
        Random rand = new Random();
        int halfCells = (BOARD_SIZE/CELL_SIZE)/2;
        food.setTranslateX((rand.nextInt(halfCells*2) - halfCells) * CELL_SIZE);
        food.setTranslateY(CELL_SIZE/2);
        food.setTranslateZ((rand.nextInt(halfCells*2) - halfCells) * CELL_SIZE);
        
        gameGroup.getChildren().add(food);
    }
    
    private void checkCollisions() {
        Box head = snake.get(0);
        double halfSize = BOARD_SIZE/2;
        double headPosX = head.getTranslateX();
        double headPosZ = head.getTranslateZ();
        
        if (Math.abs(headPosX) >= halfSize - CELL_SIZE/2 ||
            Math.abs(headPosZ) >= halfSize - CELL_SIZE/2) {
            gameOver();
            return;
        }
        
        if (head.getBoundsInParent().intersects(food.getBoundsInParent())) {
            score++;
            scoreText.setText("Score: " + score);
            growSnake();
            spawnFood();
        }
    }
    
    private void growSnake() {
        Box last = snake.get(snake.size() - 1);
        Box newSegment = new Box(CELL_SIZE, CELL_SIZE, CELL_SIZE);
        newSegment.setMaterial(new javafx.scene.paint.PhongMaterial(Color.GREEN));
        newSegment.setTranslateX(last.getTranslateX());
        newSegment.setTranslateY(last.getTranslateY());
        newSegment.setTranslateZ(last.getTranslateZ());
        snake.add(newSegment);
        gameGroup.getChildren().add(newSegment);
    }
    
    private void gameOver() {
        gameRunning = false;
        gameLoop.stop();
        
        gameOverText = new Text("GAME OVER\nScore: " + score + "\nPress R to restart");
        gameOverText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gameOverText.setFill(Color.PURPLE);
        gameOverText.setTranslateX(WIDTH/2 - 100);
        gameOverText.setTranslateY(HEIGHT/2);
        
        root.getChildren().add(gameOverText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}