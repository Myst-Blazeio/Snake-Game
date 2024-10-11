package SnakeGamePackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GameBoard extends JPanel implements ActionListener {

    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int CELL_SIZE = 20;

    private Timer timer;
    private boolean gameRunning;
    private Snake snake;
    private Point food;
    private ArrayList<Point> walls;
    private int score;
    private Random random;
    private int foodEaten = 0;

    public GameBoard() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new SnakeController());

        snake = new Snake(this); // Pass the GameBoard reference
        walls = new ArrayList<>();
        random = new Random();
        startGame();
    }

    // Getter methods to expose private fields
    public int getBoardWidth() {
        return BOARD_WIDTH;
    }

    public int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    public int getCellSize() {
        return CELL_SIZE;
    }

    private void startGame() {
        score = 0;
        snake.reset();
        walls.clear();
        generateFood();
        gameRunning = true;
        timer = new Timer(80, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the snake
        g.setColor(Color.GREEN);
        for (Point p : snake.getBody()) {
            g.fillRect(p.x * CELL_SIZE, p.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Draw the food
        if (food != null) {
            g.setColor(Color.RED);
            g.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Draw the walls
        g.setColor(Color.CYAN);
        for (Point wall : walls) {
            g.fillRect(wall.x * CELL_SIZE, wall.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Draw the score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 10);

        // Show game over screen if game ends
        if (!gameRunning) {
            showGameOver(g);
        }
    }

    private void showGameOver(Graphics g) {
        String message = "Game Over!";
        String scoreMessage = "Final Score: " + score;

        Font font = new Font("Helvetica", Font.BOLD, 30);
        FontMetrics metrics = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(message, (BOARD_WIDTH - metrics.stringWidth(message)) / 2, BOARD_HEIGHT / 2 - 50);
        g.drawString(scoreMessage, (BOARD_WIDTH - metrics.stringWidth(scoreMessage)) / 2, BOARD_HEIGHT / 2);

        // Show Play Again and Quit buttons
        JButton playAgainButton = new JButton("Play Again");
        JButton quitButton = new JButton("Quit");

        playAgainButton.setBounds(BOARD_WIDTH / 2 - 80, BOARD_HEIGHT / 2 + 30, 100, 30);
        quitButton.setBounds(BOARD_WIDTH / 2 + 20, BOARD_HEIGHT / 2 + 30, 100, 30);

        add(playAgainButton);
        add(quitButton);

        playAgainButton.addActionListener(e -> resetGame());
        quitButton.addActionListener(e -> System.exit(0));

        setLayout(null);
    }

    private void resetGame() {
        gameRunning = true;
        foodEaten = 0;
        score = 0;
        snake.reset();
        walls.clear();
        generateFood();
        timer.start();
        removeAll(); // Remove buttons from screen
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            snake.move();

            // Check if snake eats food
            if (snake.getHead().equals(food)) {
                snake.grow();
                score++;
                foodEaten++;
                generateFood();

                // Generate walls after every 2 food items eaten
                if (foodEaten % 2 == 0) {
                    generateWalls();
                }
            }

            // Check if snake hits the wall or itself
            if (snake.checkCollision() || checkWallCollision(snake.getHead())) {
                gameRunning = false;
                timer.stop();
            }
        }
        repaint();
    }

    private void generateFood() {
        int x, y;
        do {
            x = random.nextInt(BOARD_WIDTH / CELL_SIZE);
            y = random.nextInt(BOARD_HEIGHT / CELL_SIZE);
        } while (snake.getBody().contains(new Point(x, y)) || walls.contains(new Point(x, y))); // Avoid spawning on
                                                                                                // snake or walls
        food = new Point(x, y);
    }

    private void generateWalls() {
        int wallType = random.nextInt(4);
        int x, y;
        do {
            x = random.nextInt(BOARD_WIDTH / CELL_SIZE);
            y = random.nextInt(BOARD_HEIGHT / CELL_SIZE);
        } while (snake.getBody().contains(new Point(x, y)) || walls.contains(new Point(x, y))); // Avoid spawning on
                                                                                                // snake or walls

        // Randomly generate 1x1, 1x2, 2x1, or 2x2 walls
        switch (wallType) {
            case 0 -> walls.add(new Point(x, y)); // 1x1 wall
            case 1 -> {
                walls.add(new Point(x, y));
                walls.add(new Point(x, y + 1)); // 1x2 wall
            }
            case 2 -> {
                walls.add(new Point(x, y));
                walls.add(new Point(x + 1, y)); // 2x1 wall
            }
            case 3 -> {
                walls.add(new Point(x, y));
                walls.add(new Point(x + 1, y));
                walls.add(new Point(x, y + 1));
                walls.add(new Point(x + 1, y + 1)); // 2x2 wall
            }
        }
    }

    private boolean checkWallCollision(Point head) {
        return walls.contains(head);
    }

    public boolean isInBounds(Point head) {
        return head.x >= 0 && head.x < BOARD_WIDTH / CELL_SIZE && head.y >= 0 && head.y < BOARD_HEIGHT / CELL_SIZE;
    }

    private class SnakeController extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    if (snake.getDirection() != Direction.DOWN) {
                        snake.setDirection(Direction.UP);
                    }
                    break;
                case KeyEvent.VK_A:
                    if (snake.getDirection() != Direction.RIGHT) {
                        snake.setDirection(Direction.LEFT);
                    }
                    break;
                case KeyEvent.VK_S:
                    if (snake.getDirection() != Direction.UP) {
                        snake.setDirection(Direction.DOWN);
                    }
                    break;
                case KeyEvent.VK_D:
                    if (snake.getDirection() != Direction.LEFT) {
                        snake.setDirection(Direction.RIGHT);
                    }
                    break;
            }
        }
    }
}
