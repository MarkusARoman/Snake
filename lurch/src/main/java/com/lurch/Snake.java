package com.lurch;

import org.joml.Vector2f;

import java.util.LinkedList;
import java.util.List;

public class Snake {
    private LinkedList<Vector2f> segments = new LinkedList<>();
    private Direction direction = Direction.RIGHT;
    private boolean grow = false;

    public Snake() {
        segments.add(new Vector2f(5, 5));
    }

    public void move() {
        Vector2f head = new Vector2f(segments.getFirst());
        switch (direction) {
            case UP -> head.y -= 1;
            case DOWN -> head.y += 1;
            case LEFT -> head.x -= 1;
            case RIGHT -> head.x += 1;
        }
        segments.addFirst(head);
        if (!grow) segments.removeLast();
        grow = false;
    }

    public void grow() {
        grow = true;
    }

    public void setDirection(Direction dir) {
        if (!opposite(dir)) {
            direction = dir;
        }
    }

    public boolean opposite(Direction dir)
    {
        return (
            (dir == Direction.UP    && direction == Direction.DOWN  ) ||
            (dir == Direction.DOWN  && direction == Direction.UP    ) ||
            (dir == Direction.LEFT  && direction == Direction.RIGHT ) ||
            (dir == Direction.RIGHT && direction == Direction.LEFT  )
        );
    }

    public Vector2f getHead() {
        return segments.getFirst();
    }

    public List<Vector2f> getSegments() {
        return segments;
    }

    public boolean checkSelfCollision() {
        Vector2f head = getHead();
        for (int i = 1; i < segments.size(); i++) {
            if (segments.get(i).equals(head)) {
                return true;
            }
        }
        return false;
    }

    public boolean outOfBounds(int width, int height) {
        Vector2f head = getHead();
        return head.x < 0 || head.x >= width || head.y < 0 || head.y >= height;
    }
}
