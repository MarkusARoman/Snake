package com.lurch;

import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeyInput {
    private final long windowHandle;
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Set<Integer> justPressedKeys = new HashSet<>();
    private final Set<Integer> justReleasedKeys = new HashSet<>();

    public KeyInput(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    private static final int[] RELEVANT_KEYS = {
        GLFW.GLFW_KEY_UP,
        GLFW.GLFW_KEY_DOWN,
        GLFW.GLFW_KEY_LEFT,
        GLFW.GLFW_KEY_RIGHT,
        GLFW.GLFW_KEY_SPACE,
        GLFW.GLFW_KEY_ESCAPE
    };

    public void update() {
        justPressedKeys.clear();
        justReleasedKeys.clear();

        for (int key : RELEVANT_KEYS) 
        {
            boolean isDown = GLFW.glfwGetKey(windowHandle, key) == GLFW.GLFW_PRESS;
            boolean wasDown = pressedKeys.contains(key);

            if (isDown && !wasDown) 
            {
                justPressedKeys.add(key);
                pressedKeys.add(key);
            } 
            else if (!isDown && wasDown) 
            {
                justReleasedKeys.add(key);
                pressedKeys.remove(key);
            }
        }
    }


    public boolean isKeyDown(int key) 
    {
        return pressedKeys.contains(key);
    }

    public boolean isKeyPressed(int key) 
    {
        return justPressedKeys.contains(key);
    }

    public boolean isKeyReleased(int key) 
    {
        return justReleasedKeys.contains(key);
    }
}

