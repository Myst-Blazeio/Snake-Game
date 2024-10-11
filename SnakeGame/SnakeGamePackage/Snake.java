package SnakeGamePackage;

import java.awt.*;
import java.util.LinkedList;

public class Snake {
    private LinkedList<Point> body;
    private Direction direction;
    private GameBoard gameBoard;

    public Snake(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
        body = new LinkedList<>();
        direction = Direction.RIGHT;
        reset();
    }

    public void reset() {
        body.clear();
        body.add(new Point(5, 5)); // Starting position
        direction = Direction.RIGHT;
    }

    public void move() {
        Point head = getHead();
        Point newHead = new Point(head);

        switch (direction) {
            case UP -> newHead.y--;
            case DOWN -> newHead.y++;
            case LEFT -> newHead.x--;
            case RIGHT -> newHead.x++;
        }

        body.addFirst(newHead);
        body.removeLast();
    }

    public void grow() {
        Point head = getHead();
        Point newHead = new Point(head);

        switch (direction) {
            case UP -> newHead.y--;
            case DOWN -> newHead.y++;
            case LEFT -> newHead.x--;
            case RIGHT -> newHead.x++;
        }

        body.addFirst(newHead); // Add new head without removing tail
    }

    public Point getHead() {
        return body.getFirst();
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean checkCollision() {
        Point head = getHead();
        // Check if the snake hits itself
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        // Check if the snake hits the boundary of the game board
        return checkScreenBoundaryCollision(head);
    }

    private boolean checkScreenBoundaryCollision(Point head) {
        return head.x < 0 || head.x >= gameBoard.getBoardWidth() / gameBoard.getCellSize() ||
                head.y < 0 || head.y >= gameBoard.getBoardHeight() / gameBoard.getCellSize();
    }
}
