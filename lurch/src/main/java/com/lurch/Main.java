package com.lurch;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.lurch.time.Timer;
import com.lurch.display.Quad;
import com.lurch.core.Window;
import com.lurch.core.Shader;

import java.util.Random;

public class Main {
    private Window window;
    private Shader shader;
    private Timer timer;
    private Quad quad;
    private GameState state = GameState.START;

    private Snake snake;
    private Vector2f food;
    private Random random = new Random();
    private int score = 0;
    private static int highScore = 0;
    private boolean directionChanged = false;

    private final int gridWidth = 40;
    private final int gridHeight = 30;
    private final float VP = 20f; // Virtual Pixel

    private KeyInput keyInput;

    enum GameState {
        START, PLAYING, GAME_OVER
    }

    public Main() {
        window = new Window(gridWidth * (int)VP, gridHeight * (int)VP, "Snake");
        keyInput = new KeyInput(window.getHandle());

        shader = new Shader("color");
        shader.compile();
        timer = new Timer();
        timer.setUPS(5);
        timer.start();
        quad = new Quad();

        shader.bind();
        shader.setUniformMatrix4f("u_projection", window.getOrtho());
        shader.unbind();
    }

    public void run() 
    {
        System.out.println("Press SPACE to start");
        init();
        loop();
        free();
    }

    private void init() {
        snake = new Snake();
        spawnFood();
        score = 0;
        state = GameState.START;
    }

    private void loop() {
        while (!window.shouldClose()) {
            window.clear();
            keyInput.update(); 

            handleInput();
            timer.update();
            int updates = timer.getAccumulatedUpdates();

            if (state == GameState.PLAYING) {
                for (int i = 0; i < updates; i++) {
                    update();
                }
            }

            timer.consume();
            render();
            window.refresh();
        }
    }

    private void free() {
        window.delete();
        shader.delete();
    }

    private void update() {
        snake.move();
        directionChanged = false;

        if (snake.getHead().equals(food)) {
            snake.grow();
            score += 10;
            highScore = Math.max(highScore, score);
            System.out.println("Score: " + score + " | High Score: " + highScore);
            spawnFood();
        }

        if (snake.checkSelfCollision() || snake.outOfBounds(gridWidth, gridHeight)) {
            state = GameState.GAME_OVER;
        }
    }

    private void handleInput() {
        if (keyInput.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            window.close();
        }

        if (state == GameState.START) {
            if (keyInput.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
                state = GameState.PLAYING;
            }
            return;
        }

        if (state == GameState.GAME_OVER) {
            if (keyInput.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
                init();
                state = GameState.PLAYING;
            }
            return;
        }

        if (!directionChanged) {
            if (keyInput.isKeyPressed(GLFW.GLFW_KEY_UP)) {
                snake.setDirection(Direction.UP);
                directionChanged = true;
            } else if (keyInput.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
                snake.setDirection(Direction.DOWN);
                directionChanged = true;
            }if (keyInput.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
                snake.setDirection(Direction.LEFT);
                directionChanged = true;
            } else if (keyInput.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
                snake.setDirection(Direction.RIGHT);
                directionChanged = true;
            }
        }
    }


    private void render() 
    {
        shader.bind();

        // Draw grid background
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Matrix4f model = new Matrix4f().translate((x+0.5f) * VP, (y+0.5f) * VP, 0).scale(VP/2f);
                shader.setUniformMatrix4f("u_model", model);
                shader.setUniform3f("u_color", new Vector3f(0.1f, 0.1f, 0.1f));
                quad.render();
            }
        }

        // Draw snake
        int index = 0;
        for (Vector2f segment : snake.getSegments()) {
            Matrix4f model = new Matrix4f().translate((segment.x+0.5f) * VP, (segment.y+0.5f) * VP, 0).scale(VP/2f);
            shader.setUniformMatrix4f("u_model", model);
            if (index == 0) {
                shader.setUniform3f("u_color", new Vector3f(0.0f, 1.0f, 0.0f)); // Head
            } else {
                shader.setUniform3f("u_color", new Vector3f(0.0f, 0.8f, 0.0f)); // Body
            }
            quad.render();
            index++;
        }

        // Draw food
        Matrix4f model = new Matrix4f().translate((food.x+0.5f) * VP, (food.y+0.5f) * VP, 0).scale(VP/2f);
        shader.setUniformMatrix4f("u_model", model);
        shader.setUniform3f("u_color", new Vector3f(1.0f, 0.0f, 0.0f));
        quad.render();

        shader.unbind();
    }

    private void spawnFood() {
        do {
            food = new Vector2f(random.nextInt(gridWidth), random.nextInt(gridHeight));
        } while (snake.getSegments().contains(food)); // Ensure food doesn't spawn on snake
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
